package br.com.kbmg.wsmusiccontrol.controller;

import br.com.kbmg.wsmusiccontrol.config.security.annotations.SecuredAnyUserAuth;
import br.com.kbmg.wsmusiccontrol.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/event")
@CrossOrigin(origins = "*")
@SecuredAnyUserAuth
public class EventController extends GenericController {

    @Autowired
    private EventService eventService;


}
