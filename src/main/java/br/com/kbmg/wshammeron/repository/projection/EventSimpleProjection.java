package br.com.kbmg.wshammeron.repository.projection;

import java.sql.Timestamp;

public interface EventSimpleProjection {

    String getEventId();
    Timestamp getDateTimeEvent();
    String getName();

}
