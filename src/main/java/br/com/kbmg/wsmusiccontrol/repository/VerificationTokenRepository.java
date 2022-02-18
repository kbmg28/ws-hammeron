package br.com.kbmg.wsmusiccontrol.repository;

import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, String> {

    VerificationToken findByTokenAndUserApp(String token, UserApp userApp);

    Optional<VerificationToken> findByUserApp(UserApp userApp);

    void deleteByUserApp(UserApp userApp);
}
