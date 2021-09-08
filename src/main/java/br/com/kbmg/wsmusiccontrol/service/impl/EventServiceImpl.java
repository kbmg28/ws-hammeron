package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.model.Event;
import br.com.kbmg.wsmusiccontrol.repository.EventRepository;
import br.com.kbmg.wsmusiccontrol.service.EventService;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl extends GenericServiceImpl<Event, EventRepository> implements EventService {

}
