package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.model.Singer;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.SingerRepository;
import br.com.kbmg.wsmusiccontrol.service.SingerService;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SingerServiceImpl extends GenericServiceImpl<Singer, SingerRepository> implements SingerService {

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private UserAppService userAppService;

    @Override
    public Singer findByNameOrCreateIfNotExist(String name) {
        return repository.findByNameIgnoreCase(name).orElseGet(() -> {
            Singer newSinger = new Singer();
            newSinger.setName(name);
            return repository.save(newSinger);
        });
    }

    @Override
    public Singer findByNameOrCreateIfNotExistToUpdate(Music musicInDatabase,
                                                       MusicWithSingerAndLinksDto musicWithSingerAndLinksDto) {
        Singer singerToUpdate = this.findByNameOrCreateIfNotExist(musicWithSingerAndLinksDto.getSinger().getName());
        Singer singerInDatabase = musicInDatabase.getSinger();

        if (singerInDatabase != singerToUpdate) {
            singerInDatabase.getMusicList().remove(musicInDatabase);
            musicInDatabase.setSinger(singerToUpdate);

            if (singerInDatabase.getMusicList().isEmpty()) {
                repository.delete(singerInDatabase);
            }
        } else {
            singerToUpdate = singerInDatabase;
        }

        return singerToUpdate;
    }

    @Override
    public void deleteOrRemoveAssociation(Music music, Singer singer) {
        Set<Music> musicList = singer.getMusicList();
        musicList.remove(music);

        if (musicList.isEmpty()) {
            super.delete(singer);
        }
    }

    @Override
    public List<Singer> findAllBySpace(Long spaceId) {
        UserApp userLogged = userAppService.findUserLogged();
        Space space = spaceService.findByIdAndUserAppValidated(spaceId, userLogged);
        return repository.findAllBySpace(space);
    }
}
