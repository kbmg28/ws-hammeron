package br.com.kbmg.wshammeron.repository;

import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {

    VerificationToken findByTokenAndUserApp(String token, UserApp userApp);

    Optional<VerificationToken> findByUserApp(UserApp userApp);

    void deleteByUserApp(UserApp userApp);
}
