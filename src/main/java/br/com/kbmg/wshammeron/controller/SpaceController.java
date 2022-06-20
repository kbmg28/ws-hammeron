package br.com.kbmg.wshammeron.controller;

import br.com.kbmg.wshammeron.config.security.annotations.SecuredAnyUserAuth;
import br.com.kbmg.wshammeron.config.security.annotations.SecuredSysAdmin;
import br.com.kbmg.wshammeron.dto.space.MySpace;
import br.com.kbmg.wshammeron.dto.space.SpaceApproveDto;
import br.com.kbmg.wshammeron.dto.space.SpaceDto;
import br.com.kbmg.wshammeron.dto.space.SpaceRequestDto;
import br.com.kbmg.wshammeron.dto.space.overview.SpaceOverviewDto;
import br.com.kbmg.wshammeron.enums.SpaceStatusEnum;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.service.SpaceService;
import br.com.kbmg.wshammeron.util.mapper.SpaceMapper;
import br.com.kbmg.wshammeron.util.response.ResponseData;
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
            @RequestBody @Valid SpaceApproveDto spaceApproveDto,
            HttpServletRequest request) {
        spaceService.approveNewSpaceForUser(idSpace, spaceApproveDto.getSpaceStatusEnum(), request);

        return super.ok();
    }

    @GetMapping("/status/{space-status}")
    @SecuredSysAdmin
    public ResponseEntity<ResponseData<List<SpaceDto>>> findAllSpaceToApprove(
            @PathVariable("space-status") SpaceStatusEnum spaceStatusEnum
    ) {
        List<Space> spaceList = spaceService.findAllSpaceByStatus(spaceStatusEnum);

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

    @GetMapping("/overview")
    public ResponseEntity<ResponseData<SpaceOverviewDto>> findSpaceOverview() {
        SpaceOverviewDto spaceOverview = spaceService.findSpaceOverview();

        return super.ok(spaceOverview);
    }

    @PutMapping("/{id-space}/change-view")
    public ResponseEntity<ResponseData<String>> changeViewSpaceUser(
            @PathVariable("id-space") String idSpace,
            HttpServletRequest request) {
        String tokenUpdated = spaceService.changeViewSpaceUser(idSpace, request);
        return super.ok(tokenUpdated);
    }

}
