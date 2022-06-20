package br.com.kbmg.wshammeron.repository.projection;

import java.time.LocalDate;

public interface EventSimpleProjection {

    String getEventId();
    LocalDate getDate();
    String getName();

}
