package br.com.kbmg.wsmusiccontrol.repository.projection;

import java.time.LocalDate;
import java.time.LocalTime;

public interface EventWithTotalAssociationsProjection {

     String getEventId();
     String getNameEvent();
     LocalDate getDateEvent();
     LocalTime getTimeEvent();
     Integer getMusicQuantity();
     Integer getUserQuantity();
     Boolean getIsUserLoggedIncluded();

}
