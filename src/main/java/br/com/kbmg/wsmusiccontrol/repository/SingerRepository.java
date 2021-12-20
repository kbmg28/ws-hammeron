package br.com.kbmg.wsmusiccontrol.repository;

import br.com.kbmg.wsmusiccontrol.model.Singer;
import br.com.kbmg.wsmusiccontrol.model.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SingerRepository extends JpaRepository<Singer, String> {

    Optional<Singer> findByNameIgnoreCase(String name);

    @Query("SELECT s FROM Singer s where exists (SELECT 1 from Music m where m.space = :space and m.singer = s)")
    List<Singer> findAllBySpace(Space space);
}
