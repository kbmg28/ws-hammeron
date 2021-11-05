package br.com.kbmg.wsmusiccontrol.repository;

import br.com.kbmg.wsmusiccontrol.model.Event;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.repository.projection.EventWithTotalAssociationsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {

    String SELECT_FROM_EVENT_WHERE_SPACE = "SELECT e.id AS \"eventId\", e.name as \"nameEvent\", e.date_event as \"dateEvent\", e.time_event AS \"timeEvent\", " +
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

    Optional<Event> findBySpaceAndDateEventAndTimeEvent(Space space, LocalDate date, LocalTime time);

    @Query(value = SELECT_FROM_EVENT_WHERE_SPACE + " and (e.date_event >= :startDate and e.date_event < :endDate) ", nativeQuery = true)
    List<EventWithTotalAssociationsProjection> findAllBySpaceAndDateEventRange(String spaceId, LocalDate startDate, LocalDate endDate, String userId);

    @Query(value = SELECT_FROM_EVENT_WHERE_SPACE + " and e.date_event >= :startDate", nativeQuery = true)
    List<EventWithTotalAssociationsProjection> findAllBySpaceAndDateEventGreaterThanEqual(String spaceId, LocalDate startDate, String userId);
}
