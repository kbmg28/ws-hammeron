package br.com.kbmg.wshammeron.repository;

import br.com.kbmg.wshammeron.model.MusicLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicLinkRepository extends JpaRepository<MusicLink, String> {

}
