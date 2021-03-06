package br.com.kbmg.wshammeron.repository;

import br.com.kbmg.wshammeron.dto.user.UserWithSinglePermissionDto;
import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, String> {

    List<UserPermission> findByPermission(PermissionEnum permissionEnum);

    @Query("SELECT up.permission from UserPermission up where exists(" +
            "SELECT 1 from SpaceUserAppAssociation spaceAssociation " +
            "   where up.spaceUserAppAssociation = spaceAssociation and " +
            "       spaceAssociation.userApp = :userApp and spaceAssociation.space = :space " +
            ")")
    List<PermissionEnum> findAllByUserAppAndSpace(UserApp userApp, Space space);

    @Query("SELECT new br.com.kbmg.wshammeron.dto.user.UserWithSinglePermissionDto(u.email, up.permission) " +
            "   FROM UserPermission up " +
            "       JOIN up.spaceUserAppAssociation sua " +
            "       JOIN sua.userApp u " +
            "   WHERE sua.space = :space and u.email IN (:emailList)")
    List<UserWithSinglePermissionDto> findBySpaceAndEmailList(Space space, Set<String> emailList);
}