package br.com.kbmg.wsmusiccontrol.event.view;

import br.com.kbmg.wsmusiccontrol.dto.event.EventMainDataDto;
import br.com.kbmg.wsmusiccontrol.enums.DatabaseOperationEnum;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
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
