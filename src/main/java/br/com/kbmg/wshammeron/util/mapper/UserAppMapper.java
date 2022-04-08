package br.com.kbmg.wshammeron.util.mapper;

import br.com.kbmg.wshammeron.dto.user.UserDto;
import br.com.kbmg.wshammeron.dto.user.UserOnlyIdNameAndEmailDto;
import br.com.kbmg.wshammeron.dto.user.UserWithPermissionDto;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.repository.projection.UserOnlyIdNameAndEmailProjection;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserAppMapper {
    String TO_USER_DTO_FROM_USER_PERMISSION = "toUserDtoFromUserPermission";
    String TO_USER_WITH_PERMISSION_DTO = "toUserWithPermissionDto";
    String TO_PERMISSION_ENUM = "toPermissionEnum";
    String TO_PERMISSION_ENUM_LIST = "toPermissionEnumList";
    String TO_USER_ONLY_ID_AND_NAME_AND_EMAIL_FROM_ENTITY = "toUserOnlyIdNameAndEmailDtoFromEntity";

    @IterableMapping(elementTargetType = UserWithPermissionDto.class, qualifiedByName = TO_USER_WITH_PERMISSION_DTO)
    Set<UserWithPermissionDto> toUserWithPermissionDtoList(Collection<UserApp> userAppList);

    @Named(TO_USER_WITH_PERMISSION_DTO)
    @Mapping(target = "permissionList", ignore = true)
    UserWithPermissionDto toUserWithPermissionDto(UserApp userApp);

    @IterableMapping(elementTargetType = UserDto.class)
    Set<UserDto> toUserDtoList(List<UserApp> userAppList);

    @IterableMapping(elementTargetType = UserOnlyIdNameAndEmailDto.class)
    List<UserOnlyIdNameAndEmailDto> toUserOnlyIdNameAndEmailDto(List<UserOnlyIdNameAndEmailProjection> projectionList);

}
