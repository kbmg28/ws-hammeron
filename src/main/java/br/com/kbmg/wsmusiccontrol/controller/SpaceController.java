package br.com.kbmg.wsmusiccontrol.controller;

import br.com.kbmg.wsmusiccontrol.config.security.annotations.SecuredAnyUserAuth;
import br.com.kbmg.wsmusiccontrol.config.security.annotations.SecuredSysAdmin;
import br.com.kbmg.wsmusiccontrol.dto.space.MySpace;
import br.com.kbmg.wsmusiccontrol.dto.space.SpaceDto;
import br.com.kbmg.wsmusiccontrol.dto.space.SpaceRequestDto;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.service.SpaceService;
import br.com.kbmg.wsmusiccontrol.util.mapper.SpaceMapper;
import br.com.kbmg.wsmusiccontrol.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/spaces")
@CrossOrigin(origins = "*")
@SecuredAnyUserAuth
@Transactional
public class SpaceController extends GenericController {

    @Autowired
    private SpaceService spaceService;

    @Autowired
    private SpaceMapper spaceMapper;

    @PostMapping("/request")
    public ResponseEntity<ResponseData<Void>> requestNewSpaceForUser(
            @RequestBody @Valid SpaceRequestDto spaceRequestDto,
            HttpServletRequest request) {
        spaceService.requestNewSpaceForUser(spaceRequestDto, request);

        return super.ok();
    }

    @PostMapping("/{id-space}/approve")
    @SecuredSysAdmin
    public ResponseEntity<ResponseData<Void>> approveNewSpaceForUser(
            @PathVariable("id-space") String idSpace,
            HttpServletRequest request) {
        spaceService.approveNewSpaceForUser(idSpace, request);

        return super.ok();
    }

    @GetMapping("/approve")
    @SecuredSysAdmin
    public ResponseEntity<ResponseData<List<SpaceDto>>> findAllSpaceToApprove() {
        List<Space> spaceList = spaceService.findAllSpaceToApprove();

        return super.ok(spaceMapper.toSpaceDtoList(spaceList));
    }

    @GetMapping("")
    public ResponseEntity<ResponseData<List<MySpace>>> findAllSpacesByUserApp() {
        List<Space> spaceList = spaceService.findAllSpacesByUserApp();

        return super.ok(spaceMapper.toMySpaceDtoList(spaceList));
    }

    @GetMapping("/last")
    public ResponseEntity<ResponseData<MySpace>> findLastAccessedSpace() {
        Space space = spaceService.findLastAccessedSpace();

        return super.ok(new MySpace(space.getId(), space.getName(), true));
    }

    @PutMapping("/{id-space}/change-view")
    public ResponseEntity<ResponseData<MySpace>> changeViewSpaceUser(
            @PathVariable("id-space") String idSpace) {
        Space space = spaceService.changeViewSpaceUser(idSpace);

        return super.ok(spaceMapper.toMySpaceDto(space));
    }

}
