package br.com.kbmg.wshammeron.repository;

import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.Singer;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.repository.projection.MusicOnlyIdAndMusicNameAndSingerNameProjection;
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

    Optional<Music> findByNameIgnoreCaseAndSingerAndSpace(String name, Singer singer, Space space);

    List<Music> findAllBySpace(Space space);

    Optional<Music> findBySpaceAndId(Space space, String idMusic);

    @Query(value = "SELECT m.id AS \"musicId\", m.name AS \"musicName\", s.name AS \"singerName\", COUNT(ema.id) AS \"amountUsedInEvents\" FROM MUSIC m " +
            "    join SINGER s on s.id = m.singer_id " +
            "    join EVENT_MUSIC_ASSOCIATION ema on ema.music_id = m.id " +
            "    WHERE EXISTS ( " +
            "       SELECT 1 from EVENT e " +
            "           WHERE e.space_id = :spaceId " +
            "               and e.id = ema.event_id " +
            "               and e.date_time_event < :dateTimeEvent" +
            "       )" +
            "    GROUP BY m.id, m.name, s.name " +
            "    ORDER BY COUNT(ema.id) DESC " +
            "    LIMIT 10", nativeQuery = true)
    List<MusicTopUsedProjection> findAllBySpaceOrderByEventsCountDescLimit10(String spaceId, OffsetDateTime dateTimeEvent);

    @Query(value = "SELECT m.id AS \"musicId\", m.name AS \"musicName\", s.name AS \"singerName\" " +
            "FROM MUSIC m " +
            "JOIN SINGER s ON s.id = m.singer_id " +
            "WHERE m.space_id = :spaceId and m.music_status = 'ENABLED' " +
            "ORDER BY lower(m.name)", nativeQuery = true)
    List<MusicOnlyIdAndMusicNameAndSingerNameProjection> findMusicsAssociationForEventsBySpace(String spaceId);

    @Query(value = "SELECT m.music_status AS \"groupName\", COUNT(m.ID) AS \"total\" " +
            " FROM MUSIC m " +
            " WHERE m.space_id = :spaceId " +
            " GROUP BY m.music_status", nativeQuery = true)
    List<OverviewProjection> findMusicOverviewBySpace(String spaceId);
}