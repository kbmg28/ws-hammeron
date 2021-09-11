package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicLinkDto;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.model.MusicLink;

import java.util.Set;

public interface MusicLinkService extends GenericService<MusicLink>{
    Set<MusicLink> createLinksValidated(Music music, Set<MusicLinkDto> links);

    void updateMusicLink(Music musicInDatabase, Set<MusicLinkDto> linksDto);
}
