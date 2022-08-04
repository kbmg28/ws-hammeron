package br.com.kbmg.wshammeron.controller;

import br.com.kbmg.wshammeron.config.security.SpringSecurityUtil;
import br.com.kbmg.wshammeron.config.security.annotations.SecuredAnyUserAuth;
import br.com.kbmg.wshammeron.config.security.annotations.SecuredSpaceOwner;
import br.com.kbmg.wshammeron.dto.music.MusicDto;
import br.com.kbmg.wshammeron.dto.music.MusicOnlyIdAndMusicNameAndSingerNameDto;
import br.com.kbmg.wshammeron.dto.music.MusicTopUsedDto;
import br.com.kbmg.wshammeron.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wshammeron.dto.music.SingerDto;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.Singer;
import br.com.kbmg.wshammeron.repository.projection.MusicOnlyIdAndMusicNameAndSingerNameProjection;
import br.com.kbmg.wshammeron.service.MusicService;
import br.com.kbmg.wshammeron.service.SingerService;
import br.com.kbmg.wshammeron.util.mapper.MusicMapper;
import br.com.kbmg.wshammeron.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/musics")
@CrossOrigin(origins = "*")
@SecuredAnyUserAuth
public class MusicController extends GenericController {

    @Autowired
    private MusicService musicService;

    @Autowired
    private SingerService singerService;

    @Autowired
    private MusicMapper musicMapper;

    @GetMapping
    @Transactional
    public ResponseEntity<ResponseData<Set<MusicWithSingerAndLinksDto>>> findAllMusic() {
        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        List<Music> entityData = musicService.findAllBySpace(spaceId);
        Set<MusicWithSingerAndLinksDto> viewData = musicMapper.toMusicWithSingerAndLinksDtoListIgnoreLinks(entityData);
        return super.ok(viewData);
    }

    @GetMapping("/events")
    @Transactional
    public ResponseEntity<ResponseData<List<MusicTopUsedDto>>> findTop10MusicMoreUsedInEvents() {
        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        List<MusicTopUsedDto> viewData = musicService.findTop10MusicMoreUsedInEvents(spaceId);
        return super.ok(viewData);
    }

    @GetMapping("/association-for-events")
    public ResponseEntity<ResponseData<List<MusicOnlyIdAndMusicNameAndSingerNameDto>>>
            findMusicsAssociationForEventsBySpace() {
        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        List<MusicOnlyIdAndMusicNameAndSingerNameProjection> projectionList = musicService.findMusicsAssociationForEventsBySpace(spaceId);
        List<MusicOnlyIdAndMusicNameAndSingerNameDto> viewData = musicMapper.toMusicOnlyIdAndMusicNameAndSingerNameDto(projectionList);
        return super.ok(viewData);
    }

    @GetMapping("/{id-music}")
    @Transactional
    public ResponseEntity<ResponseData<MusicDto>> findById(
            @PathVariable("id-music") String idMusic,
            @RequestParam(required = true) Boolean eventsFromTheLast3Months) {
        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        MusicDto data = musicService.findBySpaceAndId(spaceId, idMusic, eventsFromTheLast3Months);
        return super.ok(data);
    }

    @GetMapping("/singers")
    @Transactional
    public ResponseEntity<ResponseData<Set<SingerDto>>> findAllSinger() {
        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        List<Singer> entityData = singerService.findAllBySpace(spaceId);
        Set<SingerDto> viewData = musicMapper.toSingerDtoList(entityData);
        return super.ok(viewData);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ResponseData<MusicWithSingerAndLinksDto>> createMusic(
            @RequestBody @Valid MusicWithSingerAndLinksDto musicWithSingerAndLinksDto) {

        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        Music entityData = musicService.createMusic(spaceId, musicWithSingerAndLinksDto);
        MusicWithSingerAndLinksDto viewData = musicMapper.toMusicWithSingerAndLinksDto(entityData);

        return super.ok(viewData);
    }

    @PutMapping("/{id-music}")
    @Transactional
    public ResponseEntity<ResponseData<MusicWithSingerAndLinksDto>> updateMusic(
            @PathVariable("id-music") String idMusic,
            @RequestBody @Valid MusicWithSingerAndLinksDto musicWithSingerAndLinksDto) {
        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        Music entityData = musicService.updateMusic(spaceId, idMusic, musicWithSingerAndLinksDto);
        MusicWithSingerAndLinksDto viewData = musicMapper.toMusicWithSingerAndLinksDto(entityData);
        return super.ok(viewData);
    }

    @DeleteMapping("/{id-music}")
    @Transactional
    @SecuredSpaceOwner
    public ResponseEntity<ResponseData<Void>> deleteMusic(
            @PathVariable("id-music") String idMusic) {
        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        musicService.deleteMusic(spaceId, idMusic);
        return super.ok();
    }

}
