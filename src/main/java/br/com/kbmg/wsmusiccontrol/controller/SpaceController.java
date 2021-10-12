package br.com.kbmg.wsmusiccontrol.controller;

import br.com.kbmg.wsmusiccontrol.config.security.SecuredAdminOrUser;
import br.com.kbmg.wsmusiccontrol.dto.space.SpaceRequestDto;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/space")
@CrossOrigin(origins = "*")
@SecuredAdminOrUser
public class SpaceController extends GenericController {

    @Autowired
    private SpaceService spaceService;

    @PostMapping("/request")
    public ResponseEntity<ResponseData<Void>> requestNewSpaceForUser(
            @RequestBody @Valid SpaceRequestDto spaceRequestDto,
            HttpServletRequest request) {
        spaceService.requestNewSpaceForUser(spaceRequestDto, request);

        return super.ok();
    }


}
