package br.com.kbmg.wshammeron.service;

import br.com.kbmg.wshammeron.dto.music.MusicLinkDto;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.MusicLink;

import java.util.Set;

public interface MusicLinkService extends GenericService<MusicLink>{
    Set<MusicLink> createLinksValidated(Music music, Set<MusicLinkDto> links);

    void updateMusicLink(Music musicInDatabase, Set<MusicLinkDto> linksDto);
}
