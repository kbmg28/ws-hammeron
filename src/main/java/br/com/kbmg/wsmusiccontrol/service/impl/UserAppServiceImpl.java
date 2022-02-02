package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.config.security.SpringSecurityUtil;
import br.com.kbmg.wsmusiccontrol.dto.user.RegisterDto;
import br.com.kbmg.wsmusiccontrol.dto.user.RegisterPasswordDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserWithPermissionDto;
import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.UserAppRepository;
import br.com.kbmg.wsmusiccontrol.repository.projection.UserOnlyIdNameAndEmailProjection;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.service.SpaceUserAppAssociationService;
import br.com.kbmg.wsmusiccontrol.service.UserAppService;
import br.com.kbmg.wsmusiccontrol.service.UserPermissionService;
import br.com.kbmg.wsmusiccontrol.util.mapper.UserAppMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants.USER_ALREADY_EXISTS;

@Service
public class UserAppServiceImpl extends GenericServiceImpl<UserApp, UserAppRepository> implements UserAppService {

    @Autowired
    private UserPermissionService userPermissionService;

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private SpaceUserAppAssociationService spaceUserAppAssociationService;

    @Autowired
    private UserAppMapper userAppMapper;

    @Override
    public UserApp registerNewUserAccount(RegisterDto userDto) {
        AtomicReference<UserApp> userAppAtomicReference = new AtomicReference<>(null);
        String email = userDto.getEmail().toLowerCase();

        repository.findByEmailIgnoreCase(email).ifPresent(userAppInDatabase -> {
            if (userAppInDatabase.getEnabled()) {
                throw new ServiceException(messagesService.get(USER_ALREADY_EXISTS));
            }
            userAppAtomicReference.set(userAppInDatabase);
        });

        if (userAppAtomicReference.get() == null) {
            UserApp newUserApp = new UserApp();

            newUserApp.setEmail(email);
            newUserApp.setEnabled(false);
            newUserApp.setIsSysAdmin(false);

            userAppAtomicReference.set(newUserApp);
        }

        UserApp userAppRegistered = userAppAtomicReference.get();
        userAppRegistered.setName(userDto.getName());
        userAppRegistered.setCellPhone(userDto.getCellPhone());

        return repository.save(userAppRegistered);
    }

    @Override
    public void registerUserPassword(RegisterPasswordDto registerPasswordDto) {
        this.findByEmail(registerPasswordDto.getEmail().toLowerCase())
                .ifPresent(user -> {
                    LocalDateTime expireDate = LocalDateTime.now().plusYears(1);
                    this.encodePasswordAndSave(user, registerPasswordDto.getPassword(), expireDate);
                });
    }

    @Override
    public void encodePasswordAndSave(UserApp userApp, String password, LocalDateTime expireDate) {
        String hashpw = BCrypt.hashpw(password, BCrypt.gensalt());
        userApp.setPassword(hashpw);
        userApp.setPasswordExpireDate(expireDate);
        repository.save(userApp);
    }

    @Override
    public List<UserApp> findAllSysAdmin() {
        return repository.findByIsSysAdminTrue();
    }

    @Override
    public UserApp findUserLogged() {
        return repository.findByEmailIgnoreCase(SpringSecurityUtil.getEmail()).orElse(null);
    }

    @Override
    public Set<UserWithPermissionDto> findAllBySpace(String spaceId) {
        Space space = spaceService.findByIdValidated(spaceId);
        List<UserApp> allBySpace = repository.findAllBySpace(space);
        Set<UserWithPermissionDto> viewData = userAppMapper.toUserWithPermissionDtoList(allBySpace);
        userPermissionService.checkPermissionsOfUsers(space, viewData);

        return viewData;
    }

    @Override
    public void addPermissionToUserInSpace(String emailUser, String spaceId, PermissionEnum permissionEnum) {
        UserApp userAppToAddRole = this.findByEmailOrCreateIfNotExists(emailUser);
        Space space = spaceService.findByIdValidated(spaceId);

        if (PermissionEnum.SPACE_OWNER.equals(permissionEnum)) {
            spaceUserAppAssociationService.createAssociationToSpaceOwner(space, userAppToAddRole);
        } else {
            spaceUserAppAssociationService.createAssociationToParticipant(space, userAppToAddRole);
        }

    }

    @Override
    public List<UserOnlyIdNameAndEmailProjection> findUsersAssociationForEventsBySpace(String spaceId) {
        return repository.findUsersAssociationForEventsBySpace(spaceId);
    }

    @Override
    public UserApp updateUserLogged(UserDto body) {
        UserApp userLogged = this.findUserLogged();
        userLogged.setName(body.getName());
        userLogged.setCellPhone(body.getCellPhone());

        return repository.save(userLogged);
    }

    @Override
    public void saveUserEnabled(UserApp userApp) {
        userApp.setEnabled(true);
        repository.save(userApp);
    }


    @Override
    public UserApp findByEmailOrCreateIfNotExists(String email) {
        return repository
                .findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    UserApp newUserApp = new UserApp();
                    newUserApp.setEmail(email);
                    newUserApp.setEnabled(false);
                    newUserApp.setIsSysAdmin(false);

                    return repository.save(newUserApp);
                });
    }

    @Override
    public Optional<UserApp> findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email);
    }

}
