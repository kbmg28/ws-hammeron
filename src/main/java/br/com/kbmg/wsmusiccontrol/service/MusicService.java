package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicDto;
import br.com.kbmg.wsmusiccontrol.dto.music.MusicTopUsedDto;
import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.dto.space.overview.MusicOverviewDto;
import br.com.kbmg.wsmusiccontrol.enums.MusicStatusEnum;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.repository.projection.MusicOnlyIdAndMusicNameAndSingerNameProjection;

import java.util.List;

public interface MusicService extends GenericService<Music>{
    Music createMusic(String spaceId, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto);

    void updateStatusMusic(String spaceId, String idMusic, MusicStatusEnum newStatus);

    void deleteMusic(String spaceId, String idMusic);

    Music updateMusic(String spaceId, String idMusic, MusicWithSingerAndLinksDto musicWithSingerAndLinksDto);

    List<Music> findAllBySpace(String spaceId);

    List<MusicTopUsedDto> findTop10MusicMoreUsedInEvents(String spaceId);

    MusicDto findBySpaceAndId(String spaceId, String idMusic, Boolean eventsFromTheLast3Months);

    List<MusicOnlyIdAndMusicNameAndSingerNameProjection> findMusicsAssociationForEventsBySpace(String spaceId);

    List<MusicOverviewDto> findMusicOverview(Space space);
}
