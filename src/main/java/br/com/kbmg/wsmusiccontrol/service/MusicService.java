package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.enums.MusicStatusEnum;
import br.com.kbmg.wsmusiccontrol.model.Music;

import java.util.List;
import java.util.UUID;

public interface MusicService extends GenericService<Music>{
    Music createMusic(String spaceId, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto);

    void updateStatusMusic(String spaceId, String idMusic, MusicStatusEnum newStatus);

    void deleteMusic(String spaceId, String idMusic);

    Music updateMusic(String spaceId, String idMusic, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto);

    List<Music> findAllBySpace(String spaceId);

    Music findBySpaceAndId(String spaceId, String idMusic);
}
