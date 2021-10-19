package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.model.Singer;

import java.util.List;

public interface SingerService extends GenericService<Singer>{
    Singer findByNameOrCreateIfNotExist(String name);

    Singer findByNameOrCreateIfNotExistToUpdate(Music musicInDatabase, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto);

    void deleteOrRemoveAssociation(Music music, Singer singer);

    List<Singer> findAllBySpace(Long spaceId);
}
