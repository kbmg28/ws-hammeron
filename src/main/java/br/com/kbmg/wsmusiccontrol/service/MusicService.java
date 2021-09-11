package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.enums.MusicStatusEnum;
import br.com.kbmg.wsmusiccontrol.model.Music;

public interface MusicService extends GenericService<Music>{
    Music createMusic(MusicWithSingerAndLinksDto musicWithSingerAndLinksDto);

    void updateStatusMusic(Long idMusic, MusicStatusEnum newStatus);

    Music findByIdValidated(Long idMusic);

    void deleteMusic(Long idMusic);

    Music updateMusic(Long idMusic, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto);
}
