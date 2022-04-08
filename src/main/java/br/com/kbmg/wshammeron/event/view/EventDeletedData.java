package br.com.kbmg.wshammeron.event.view;

import br.com.kbmg.wshammeron.model.UserApp;
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
