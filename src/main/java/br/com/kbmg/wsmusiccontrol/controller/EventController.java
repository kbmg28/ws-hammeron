package br.com.kbmg.wsmusiccontrol.controller;

import br.com.kbmg.wsmusiccontrol.config.security.annotations.SecuredAnyUserAuth;
import br.com.kbmg.wsmusiccontrol.dto.event.EventWithMusicListDto;
import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.service.EventService;
import br.com.kbmg.wsmusiccontrol.util.response.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/spaces/{space-id}/events")
@CrossOrigin(origins = "*")
@SecuredAnyUserAuth
public class EventController extends GenericController {

    @Autowired
    private EventService eventService;


    @GetMapping
    @Transactional
    public ResponseEntity<ResponseData<Set<EventWithMusicListDto>>> findAllEvents(
            @PathVariable("space-id") String spaceId,
            @RequestParam(name = "startFilter") Optional<LocalDate> startFilter,
            @RequestParam(name = "endFilter") Optional<LocalDate> endFilter) {
//        startFilter.
        Set<EventWithMusicListDto> list = eventService.findAllEventsBySpace(spaceId, null,null);
        return super.ok(list);
    }

    @GetMapping("/{id-event}")
    @Transactional
    public ResponseEntity<ResponseData<EventWithMusicListDto>> findById(
            @PathVariable("space-id") String spaceId,
            @PathVariable("id-event") String idMusic) {
        EventWithMusicListDto data = eventService.findBySpaceAndId(spaceId, idMusic);
        return super.ok(data);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ResponseData<EventWithMusicListDto>> createEvent(
            @PathVariable("space-id") String spaceId,
            @RequestBody @Valid EventWithMusicListDto body) {
        EventWithMusicListDto data = eventService.createEvent(spaceId, body);
        return super.ok(data);
    }

}
