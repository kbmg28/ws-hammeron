package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.user.RegisterDto;
import br.com.kbmg.wsmusiccontrol.dto.user.RegisterPasswordDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserWithPermissionDto;
import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.projection.UserOnlyIdNameAndEmailProjection;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserAppService extends GenericService<UserApp>{
    UserApp registerNewUserAccount(RegisterDto userDto);
    void saveUserEnabled(UserApp userApp);
    UserApp findByEmailOrCreateIfNotExists(String email);
    Optional<UserApp> findByEmail(String email);
    void registerUserPassword(RegisterPasswordDto registerPasswordDto);
    void encodePasswordAndSave(UserApp userApp, String password);

    List<UserApp> findAllSysAdmin();

    UserApp findUserLogged();

    Set<UserWithPermissionDto> findAllBySpace(String spaceId);

    void addPermissionToUserInSpace(String emailUser, String spaceId, PermissionEnum permissionEnum);

    List<UserOnlyIdNameAndEmailProjection> findUsersAssociationForEventsBySpace(String spaceId);

    UserApp updateUserLogged(UserDto body);
}
