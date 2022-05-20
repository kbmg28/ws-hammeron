package br.com.kbmg.wshammeron.service.impl;

import br.com.kbmg.wshammeron.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.Singer;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.repository.SingerRepository;
import br.com.kbmg.wshammeron.service.SingerService;
import br.com.kbmg.wshammeron.service.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Service
public class SingerServiceImpl extends GenericServiceImpl<Singer, SingerRepository> implements SingerService {

    @Autowired
    private SpaceService spaceService;

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
                                                       @Valid MusicWithSingerAndLinksDto musicWithSingerAndLinksDto) {
        Singer singerToUpdate = this.findByNameOrCreateIfNotExist(musicWithSingerAndLinksDto.getSinger().getName());
        Singer singerInDatabase = musicInDatabase.getSinger();

        if (!singerInDatabase.equals(singerToUpdate)) {
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
    public List<Singer> findAllBySpace(String spaceId) {
        Space space = spaceService.findByIdValidated(spaceId);
        return repository.findAllBySpace(space);
    }
}
