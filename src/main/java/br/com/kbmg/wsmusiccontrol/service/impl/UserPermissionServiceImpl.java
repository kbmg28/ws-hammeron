package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.model.UserPermission;
import br.com.kbmg.wsmusiccontrol.repository.UserPermissionRepository;
import br.com.kbmg.wsmusiccontrol.service.UserPermissionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserPermissionServiceImpl
        extends GenericServiceImpl<UserPermission, UserPermissionRepository>
        implements UserPermissionService {

    @Override
    public List<UserPermission> findByPermission(PermissionEnum permissionEnum) {
        return repository.findByPermission(permissionEnum);
    }
}
