package builder;

import br.com.kbmg.wshammeron.dto.space.MySpace;
import br.com.kbmg.wshammeron.dto.space.SpaceApproveDto;
import br.com.kbmg.wshammeron.dto.space.SpaceDto;
import br.com.kbmg.wshammeron.dto.space.SpaceRequestDto;
import br.com.kbmg.wshammeron.dto.space.overview.EventOverviewDto;
import br.com.kbmg.wshammeron.dto.space.overview.MusicOverviewDto;
import br.com.kbmg.wshammeron.dto.space.overview.SpaceOverviewDto;
import br.com.kbmg.wshammeron.dto.space.overview.UserOverviewDto;
import br.com.kbmg.wshammeron.dto.user.UserDto;
import br.com.kbmg.wshammeron.enums.EventTypeEnum;
import br.com.kbmg.wshammeron.enums.MusicStatusEnum;
import br.com.kbmg.wshammeron.enums.PermissionEnum;
import br.com.kbmg.wshammeron.enums.SpaceStatusEnum;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.model.UserPermission;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static constants.BaseTestsConstants.ANY_VALUE;

public abstract class SpaceBuilder {

    public static Space generateSpace(UserApp userApp) {
        Space space = new Space();

        space.setName(ANY_VALUE);
        space.setJustification(ANY_VALUE);
        space.setRequestedBy(userApp);
        space.setApprovedBy(userApp);
        space.setRequestedByDate(LocalDateTime.now());
        space.setApprovedByDate(LocalDateTime.now());
        space.setSpaceStatus(SpaceStatusEnum.APPROVED);

        return space;
    }

    public static SpaceUserAppAssociation generateSpaceUserAppAssociation(Space space, UserApp userApp, Set<UserPermission> userPermissions) {
        SpaceUserAppAssociation spaceUserAppAssociation = new SpaceUserAppAssociation();

        spaceUserAppAssociation.setSpace(space);
        spaceUserAppAssociation.setUserApp(userApp);
        spaceUserAppAssociation.setLastAccessedSpace(true);
        spaceUserAppAssociation.setActive(true);
        spaceUserAppAssociation.setUserPermissionList(userPermissions);

        space.getSpaceUserAppAssociationList().add(spaceUserAppAssociation);

        return spaceUserAppAssociation;
    }

    public static SpaceRequestDto generateSpaceRequestDto(Space space) {
        SpaceRequestDto spaceRequestDto = new SpaceRequestDto();

        spaceRequestDto.setName(space.getName());
        spaceRequestDto.setJustification(space.getJustification());

        return spaceRequestDto;
    }

    public static SpaceApproveDto generateSpaceApproveDto() {
        return new SpaceApproveDto(SpaceStatusEnum.APPROVED);
    }

    public static SpaceOverviewDto generateSpaceOverviewDto(Space space, UserApp userApp) {
        String spaceId = space.getId();
        String spaceName = space.getName();
        String createdByFormatted = String.format("%s (%s)", userApp.getName(), userApp.getEmail());

        UserOverviewDto userOverviewDto = new UserOverviewDto(PermissionEnum.PARTICIPANT.name(), 1L);
        MusicOverviewDto musicOverviewDto = new MusicOverviewDto(MusicStatusEnum.ENABLED.name(), 1L);
        EventOverviewDto eventOverviewDto = new EventOverviewDto(EventTypeEnum.NEXT.name(), 1L);

        space.setSpaceStatus(SpaceStatusEnum.APPROVED);

        return new SpaceOverviewDto(spaceId,
                spaceName,
                createdByFormatted,
                List.of(userOverviewDto),
                List.of(musicOverviewDto),
                List.of(eventOverviewDto)
        );
    }

    public static SpaceDto generateSpaceDto(Space space, UserApp userLogged) {
        UserDto userDto = UserBuilder.generateUserDto(userLogged.getEmail());

        SpaceDto spaceRequestDto = new SpaceDto();

        spaceRequestDto.setName(space.getName());
        spaceRequestDto.setJustification(space.getJustification());
        spaceRequestDto.setSpaceId(space.getId());
        spaceRequestDto.setRequestedBy(userDto);
        spaceRequestDto.setRequestedByDate(space.getRequestedByDate());

        if (!space.getSpaceStatus().equals(SpaceStatusEnum.REQUESTED)) {
            spaceRequestDto.setApprovedBy(userDto);
            spaceRequestDto.setApprovedByDate(space.getApprovedByDate());
        }

        spaceRequestDto.setSpaceStatus(space.getSpaceStatus());

        return spaceRequestDto;
    }

    public static MySpace generateMySpace(Space space) {
        return generateMySpace(space, null);
    }
    public static MySpace generateMySpace(Space space, Boolean isLastAccessed) {
        MySpace mySpace = new MySpace();

        mySpace.setName(space.getName());
        mySpace.setSpaceId(space.getId());

        mySpace.setLastAccessed(isLastAccessed);

        return mySpace;
    }

}
