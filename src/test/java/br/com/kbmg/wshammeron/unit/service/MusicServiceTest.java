package br.com.kbmg.wshammeron.unit.service;

import br.com.kbmg.wshammeron.dto.music.MusicDto;
import br.com.kbmg.wshammeron.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wshammeron.dto.space.overview.MusicOverviewDto;
import br.com.kbmg.wshammeron.model.Event;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.Singer;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.repository.MusicRepository;
import br.com.kbmg.wshammeron.service.impl.MusicServiceImpl;
import br.com.kbmg.wshammeron.unit.BaseUnitTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.MUSIC_ALREADY_EXIST_SPACE;
import static br.com.kbmg.wshammeron.constants.KeyMessageConstants.MUSIC_NOT_EXIST_SPACE;
import static br.com.kbmg.wshammeron.unit.ExceptionAssertions.thenShouldThrowServiceException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MusicServiceTest extends BaseUnitTests {

    @InjectMocks
    private MusicServiceImpl musicService;

    @Mock
    private MusicRepository repositoryMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMusic_shouldReturnMusicCreated() {
        Music music = givenMusic();
        Singer singer = music.getSinger();
        Space space = music.getSpace();
        MusicWithSingerAndLinksDto musicWithSingerAndLinksDto = givenMusicWithSingerAndLinksDto(music);

        when(spaceServiceMock.findByIdValidated(space.getId())).thenReturn(space);
        when(singerServiceMock.findByNameOrCreateIfNotExist(singer.getName())).thenReturn(singer);
        when(musicMapperMock.toMusic(musicWithSingerAndLinksDto)).thenReturn(music);
        when(repositoryMock.save(any())).thenReturn(music);

        Music result = musicService.createMusic(space.getId(), musicWithSingerAndLinksDto);


        assertAll(() -> verify(spaceServiceMock).findByIdValidated(any()),
                () -> verify(singerServiceMock).findByNameOrCreateIfNotExist(any()),
                () -> verify(musicMapperMock).toMusic(any()),
                () -> verify(repositoryMock).findByNameIgnoreCaseAndSingerAndSpace(music.getName(), singer, space),
                () -> verify(repositoryMock).save(any()),
                () -> assertEquals(music, result));
    }

    @Test
    void createMusic_shouldReturnExceptionIfAlreadyExists() {
        Music music = givenMusic();
        Singer singer = music.getSinger();
        Space space = music.getSpace();
        MusicWithSingerAndLinksDto musicWithSingerAndLinksDto = givenMusicWithSingerAndLinksDto(music);

        when(spaceServiceMock.findByIdValidated(space.getId())).thenReturn(space);
        when(singerServiceMock.findByNameOrCreateIfNotExist(singer.getName())).thenReturn(singer);
        when(musicMapperMock.toMusic(musicWithSingerAndLinksDto)).thenReturn(music);
        when(repositoryMock.findByNameIgnoreCaseAndSingerAndSpace(music.getName(), singer, space))
                .thenReturn(Optional.of(music));

        assertAll(() -> thenShouldThrowServiceException(space.getId(), musicWithSingerAndLinksDto, musicService::createMusic),
                () -> verify(spaceServiceMock).findByIdValidated(any()),
                () -> verify(singerServiceMock).findByNameOrCreateIfNotExist(any()),
                () -> verify(musicMapperMock).toMusic(any()),
                () -> verify(repositoryMock).findByNameIgnoreCaseAndSingerAndSpace(music.getName(), singer, space),
                () -> verify(messagesServiceMock).get(MUSIC_ALREADY_EXIST_SPACE));
    }

    @Test
    void deleteMusic_shouldDelete() {
        Music music = givenMusic();
        Singer singer = music.getSinger();
        Space space = music.getSpace();

        when(spaceServiceMock.findByIdValidated(space.getId())).thenReturn(space);
        when(repositoryMock.findBySpaceAndId(space, music.getId())).thenReturn(Optional.of(music));

        musicService.deleteMusic(space.getId(), music.getId());

        assertAll(() -> verify(spaceServiceMock).findByIdValidated(space.getId()),
                () -> verify(repositoryMock).findBySpaceAndId(space, music.getId()),
                () -> verify(musicLinkServiceMock).deleteInBatch(music.getMusicLinkList()),
                () -> verify(repositoryMock).delete(music),
                () -> verify(singerServiceMock).deleteOrRemoveAssociation(music, singer));
    }

    @Test
    void deleteMusic_shouldReturnErrorIfMusicNotExistOnSpace() {
        Music music = givenMusic();
        Space space = music.getSpace();

        when(spaceServiceMock.findByIdValidated(space.getId())).thenReturn(space);
        when(repositoryMock.findBySpaceAndId(space, music.getId())).thenReturn(Optional.empty());

        assertAll(() -> thenShouldThrowServiceException(space.getId(), music.getId(), musicService::deleteMusic),
                () -> verify(spaceServiceMock).findByIdValidated(space.getId()),
                () -> verify(repositoryMock).findBySpaceAndId(space, music.getId()),
                () -> verify(messagesServiceMock).get(MUSIC_NOT_EXIST_SPACE));
    }

    @Test
    void updateMusic_shouldReturnMusicUpdated() {
        Music music = givenMusic();
        Singer singer = music.getSinger();
        Space space = music.getSpace();
        MusicWithSingerAndLinksDto musicWithSingerAndLinksDto = givenMusicWithSingerAndLinksDto(music);

        when(spaceServiceMock.findByIdValidated(space.getId())).thenReturn(space);
        when(repositoryMock.findBySpaceAndId(space, music.getId())).thenReturn(Optional.of(music));
        when(musicMapperMock.toMusic(musicWithSingerAndLinksDto)).thenReturn(music);
        when(singerServiceMock.findByNameOrCreateIfNotExistToUpdate(music, musicWithSingerAndLinksDto)).thenReturn(singer);
        when(repositoryMock.findByNameIgnoreCaseAndSingerAndSpace(music.getName(), singer, space)).thenReturn(Optional.empty());
        when(musicMapperMock.updateMusic(music, music)).thenReturn(music);


        Music result = musicService.updateMusic(space.getId(), music.getId(), musicWithSingerAndLinksDto);


        assertAll(() -> verify(spaceServiceMock).findByIdValidated(any()),
                () -> verify(repositoryMock).findBySpaceAndId(space, music.getId()),
                () -> verify(musicMapperMock).toMusic(musicWithSingerAndLinksDto),
                () -> verify(singerServiceMock).findByNameOrCreateIfNotExistToUpdate(music, musicWithSingerAndLinksDto),
                () -> verify(repositoryMock).findByNameIgnoreCaseAndSingerAndSpace(music.getName(), singer, space),
                () -> verify(musicMapperMock).updateMusic(music, music),
                () -> assertEquals(music, result));
    }

    @Test
    void updateMusic_shouldReturnErrorIfMusicNotExistOnSpace() {
        Music music = givenMusic();
        Space space = music.getSpace();
        MusicWithSingerAndLinksDto musicWithSingerAndLinksDto = givenMusicWithSingerAndLinksDto(music);

        when(spaceServiceMock.findByIdValidated(space.getId())).thenReturn(space);
        when(repositoryMock.findBySpaceAndId(space, music.getId())).thenReturn(Optional.empty());

        assertAll(() -> thenShouldThrowServiceException(
                            ()-> musicService.updateMusic(space.getId(), music.getId(), musicWithSingerAndLinksDto)),
                () -> verify(spaceServiceMock).findByIdValidated(any()),
                () -> verify(repositoryMock).findBySpaceAndId(space, music.getId()),
                () -> verify(messagesServiceMock).get(MUSIC_NOT_EXIST_SPACE));
    }

    @Test
    void updateMusic_shouldReturnErrorIfMusicNameAlreadyExist() {
        Music music = givenMusic();
        Music otherMusic = givenMusic();
        otherMusic.setId(UUID.randomUUID().toString());
        Singer singer = music.getSinger();
        Space space = music.getSpace();
        MusicWithSingerAndLinksDto musicWithSingerAndLinksDto = givenMusicWithSingerAndLinksDto(music);

        when(spaceServiceMock.findByIdValidated(space.getId())).thenReturn(space);
        when(repositoryMock.findBySpaceAndId(space, music.getId())).thenReturn(Optional.of(music));
        when(musicMapperMock.toMusic(musicWithSingerAndLinksDto)).thenReturn(music);
        when(singerServiceMock.findByNameOrCreateIfNotExistToUpdate(music, musicWithSingerAndLinksDto)).thenReturn(singer);
        when(repositoryMock.findByNameIgnoreCaseAndSingerAndSpace(music.getName(), singer, space)).thenReturn(Optional.of(otherMusic));

        assertAll(() -> thenShouldThrowServiceException(
                        ()-> musicService.updateMusic(space.getId(), music.getId(), musicWithSingerAndLinksDto)),
                () -> verify(spaceServiceMock).findByIdValidated(any()),
                () -> verify(repositoryMock).findBySpaceAndId(space, music.getId()),
                () -> verify(musicMapperMock).toMusic(musicWithSingerAndLinksDto),
                () -> verify(singerServiceMock).findByNameOrCreateIfNotExistToUpdate(music, musicWithSingerAndLinksDto),
                () -> verify(repositoryMock).findByNameIgnoreCaseAndSingerAndSpace(music.getName(), singer, space),
                () -> verify(messagesServiceMock).get(MUSIC_ALREADY_EXIST_SPACE));
    }

    @Test
    void findAllBySpace_shouldReturnMusicList() {
        Music music = givenMusic();
        Space space = music.getSpace();
        List<Music> musicList = List.of(music);

        when(spaceServiceMock.findByIdValidated(space.getId())).thenReturn(space);
        when(repositoryMock.findAllBySpace(space)).thenReturn(musicList);

        List<Music> result = musicService.findAllBySpace(space.getId());

        assertAll(() -> verify(spaceServiceMock).findByIdValidated(space.getId()),
                () -> verify(repositoryMock).findAllBySpace(space),
                () -> assertEquals(musicList, result));
    }

    @Test
    void findTop10MusicMoreUsedInEvents_shouldReturnMusicTopUsedDto() {
        musicService.findTop10MusicMoreUsedInEvents(UUID.randomUUID().toString());
        verify(repositoryMock).findAllBySpaceOrderByEventsCountDescLimit10(any(), any());
    }

    @Test
    void findBySpaceAndId_shouldReturnMusicDto() {
        Music music = givenMusic();
        Space space = music.getSpace();
        MusicDto musicDto = givenMusicDto(music);

        when(spaceServiceMock.findByIdValidated(space.getId())).thenReturn(space);
        when(repositoryMock.findBySpaceAndId(space, music.getId())).thenReturn(Optional.of(music));
        when(musicMapperMock.toMusicDto(music)).thenReturn(musicDto);

        MusicDto result = musicService.findBySpaceAndId(space.getId(), music.getId(), false);

        assertAll(() -> verify(spaceServiceMock).findByIdValidated(space.getId()),
                () -> verify(repositoryMock).findBySpaceAndId(space, music.getId()),
                () -> verify(eventMusicAssociationServiceMock).findEventsByMusic(music, false),
                () -> assertEquals(musicDto, result));
    }

    @Test
    void findBySpaceAndId_shouldReturnErrorIfMusicNotExistOnSpace() {
        Music music = givenMusic();
        Space space = music.getSpace();

        when(spaceServiceMock.findByIdValidated(space.getId())).thenReturn(space);
        when(repositoryMock.findBySpaceAndId(space, music.getId())).thenReturn(Optional.empty());

        assertAll(() -> thenShouldThrowServiceException(
                        () -> musicService.findBySpaceAndId(space.getId(), music.getId(), false)
                ),
                () -> verify(spaceServiceMock).findByIdValidated(space.getId()),
                () -> verify(repositoryMock).findBySpaceAndId(space, music.getId()),
                () -> verify(messagesServiceMock).get(MUSIC_NOT_EXIST_SPACE));
    }

    @Test
    void findMusicsAssociationForEventsBySpace_shouldReturnMusicProjection() {
        musicService.findMusicsAssociationForEventsBySpace(UUID.randomUUID().toString());
        verify(repositoryMock).findMusicsAssociationForEventsBySpace(any());
    }

    @Test
    void findMusicOverview_shouldReturnMusicOverviewDtoList() {
        UserApp userApp = super.givenUserAppFull();
        Space space = super.givenSpace(userApp);
        Music music = givenMusic(space);
        Event event = givenOldEvent(userApp, space, music);
        MusicOverviewDto musicOverviewDto = new MusicOverviewDto(music.getMusicStatus().toString(), (long) event.getEventMusicList().size());

        when(overviewMapperMock.toMusicOverviewDtoList(any()))
                .thenReturn(new ArrayList<>(List.of(musicOverviewDto)));

        List<MusicOverviewDto> result = musicService.findMusicOverview(space);

        assertAll(() -> verify(repositoryMock).findMusicOverviewBySpace(space.getId()),
                () -> verify(overviewMapperMock).toMusicOverviewDtoList(any()),
                () -> assertTrue(result.contains(musicOverviewDto)));
    }
}
