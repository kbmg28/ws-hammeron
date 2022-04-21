package br.com.kbmg.wshammeron.service.impl;

import br.com.kbmg.wshammeron.config.security.SpringSecurityUtil;
import br.com.kbmg.wshammeron.dto.event.EventSimpleDto;
import br.com.kbmg.wshammeron.dto.music.MusicDto;
import br.com.kbmg.wshammeron.dto.music.MusicTopUsedDto;
import br.com.kbmg.wshammeron.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wshammeron.dto.space.overview.MusicOverviewDto;
import br.com.kbmg.wshammeron.enums.MusicStatusEnum;
import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.exception.ForbiddenException;
import br.com.kbmg.wshammeron.exception.ServiceException;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.MusicLink;
import br.com.kbmg.wshammeron.model.Singer;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.repository.MusicRepository;
import br.com.kbmg.wshammeron.repository.projection.MusicOnlyIdAndMusicNameAndSingerNameProjection;
import br.com.kbmg.wshammeron.repository.projection.OverviewProjection;
import br.com.kbmg.wshammeron.service.EventMusicAssociationService;
import br.com.kbmg.wshammeron.service.MusicLinkService;
import br.com.kbmg.wshammeron.service.MusicService;
import br.com.kbmg.wshammeron.service.SingerService;
import br.com.kbmg.wshammeron.service.SpaceService;
import br.com.kbmg.wshammeron.service.UserAppService;
import br.com.kbmg.wshammeron.util.mapper.MusicMapper;
import br.com.kbmg.wshammeron.util.mapper.OverviewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.MUSIC_ALREADY_EXIST_SPACE;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.MUSIC_CANNOT_CHANGE_STATUS;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.MUSIC_NOT_EXIST_SPACE;

@Service
public class MusicServiceImpl extends GenericServiceImpl<Music, MusicRepository> implements MusicService {

    @Autowired
    private MusicMapper musicMapper;

    @Autowired
    private SingerService singerService;

    @Autowired
    private MusicLinkService musicLinkService;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private EventMusicAssociationService eventMusicAssociationService;

    @Autowired
    private OverviewMapper overviewMapper;

    @Override
    public Music createMusic(String spaceId, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto) {
        Space space = spaceService.findByIdValidated(spaceId);
        Singer singer = singerService.findByNameOrCreateIfNotExist(musicWithSingerAndLinksDto.getSinger().getName());
        Music music = musicMapper.toMusic(musicWithSingerAndLinksDto);

        validateAssociationBetweenMusicAndSinger(music, singer, space);
        music.setSpace(space);
        repository.save(music);


        Set<MusicLink> links = musicLinkService.createLinksValidated(music, musicWithSingerAndLinksDto.getLinks());
        music.setMusicLinkList(links);

        return music;
    }

    @Override
    public void updateStatusMusic(String spaceId, String idMusic, MusicStatusEnum newStatus) {
        Music music = this.findMusicValidatingSpace(spaceId, idMusic);
        music.setMusicStatus(newStatus);
        this.update(music);
    }

    @Override
    public void deleteMusic(String spaceId, String idMusic) {
        Music music = this.findMusicValidatingSpace(spaceId, idMusic);

        if (!music.getEventMusicList().isEmpty()) {
            throw new ServiceException(
                    messagesService.get("music.already.used.in.events"));
        }
        Singer singer = music.getSinger();

        musicLinkService.deleteInBatch(music.getMusicLinkList());
        super.delete(music);
        singerService.deleteOrRemoveAssociation(music, singer);
    }

    @Override
    public Music updateMusic(String spaceId, String idMusic, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto) {
        Space space = spaceService.findByIdValidated(spaceId);
        Music musicInDatabase = findBySpaceAndIdValidated(idMusic, space);
        Music musicUpdated = musicMapper.toMusic(musicWithSingerAndLinksDto);
        checkIfUserCanChangeMusicStatus(musicInDatabase, musicUpdated);
        Singer singer = singerService.findByNameOrCreateIfNotExistToUpdate(musicInDatabase, musicWithSingerAndLinksDto);

        repository.findByNameIgnoreCaseAndSingerAndSpace(musicUpdated.getName(), singer, space)
                .ifPresent(musicFound ->
                        super.verifyIfAlreadyExist(idMusic, musicFound, MUSIC_ALREADY_EXIST_SPACE));

        musicLinkService.updateMusicLink(musicInDatabase, musicWithSingerAndLinksDto.getLinks());
        return musicMapper.updateMusic(musicInDatabase, musicUpdated);
    }

