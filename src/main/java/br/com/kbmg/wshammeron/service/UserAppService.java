package br.com.kbmg.wshammeron.service;

import br.com.kbmg.wshammeron.dto.user.RegisterDto;
import br.com.kbmg.wshammeron.dto.user.RegisterPasswordDto;
import br.com.kbmg.wshammeron.dto.user.UserDto;
import br.com.kbmg.wshammeron.dto.user.UserWithPermissionDto;
import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.repository.projection.UserOnlyIdNameAndEmailProjection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserAppService extends GenericService<UserApp>{
    UserApp registerNewUserAccount(RegisterDto userDto);
    void saveUserEnabled(UserApp userApp);
    UserApp findByEmailOrCreateIfNotExists(String email);
    Optional<UserApp> findByEmail(String email);
    void registerUserPassword(RegisterPasswordDto registerPasswordDto);
    void encodePasswordAndSave(UserApp userApp, String password, LocalDateTime expireDate);

    List<UserApp> findAllSysAdmin();

    UserApp findUserLogged();

    Set<UserWithPermissionDto> findAllBySpace(String spaceId);

    void addPermissionToUserInSpace(String emailUser, String spaceId, PermissionEnum permissionEnum);

    List<UserOnlyIdNameAndEmailProjection> findUsersAssociationForEventsBySpace(String spaceId);

    UserApp updateUserLogged(UserDto body);

    void deleteCascade(String email);
}
