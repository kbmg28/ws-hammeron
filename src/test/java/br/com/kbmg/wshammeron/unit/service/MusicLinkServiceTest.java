package br.com.kbmg.wshammeron.unit.service;

import br.com.kbmg.wshammeron.dto.music.MusicLinkDto;
import br.com.kbmg.wshammeron.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.MusicLink;
import br.com.kbmg.wshammeron.repository.MusicLinkRepository;
import br.com.kbmg.wshammeron.service.impl.MusicLinkServiceImpl;
import br.com.kbmg.wshammeron.unit.BaseUnitTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MusicLinkServiceTest extends BaseUnitTests {

    @InjectMocks
    private MusicLinkServiceImpl musicLinkService;

    @Mock
    private MusicLinkRepository musicLinkRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createLinksValidated_shouldReturnMusicListCreated() {
        Music music = givenMusicFull();
        Set<MusicLink> musicLinkList = music.getMusicLinkList();
        MusicWithSingerAndLinksDto musicWithSingerAndLinksDto = givenMusicWithSingerAndLinksDto(music);
        Set<MusicLinkDto> musicLinkListDto = musicWithSingerAndLinksDto.getLinks();

        Map<String, List<MusicLink>> entityMap = musicLinkList.stream().collect(Collectors.groupingBy(MusicLink::getId));

        musicLinkListDto.forEach(musicLinkDto -> {
            MusicLink musicLink = entityMap.get(musicLinkDto.getId()).get(0);

            when(musicMapperMock.toMusicLink(musicLinkDto)).thenReturn(musicLink);
            when(musicLinkRepository.save(musicLink)).thenReturn(musicLink);
        });

        Set<MusicLink> result = musicLinkService.createLinksValidated(music, musicLinkListDto);

        assertAll(
            () -> verify(musicMapperMock, times(3)).toMusicLink(any()),
            () -> verify(musicLinkRepository, times(3)).save(any()),
            () -> assertEquals(musicLinkList, result)
        );
    }

    @Test
    void updateMusicLink_shouldUpdate() {
        Music music = givenMusicFull();
        MusicWithSingerAndLinksDto musicWithSingerAndLinksDto = givenMusicWithSingerAndLinksDto(music);
        Set<MusicLinkDto> musicLinkListDto = musicWithSingerAndLinksDto.getLinks();

        musicLinkService.updateMusicLink(music, musicLinkListDto);
    }

}
