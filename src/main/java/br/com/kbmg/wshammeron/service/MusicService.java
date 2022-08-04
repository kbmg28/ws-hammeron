package br.com.kbmg.wshammeron.service;

import br.com.kbmg.wshammeron.dto.music.MusicDto;
import br.com.kbmg.wshammeron.dto.music.MusicTopUsedDto;
import br.com.kbmg.wshammeron.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wshammeron.dto.space.overview.MusicOverviewDto;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.repository.projection.MusicOnlyIdAndMusicNameAndSingerNameProjection;

import java.util.List;

public interface MusicService extends GenericService<Music>{
    Music createMusic(String spaceId, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto);

    void deleteMusic(String spaceId, String idMusic);

    Music updateMusic(String spaceId, String idMusic, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto);

    List<Music> findAllBySpace(String spaceId);

    List<MusicTopUsedDto> findTop10MusicMoreUsedInEvents(String spaceId);

    MusicDto findBySpaceAndId(String spaceId, String idMusic, Boolean eventsFromTheLast3Months);

    List<MusicOnlyIdAndMusicNameAndSingerNameProjection> findMusicsAssociationForEventsBySpace(String spaceId);

    List<MusicOverviewDto> findMusicOverview(Space space);

    Music findBySpaceAndId(Space space, String musicId);
}
