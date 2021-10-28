package br.com.kbmg.wsmusiccontrol.service;

import br.com.kbmg.wsmusiccontrol.dto.space.SpaceRequestDto;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.UserApp;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface SpaceService extends GenericService<Space>{
    Space findOrCreatePublicSpace();

    void requestNewSpaceForUser(SpaceRequestDto spaceRequestDto, HttpServletRequest request);

    Space findByIdAndUserAppValidated(String spaceId, UserApp userApp);

    void approveNewSpaceForUser(String idSpace, HttpServletRequest request);

    List<Space> findAllSpaceToApprove();

    List<Space> findAllSpacesByUserApp();

    Space changeViewSpaceUser(String idSpace);

    Space findLastAccessedSpace();
}
