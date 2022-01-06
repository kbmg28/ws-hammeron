package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.space.SpaceRequestDto;
import br.com.kbmg.wsmusiccontrol.dto.space.overview.SpaceOverviewDto;
import br.com.kbmg.wsmusiccontrol.enums.SpaceStatusEnum;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserApp;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface SpaceService extends GenericService<Space>{
    Space findOrCreatePublicSpace();

    void requestNewSpaceForUser(SpaceRequestDto spaceRequestDto, HttpServletRequest request);

    Space findByIdValidated(String spaceId);

    Space findByIdAndUserAppValidated(String spaceId, UserApp userApp);

    void approveNewSpaceForUser(String idSpace, SpaceStatusEnum spaceStatusEnum,  HttpServletRequest request);

    List<Space> findAllSpaceByStatus(SpaceStatusEnum spaceStatusEnum);

    List<Space> findAllSpacesByUserApp();

    String changeViewSpaceUser(String idSpace, HttpServletRequest request);

    Space findLastAccessedSpace();

    SpaceOverviewDto findSpaceOverview();
}
