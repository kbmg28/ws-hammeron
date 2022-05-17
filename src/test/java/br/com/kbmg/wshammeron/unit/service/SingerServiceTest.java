package br.com.kbmg.wshammeron.unit.service;

import br.com.kbmg.wshammeron.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.Singer;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.repository.SingerRepository;
import br.com.kbmg.wshammeron.service.impl.SingerServiceImpl;
import br.com.kbmg.wshammeron.unit.BaseUnitTests;
import builder.MusicBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SingerServiceTest extends BaseUnitTests {

    @InjectMocks
    private SingerServiceImpl singerService;

    @Mock
    private SingerRepository singerRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByNameOrCreateIfNotExist_shouldReturnNewSingerCreated() {
        Music music = givenMusicFull();
        Singer singer = music.getSinger();

        when(singerRepository.findByNameIgnoreCase(singer.getName())).thenReturn(Optional.empty());
        when(singerRepository.save(any())).thenReturn(singer);

        Singer result = singerService.findByNameOrCreateIfNotExist(singer.getName());

        assertAll(
            () -> verify(singerRepository).findByNameIgnoreCase(any()),
            () -> verify(singerRepository).save(any()),
            () -> assertEquals(singer, result)
        );
    }

    @Test
    void findByNameOrCreateIfNotExist_shouldReturnSingerPreviousExists() {
        Music music = givenMusicFull();
        Singer singer = music.getSinger();

        when(singerRepository.findByNameIgnoreCase(singer.getName())).thenReturn(Optional.of(singer));

        Singer result = singerService.findByNameOrCreateIfNotExist(singer.getName());

        assertAll(
            () -> verify(singerRepository).findByNameIgnoreCase(any()),
            () -> verify(singerRepository, times(0)).save(any()),
            () -> assertEquals(singer, result)
        );
    }

    @Test
    void findByNameOrCreateIfNotExistToUpdate_shouldReturnSingerPreviousExists() {
        Music music = givenMusicFull();
        Music musicInDatabase = givenMusicFull();
        Singer singerToUpdate = music.getSinger();
        Singer singerInDatabase = musicInDatabase.getSinger();
        MusicWithSingerAndLinksDto musicWithSingerAndLinksDto = givenMusicWithSingerAndLinksDto(musicInDatabase);

        when(singerRepository.findByNameIgnoreCase(singerToUpdate.getName())).thenReturn(Optional.of(singerToUpdate));

        Singer result = singerService.findByNameOrCreateIfNotExistToUpdate(musicInDatabase, musicWithSingerAndLinksDto);

        assertAll(
            () -> verify(singerRepository).findByNameIgnoreCase(any()),
            () -> verify(singerRepository).delete(any()),
            () -> assertEquals(singerToUpdate, result)
        );
    }

    @Test
    void deleteOrRemoveAssociation_shouldDeleteSingerIfMusicListEmpty() {
        Music music = givenMusicFull();
        Singer singer = MusicBuilder.generateSinger();

        singerService.deleteOrRemoveAssociation(music, singer);

        assertAll(
                () -> verify(singerRepository).delete(any())
        );
    }

    @Test
    void findAllBySpace_shouldReturnSingerList() {
        Music music = givenMusicFull();
        Singer singer = MusicBuilder.generateSinger();
        Space space = music.getSpace();
        List<Singer> listOfSingers = List.of(singer);
        when(spaceServiceMock.findByIdValidated(space.getId())).thenReturn(space);
        when(singerRepository.findAllBySpace(space)).thenReturn(listOfSingers);

        List<Singer> result = singerService.findAllBySpace(space.getId());

        assertAll(
                () -> verify(spaceServiceMock).findByIdValidated(any()),
                () -> verify(singerRepository).findAllBySpace(space),
                () -> assertEquals(listOfSingers, result)
        );
    }

}
