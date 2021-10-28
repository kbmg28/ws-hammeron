package br.com.kbmg.wsmusiccontrol.repository;

import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.model.Singer;
import br.com.kbmg.wsmusiccontrol.model.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MusicRepository extends JpaRepository<Music, String> {

    Optional<Music> findByNameIgnoreCaseAndSingerAndSpace(String name, Singer singer, Space space);

    List<Music> findAllBySpace(Space space);

    Optional<Music> findBySpaceAndId(Space space, String idMusic);
}
