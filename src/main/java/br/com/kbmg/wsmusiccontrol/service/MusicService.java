package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.enums.MusicStatusEnum;
import br.com.kbmg.wsmusiccontrol.model.Music;

import java.util.List;

public interface MusicService extends GenericService<Music>{
    Music createMusic(Long spaceId, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto);

    void updateStatusMusic(Long spaceId, Long idMusic, MusicStatusEnum newStatus);

    void deleteMusic(Long spaceId, Long idMusic);

    Music updateMusic(Long spaceId, Long idMusic, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto);

    List<Music> findAllBySpace(Long spaceId);

    Music findBySpaceAndId(Long spaceId, Long idMusic);
}
