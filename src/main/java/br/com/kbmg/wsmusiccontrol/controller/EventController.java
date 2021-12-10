package br.com.kbmg.wsmusiccontrol.controller;

import br.com.kbmg.wsmusiccontrol.config.security.SpringSecurityUtil;
import br.com.kbmg.wsmusiccontrol.config.security.annotations.SecuredAnyUserAuth;
import br.com.kbmg.wsmusiccontrol.config.security.annotations.SecuredSpaceOwner;
import br.com.kbmg.wsmusiccontrol.dto.event.EventDetailsDto;
import br.com.kbmg.wsmusiccontrol.dto.event.EventDto;
import br.com.kbmg.wsmusiccontrol.dto.event.EventWithMusicListDto;
import br.com.kbmg.wsmusiccontrol.enums.RangeDateFilterEnum;
import br.com.kbmg.wsmusiccontrol.service.EventService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
@SecuredAnyUserAuth
public class EventController extends GenericController {

    @Autowired
    private EventService eventService;


    @GetMapping
    @Transactional
    public ResponseEntity<ResponseData<List<EventDto>>> findAllEvents(
            @RequestParam(name = "nextEvents", required = true) Boolean nextEvents,
            @RequestParam(name = "rangeDate", required = false) RangeDateFilterEnum rangeDate) {
        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        List<EventDto> list = eventService.findAllEventsBySpace(spaceId, nextEvents, rangeDate);
        return super.ok(list);
    }

    @GetMapping("/{id-event}")
    @Transactional
    public ResponseEntity<ResponseData<EventDetailsDto>> findById(
            @PathVariable("id-event") String idMusic) {
        EventDetailsDto data = eventService.findByIdValidated( idMusic);
        return super.ok(data);
    }

    @PostMapping
    @Transactional
    @SecuredSpaceOwner
    public ResponseEntity<ResponseData<EventDto>> createEvent(
            @RequestBody @Valid EventWithMusicListDto body) {
        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        EventDto data = eventService.createEvent(spaceId, body);
        return super.ok(data);
    }

    @PutMapping("/{id-event}")
    @Transactional
    public ResponseEntity<ResponseData<EventDto>> updateEvent(
            @PathVariable("id-event") String idEvent,
            @RequestBody @Valid EventWithMusicListDto body) {
        String spaceId = SpringSecurityUtil.getCredentials().getSpaceId();
        EventDto data = eventService.editEvent(spaceId,idEvent, body);
        return super.ok(data);
    }

}
