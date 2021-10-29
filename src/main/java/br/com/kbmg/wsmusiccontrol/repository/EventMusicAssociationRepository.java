package br.com.kbmg.wsmusiccontrol.repository;

import br.com.kbmg.wsmusiccontrol.model.Event;
import br.com.kbmg.wsmusiccontrol.model.EventMusicAssociation;
import br.com.kbmg.wsmusiccontrol.model.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventMusicAssociationRepository extends JpaRepository<EventMusicAssociation, String> {

    @Query("SELECT ema.music FROM EventMusicAssociation ema where ema.event = :event")
    List<Music> findAllMusicByEvent(Event event);
}
