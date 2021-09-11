package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.enums.MusicStatusEnum;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.model.MusicLink;
import br.com.kbmg.wsmusiccontrol.model.Singer;
import br.com.kbmg.wsmusiccontrol.repository.MusicRepository;
import br.com.kbmg.wsmusiccontrol.service.MusicLinkService;
import br.com.kbmg.wsmusiccontrol.service.MusicService;
import br.com.kbmg.wsmusiccontrol.service.SingerService;
import br.com.kbmg.wsmusiccontrol.util.mapper.MusicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class MusicServiceImpl extends GenericServiceImpl<Music, MusicRepository> implements MusicService {

    @Autowired
    private MusicMapper musicMapper;

    @Autowired
    private SingerService singerService;

    @Autowired
    private MusicLinkService musicLinkService;

    @Override
    public Music createMusic(MusicWithSingerAndLinksDto musicWithSingerAndLinksDto) {
        Singer singer = singerService.findByNameOrCreateIfNotExist(musicWithSingerAndLinksDto.getSinger().getName());
        Music music = musicMapper.toMusic(musicWithSingerAndLinksDto);

        validateAssociationBetweenMusicAndSinger(music, singer);
        repository.save(music);


        Set<MusicLink> links = musicLinkService.createLinksValidated(music, musicWithSingerAndLinksDto.getLinks());
        music.setMusicLinkList(links);

        return music;
    }

    @Override
    public void updateStatusMusic(Long idMusic, MusicStatusEnum newStatus) {
        Music music = this.findByIdValidated(idMusic);
        music.setMusicStatus(newStatus);
        this.update(music);
    }

    @Override
    public Music findByIdValidated(Long idMusic) {
        return this.findByIdValidated(idMusic, "music not exists");
    }

    @Override
    public void deleteMusic(Long idMusic) {
        Music music = this.findByIdValidated(idMusic);

        if (!music.getEventMusicList().isEmpty()) {
            throw new ServiceException("music already used in events. Only is permitted disable");
        }
        Singer singer = music.getSinger();

        musicLinkService.deleteInBatch(music.getMusicLinkList());
        super.delete(music);
        singerService.deleteOrRemoveAssociation(music, singer);
    }

    @Override
    public Music updateMusic(Long idMusic, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto) {
        Music musicInDatabase = findByIdValidated(idMusic);
        Music musicUpdated = musicMapper.toMusic(musicWithSingerAndLinksDto);

        Singer singer = singerService.findByNameOrCreateIfNotExistToUpdate(musicInDatabase, musicWithSingerAndLinksDto);

        validateIfAlreadyExist(idMusic, musicUpdated.getName(), singer, repository::findByNameIgnoreCaseAndSinger, "music already exist");

        musicLinkService.updateMusicLink(musicInDatabase, musicWithSingerAndLinksDto.getLinks());
        return musicMapper.updateMusic(musicInDatabase, musicUpdated);
    }

    private void validateAssociationBetweenMusicAndSinger(Music music, Singer singer) {
        repository.findByNameIgnoreCaseAndSinger(music.getName(), singer)
                .ifPresent(mus -> {
                    throw new ServiceException("music already exists");
                });

        music.setSinger(singer);
    }
}