    @Override
    public List<Music> findAllBySpace(String spaceId) {
        Space space = spaceService.findByIdValidated(spaceId);

        return repository.findAllBySpace(space);
    }

    @Override
    public List<MusicTopUsedDto> findTop10MusicMoreUsedInEvents(String spaceId) {
        return repository.findAllBySpaceOrderByEventsCountDescLimit10(spaceId, LocalDate.now()).stream()
                .map(proj -> new MusicTopUsedDto(proj.getMusicId(), proj.getMusicName(), proj.getSingerName(), proj.getAmountUsedInEvents()))
                .collect(Collectors.toList());
    }

    @Override
    public MusicDto findBySpaceAndId(String spaceId, String idMusic, Boolean eventsFromTheLast3Months) {
        Music music = findMusicValidatingSpace(spaceId, idMusic);
        List<EventSimpleDto> events = eventMusicAssociationService.findEventsByMusic(music, eventsFromTheLast3Months);

        MusicDto list = musicMapper.toMusicDto(music);
        list.setEvents(events);

        return list;
    }

    @Override
    public List<MusicOnlyIdAndMusicNameAndSingerNameProjection> findMusicsAssociationForEventsBySpace(String spaceId) {
        return repository.findMusicsAssociationForEventsBySpace(spaceId);
    }

    @Override
    public List<MusicOverviewDto> findMusicOverview(Space space) {
        List<OverviewProjection> list = repository.findMusicOverviewBySpace(space.getId());
        List<MusicOverviewDto> musicOverviewDtoList = overviewMapper.toMusicOverviewDtoList(list);

        Map<String, List<MusicOverviewDto>> musicOverviewMap = musicOverviewDtoList.stream().collect(Collectors.groupingBy(MusicOverviewDto::getStatusName));
        Arrays.asList(MusicStatusEnum.values()).forEach(type -> {
            String typeMusic = type.name();
            if(!musicOverviewMap.containsKey(typeMusic)) {
                musicOverviewDtoList.add(new MusicOverviewDto(typeMusic, 0L));
            }
        });

        return musicOverviewDtoList;
    }

    private void checkIfUserCanChangeMusicStatus(Music musicInDatabase, Music musicUpdated) {
        boolean statusInDatabaseIsRejected = MusicStatusEnum.REJECTED.equals(musicInDatabase.getMusicStatus());
        boolean newStatusIsRejected = MusicStatusEnum.REJECTED.equals(musicUpdated.getMusicStatus());
        boolean isNotSpaceOwner = SpringSecurityUtil.getAllPermissions().stream().noneMatch(p -> PermissionEnum.SPACE_OWNER.name().equals(p));
        if ((statusInDatabaseIsRejected || newStatusIsRejected) && isNotSpaceOwner) {
            throw new ForbiddenException(messagesService.get(MUSIC_CANNOT_CHANGE_STATUS));
        }
    }

    private Music findMusicValidatingSpace(String spaceId, String idMusic) {
        Space space = spaceService.findByIdValidated(spaceId);
        return findBySpaceAndIdValidated(idMusic, space);
    }

    private Music findBySpaceAndIdValidated(String idMusic, Space space) {
        return repository.findBySpaceAndId(space, idMusic)
                .orElseThrow(() ->
                        new ServiceException(
                                messagesService.get(MUSIC_NOT_EXIST_SPACE)
                        ));
    }

    private void validateAssociationBetweenMusicAndSinger(Music music, Singer singer, Space space) {
        repository.findByNameIgnoreCaseAndSingerAndSpace(music.getName(), singer, space)
                .ifPresent(mus -> {
                    throw new ServiceException(
                            messagesService.get(MUSIC_ALREADY_EXIST_SPACE)
                    );
                });

        music.setSinger(singer);
    }

}
