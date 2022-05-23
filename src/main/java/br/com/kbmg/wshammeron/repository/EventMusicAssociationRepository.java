package br.com.kbmg.wshammeron.repository;

import br.com.kbmg.wshammeron.model.Event;
import br.com.kbmg.wshammeron.model.EventMusicAssociation;
import br.com.kbmg.wshammeron.repository.projection.EventSimpleProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventMusicAssociationRepository extends JpaRepository<EventMusicAssociation, String> {

    String SELECT_ASSOCIATION_BY_MUSIC = "SELECT e.id AS \"eventId\", e.date_event AS \"date\", e.name AS \"name\" " +
            "FROM EVENT_MUSIC_ASSOCIATION ema " +
            "JOIN EVENT e ON e.id = ema.event_id " +
            "WHERE ema.music_id = :musicId ";
    String ORDER_BY_DATE_EVENT_DESC = "ORDER BY e.date_event DESC";

    List<EventMusicAssociation> findAllByEvent(Event event);

    @Query(value = SELECT_ASSOCIATION_BY_MUSIC + "AND (e.date_event >= :startDate AND e.date_event < :endDate) " +
            ORDER_BY_DATE_EVENT_DESC
            , nativeQuery = true)
    List<EventSimpleProjection> findAllEventsOfMusicByDateRange(String musicId, LocalDate startDate, LocalDate endDate);

    @Query(value = SELECT_ASSOCIATION_BY_MUSIC + ORDER_BY_DATE_EVENT_DESC, nativeQuery = true)
    List<EventSimpleProjection> findAllEventsOfMusic(String musicId);
}