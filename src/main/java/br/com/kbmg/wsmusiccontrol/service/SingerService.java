package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.model.Singer;

import java.util.List;
import java.util.UUID;

public interface SingerService extends GenericService<Singer>{
    Singer findByNameOrCreateIfNotExist(String name);

    Singer findByNameOrCreateIfNotExistToUpdate(Music musicInDatabase, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto);

    void deleteOrRemoveAssociation(Music music, Singer singer);

    List<Singer> findAllBySpace(String spaceId);
}
