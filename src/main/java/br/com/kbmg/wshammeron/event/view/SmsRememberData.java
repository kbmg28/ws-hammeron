package br.com.kbmg.wshammeron.event.view;

import br.com.kbmg.wshammeron.model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SmsRememberData {
    private String uuidScheduler;
    private Event eventHammerOn;
}
