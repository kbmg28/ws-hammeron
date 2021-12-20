package br.com.kbmg.wsmusiccontrol.event.view;

import br.com.kbmg.wsmusiccontrol.model.Event;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SmsRememberData {
    private String uuidScheduler;
    private Event eventHammerOn;
}
