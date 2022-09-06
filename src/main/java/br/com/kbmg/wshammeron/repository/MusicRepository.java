package br.com.kbmg.wshammeron.repository;

import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.Singer;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.repository.projection.MusicTopUsedProjection;
import br.com.kbmg.wshammeron.repository.projection.OverviewProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MusicRepository extends JpaRepository<Music, String> {

    String SELECT_FIND_MUSICS_WITH_EVENTS_COUNT = "SELECT m.id AS \"musicId\", " +
            "       m.name AS \"musicName\", " +
            "       m.music_status as \"musicStatus\", " +
            "       s.name AS \"singerName\", " +
            "       (select COUNT(1) from EVENT_MUSIC_ASSOCIATION ema " +
            "        where ema.music_id = m.id " +
            "          and EXISTS ( " +
            "                SELECT 1 from EVENT e " +
            "                WHERE e.id = ema.event_id " +
            "                  and (e.date_time_event >= :startDateEvent " +
            "                    and e.date_time_event < :endDateEvent) " +
            "            )) AS \"amountUsedInEvents\" " +
            "FROM MUSIC m " +
            "         join SINGER s on s.id = m.singer_id " +
            "WHERE m.music_Status = :musicStatus and m.space_id = :spaceId ";
    Optional<Music> findByNameIgnoreCaseAndSingerAndSpace(String name, Singer singer, Space space);

    List<Music> findAllBySpace(Space space);

    Optional<Music> findBySpaceAndId(Space space, String idMusic);

    @Query(value = SELECT_FIND_MUSICS_WITH_EVENTS_COUNT +
            "    ORDER BY \"amountUsedInEvents\" DESC " +
            "    LIMIT 10", nativeQuery = true)
    List<MusicTopUsedProjection> findAllBySpaceOrderByEventsCountDescLimit10(String spaceId,
                                                                             String musicStatus,
                                                                             OffsetDateTime startDateEvent,
                                                                             OffsetDateTime endDateEvent);

    @Query(value = SELECT_FIND_MUSICS_WITH_EVENTS_COUNT +
            "ORDER BY lower(m.name)", nativeQuery = true)
    List<MusicTopUsedProjection> findMusicsAssociationForEventsBySpace(String spaceId,
                                                                       String musicStatus,
                                                                       OffsetDateTime startDateEvent,
                                                                       OffsetDateTime endDateEvent);

    @Query(value = "SELECT m.music_status AS \"groupName\", COUNT(m.ID) AS \"total\" " +
            " FROM MUSIC m " +
            " WHERE m.space_id = :spaceId " +
            " GROUP BY m.music_status", nativeQuery = true)
    List<OverviewProjection> findMusicOverviewBySpace(String spaceId);
}