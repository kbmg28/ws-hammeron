package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.enums.MusicStatusEnum;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.model.MusicLink;
import br.com.kbmg.wsmusiccontrol.model.Singer;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.MusicRepository;
import br.com.kbmg.wsmusiccontrol.service.MusicLinkService;
import br.com.kbmg.wsmusiccontrol.service.MusicService;
import br.com.kbmg.wsmusiccontrol.service.SingerService;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import br.com.kbmg.wsmusiccontrol.util.mapper.MusicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    @Override
    public Music createMusic(String spaceId, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto) {
        Space space = getSpaceValidatingIfUserCanAccess(spaceId);
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
        Music music = this.findBySpaceAndId(spaceId, idMusic);
        music.setMusicStatus(newStatus);
        this.update(music);
    }

    @Override
    public void deleteMusic(String spaceId, String idMusic) {
        Music music = this.findBySpaceAndId(spaceId, idMusic);

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
        Space space = getSpaceValidatingIfUserCanAccess(spaceId);
        Music musicInDatabase = findBySpaceAndIdValidated(idMusic, space);
        Music musicUpdated = musicMapper.toMusic(musicWithSingerAndLinksDto);

        Singer singer = singerService.findByNameOrCreateIfNotExistToUpdate(musicInDatabase, musicWithSingerAndLinksDto);

        repository.findByNameIgnoreCaseAndSingerAndSpace(musicUpdated.getName(), singer, space)
                .ifPresent(musicFound ->
                        super.verifyIfAlreadyExist(idMusic, musicFound, "music.already.exist.space"));

        musicLinkService.updateMusicLink(musicInDatabase, musicWithSingerAndLinksDto.getLinks());
        return musicMapper.updateMusic(musicInDatabase, musicUpdated);
    }

    @Override
    public List<Music> findAllBySpace(String spaceId) {
        Space space = getSpaceValidatingIfUserCanAccess(spaceId);

        return repository.findAllBySpace(space);
    }

    @Override
    public Music findBySpaceAndId(String spaceId, String idMusic) {
        Space space = getSpaceValidatingIfUserCanAccess(spaceId);
        return findBySpaceAndIdValidated(idMusic, space);
    }

    private Music findBySpaceAndIdValidated(String idMusic, Space space) {
        return repository.findBySpaceAndId(space, idMusic)
                .orElseThrow(() ->
                        new ServiceException(
                                messagesService.get("music.not.exist.space")
                        ));
    }

    private void validateAssociationBetweenMusicAndSinger(Music music, Singer singer, Space space) {
        repository.findByNameIgnoreCaseAndSingerAndSpace(music.getName(), singer, space)
                .ifPresent(mus -> {
                    throw new ServiceException(
                            messagesService.get("music.already.exist.space")
                    );
                });

        music.setSinger(singer);
    }

    private Space getSpaceValidatingIfUserCanAccess(String spaceId) {
        UserApp userLogged = userAppService.findUserLogged();
        return spaceService.findByIdAndUserAppValidated(spaceId, userLogged);
    }

}
