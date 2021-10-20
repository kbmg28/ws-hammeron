package br.com.kbmg.wsmusiccontrol.dto.space;

import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySpace {

    private Long spaceId;
    private String name;

    //TODO: Modify mapper to owner list
    private UserDto spaceOwner;
}
