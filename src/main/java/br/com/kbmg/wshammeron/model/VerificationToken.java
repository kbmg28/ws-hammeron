package br.com.kbmg.wshammeron.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
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
    public static final int EXPIRATION_TIME_MINUTES = 10;

    @Column(nullable = false)
    private String token;

    @OneToOne(targetEntity = UserApp.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_app_id")
    private UserApp userApp;

    @Column(nullable = false)
    private Date expiryDate;

    public VerificationToken(String token, UserApp userApp) {
        this.token = token;
        this.userApp = userApp;
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public Boolean isValid() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));

        Date now = new Date(cal.getTime().getTime());

        return now.before(expiryDate);
    }

    @PrePersist
    protected void onSave() {
        expiryDate = calculateExpiryDate(EXPIRATION_TIME_MINUTES);
    }
}
