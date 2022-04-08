package br.com.kbmg.wshammeron.event.view;

import br.com.kbmg.wshammeron.dto.event.EventMainDataDto;
import br.com.kbmg.wshammeron.enums.DatabaseOperationEnum;
import br.com.kbmg.wshammeron.model.UserApp;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class UserOfEventOperation {
    private EventMainDataDto event;
    private Set<UserApp> userList;
    private DatabaseOperationEnum operation;
}
