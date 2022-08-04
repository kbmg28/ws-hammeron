package br.com.kbmg.wshammeron.repository.projection;

import java.sql.Timestamp;

public interface EventWithTotalAssociationsProjection {

     String getEventId();
     String getNameEvent();
     Timestamp getDateTimeEvent();
     Integer getMusicQuantity();
     Integer getUserQuantity();
     Boolean getIsUserLoggedIncluded();
     Boolean getHasMusicId();

}
