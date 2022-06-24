package br.com.kbmg.wshammeron.repository;

import br.com.kbmg.wshammeron.model.Event;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.repository.projection.EventWithTotalAssociationsProjection;
import br.com.kbmg.wshammeron.repository.projection.OverviewProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    String SELECT_FROM_EVENT_WHERE_SPACE = "SELECT e.id AS \"eventId\", e.name as \"nameEvent\", e.date_time_event as \"dateTimeEvent\", " +
            "               (SELECT COUNT(ema.id) FROM EVENT_MUSIC_ASSOCIATION ema \n" +
            "                   WHERE ema.EVENT_ID = e.id) AS \"musicQuantity\", " +
            "               (SELECT COUNT(eu.id) FROM EVENT_SPACE_USER_APP_ASSOCIATION eu \n" +
            "                   WHERE eu.EVENT_ID = e.id) AS \"userQuantity\", " +
            "               COALESCE((SELECT e.id is not null FROM SPACE_USER_APP_ASSOCIATION su \n" +
            "                   WHERE su.user_app_id = :userId and EXISTS( \n" +
            "                       SELECT 1 FROM EVENT_SPACE_USER_APP_ASSOCIATION esu \n" +
            "                           where esu.event_id = e.id and esu.space_user_app_association_id = su.id)), FALSE) AS \"isUserLoggedIncluded\" " +
            "FROM EVENT e " +
            "where e.space_id = :spaceId";

    Optional<Event> findBySpaceAndDateTimeEvent(Space space, OffsetDateTime dateTimeEvent);

    @Query(value = SELECT_FROM_EVENT_WHERE_SPACE + " and (e.date_time_event >= :startDate and e.date_time_event <= :endDate) ", nativeQuery = true)
    List<EventWithTotalAssociationsProjection> findAllBySpaceAndDateTimeEventRange(String spaceId, OffsetDateTime startDate, OffsetDateTime endDate, String userId);

    @Query(value = SELECT_FROM_EVENT_WHERE_SPACE + " and e.date_time_event >= :startDate", nativeQuery = true)
    List<EventWithTotalAssociationsProjection> findAllBySpaceAndDateTimeEventGreaterThanEqual(String spaceId, OffsetDateTime startDate, String userId);

    Optional<Event> findBySpaceAndId(Space space, String idEvent);

    @Query(value = "SELECT " +
            "   (CASE " +
            "       WHEN e.date_time_event < current_date " +
            "           THEN 'OLD' " +
            "       ELSE 'NEXT' " +
            "    END)  AS \"groupName\", " +
            "   COUNT(e.id) AS \"total\"" +
            "FROM EVENT e " +
            "WHERE e.space_id = :spaceId " +
            "GROUP BY \"groupName\"", nativeQuery = true)
    List<OverviewProjection> findEventOverviewBySpace(String spaceId);

    List<Event> findAllEventsByDateTimeEvent(OffsetDateTime dateTimeEvent);
}
