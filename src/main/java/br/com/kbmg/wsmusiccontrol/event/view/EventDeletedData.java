package br.com.kbmg.wsmusiccontrol.event.view;

import br.com.kbmg.wsmusiccontrol.model.UserApp;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
@AllArgsConstructor
public class EventDeletedData {
    private String nameEvent;
    private LocalDate dateEvent;
    private LocalTime timeEvent;
    private Set<UserApp> userList;
}
