package br.com.kbmg.wshammeron.service;

import br.com.kbmg.wshammeron.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.Singer;

import java.util.List;

public interface SingerService extends GenericService<Singer>{
    Singer findByNameOrCreateIfNotExist(String name);

    Singer findByNameOrCreateIfNotExistToUpdate(Music musicInDatabase, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto);

    void deleteOrRemoveAssociation(Music music, Singer singer);

    List<Singer> findAllBySpace(String spaceId);
}
