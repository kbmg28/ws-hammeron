package br.com.kbmg.wsmusiccontrol.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Entity
@Data
public class VerificationToken extends AbstractEntity {
    private static final int EXPIRATION = 60 * 24;

    private String token;

    @OneToOne(targetEntity = UserApp.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_app_id")
    private UserApp userApp;

    private Date expiryDate;

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public VerificationToken(String token, UserApp userApp) {
        this.token = token;
        this.userApp = userApp;
    }

    @PrePersist
    protected void onSave() {
        expiryDate = calculateExpiryDate(30);
    }
}
