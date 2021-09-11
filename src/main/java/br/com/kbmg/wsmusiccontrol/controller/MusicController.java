package br.com.kbmg.wsmusiccontrol.controller;

import br.com.kbmg.wsmusiccontrol.config.security.SecuredAdminOrUser;
import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.dto.music.SingerDto;
import br.com.kbmg.wsmusiccontrol.enums.MusicStatusEnum;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.model.Singer;
import br.com.kbmg.wsmusiccontrol.service.MusicService;
import br.com.kbmg.wsmusiccontrol.service.SingerService;
import br.com.kbmg.wsmusiccontrol.util.mapper.MusicMapper;
import br.com.kbmg.wsmusiccontrol.util.response.ResponseData;
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
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/musics")
@CrossOrigin(origins = "*")
@SecuredAdminOrUser
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
        List<Music> entityData = musicService.findAll();
        Set<MusicWithSingerAndLinksDto> viewData = musicMapper.toMusicWithSingerAndLinksDtoList(entityData);
        return super.ok(viewData);
    }

    @GetMapping("/{id-music}")
    @Transactional
    public ResponseEntity<ResponseData<MusicWithSingerAndLinksDto>> findById(
            @PathVariable("id-music") Long idMusic) {
        Music entityData = musicService.findByIdValidated(idMusic);
        MusicWithSingerAndLinksDto viewData = musicMapper.toMusicWithSingerAndLinksDto(entityData);
        return super.ok(viewData);
    }

    @GetMapping("/singers")
    @Transactional
    public ResponseEntity<ResponseData<Set<SingerDto>>> findAllSinger() {
        List<Singer> entityData = singerService.findAll();
        Set<SingerDto> viewData = musicMapper.toSingerDtoList(entityData);
        return super.ok(viewData);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ResponseData<MusicWithSingerAndLinksDto>> createMusic(
            @RequestBody @Valid MusicWithSingerAndLinksDto musicWithSingerAndLinksDto) {

        Music entityData = musicService.createMusic(musicWithSingerAndLinksDto);
        MusicWithSingerAndLinksDto viewData = musicMapper.toMusicWithSingerAndLinksDto(entityData);

        return super.ok(viewData);
    }

    @PutMapping("/{id-music}")
    @Transactional
    public ResponseEntity<ResponseData<MusicWithSingerAndLinksDto>> updateMusic(
            @PathVariable("id-music") Long idMusic,
            @RequestBody @Valid MusicWithSingerAndLinksDto musicWithSingerAndLinksDto) {
        Music entityData = musicService.updateMusic(idMusic, musicWithSingerAndLinksDto);
        MusicWithSingerAndLinksDto viewData = musicMapper.toMusicWithSingerAndLinksDto(entityData);
        return super.ok(viewData);
    }

    @PutMapping("/{id-music}/status/{new-status}")
    @Transactional
    public ResponseEntity<ResponseData<Void>> updateStatusMusic(
            @PathVariable("id-music") Long idMusic,
            @PathVariable("new-status") MusicStatusEnum newStatus) {
        musicService.updateStatusMusic(idMusic, newStatus);
        return super.ok();
    }

    @DeleteMapping("/{id-music}")
    @Transactional
    public ResponseEntity<ResponseData<Void>> deleteMusic(
            @PathVariable("id-music") Long idMusic) {
        musicService.deleteMusic(idMusic);
        return super.ok();
    }

}
