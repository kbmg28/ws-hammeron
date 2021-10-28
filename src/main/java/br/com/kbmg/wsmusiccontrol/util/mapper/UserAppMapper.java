package br.com.kbmg.wsmusiccontrol.util.mapper;

import br.com.kbmg.wsmusiccontrol.dto.user.UserDto;
import br.com.kbmg.wsmusiccontrol.dto.user.UserWithPermissionDto;
import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.model.UserPermission;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserAppMapper {
    String TO_USER_DTO = "toUserDto";
    String TO_USER_WITH_PERMISSION_DTO = "toUserWithPermissionDto";
    String TO_PERMISSION_ENUM = "toPermissionEnum";
    String TO_PERMISSION_ENUM_LIST = "toPermissionEnumList";

    @IterableMapping(elementTargetType = UserWithPermissionDto.class, qualifiedByName = TO_USER_WITH_PERMISSION_DTO)
    Set<UserWithPermissionDto> toUserWithPermissionDtoList(Collection<UserApp> userAppList);

    @Named(TO_USER_WITH_PERMISSION_DTO)
    @Mapping(target = "permissionList", source = "userPermissionList", qualifiedByName = TO_PERMISSION_ENUM_LIST)
    UserWithPermissionDto toUserWithPermissionDto(UserApp userApp);

    @Named(TO_PERMISSION_ENUM_LIST)
    @IterableMapping(elementTargetType = PermissionEnum.class, qualifiedByName = TO_PERMISSION_ENUM)
    Set<PermissionEnum> toPermissionEnumList(Set<UserPermission> userPermissionList);

    @Named(TO_PERMISSION_ENUM)
    static PermissionEnum toPermissionEnum(UserPermission userPermission){
        return (userPermission == null) ? null : userPermission.getPermission();
    }

    @IterableMapping(elementTargetType = UserDto.class, qualifiedByName = TO_USER_DTO)
    Set<UserDto> toUserDtoList(List<UserPermission> userPermissionList);

    @Named(TO_USER_DTO)
    @Mapping(target = "name", source = "userApp.name")
    @Mapping(target = "email", source = "userApp.email")
    @Mapping(target = "cellPhone", source = "userApp.cellPhone")
    UserDto toUserDto(UserPermission userPermission);

}
