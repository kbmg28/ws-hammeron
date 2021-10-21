package br.com.kbmg.wsmusiccontrol.repository;

import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, String> {

    List<UserPermission> findByPermission(PermissionEnum permissionEnum);

    @Query("SELECT up from UserPermission up where up.permission = :permissionEnum AND exists(" +
            "SELECT 1 from SpaceUserAppAssociation spaceAssociation " +
            "   where up.userApp = spaceAssociation.userApp and " +
            "       spaceAssociation.space = :space" +
            ")")
    List<UserPermission> findByPermissionAndSpace(PermissionEnum permissionEnum, Space space);
}
