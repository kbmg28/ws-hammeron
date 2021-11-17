package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.dto.user.UserOnlyIdNameAndEmailDto;
import br.com.kbmg.wsmusiccontrol.model.Event;
import br.com.kbmg.wsmusiccontrol.model.EventSpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.Space;
import br.com.kbmg.wsmusiccontrol.model.SpaceUserAppAssociation;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.repository.EventSpaceUserAppAssociationRepository;
import br.com.kbmg.wsmusiccontrol.service.EventSpaceUserAppAssociationService;
import br.com.kbmg.wsmusiccontrol.service.SpaceUserAppAssociationService;
import br.com.kbmg.wsmusiccontrol.util.mapper.UserAppMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EventSpaceUserAppAssociationServiceImpl extends GenericServiceImpl<EventSpaceUserAppAssociation, EventSpaceUserAppAssociationRepository> implements EventSpaceUserAppAssociationService {

    @Autowired
    private UserAppMapper userAppMapper;

    @Autowired
    private SpaceUserAppAssociationService spaceUserAppAssociationService;

    @Override
    public Set<UserOnlyIdNameAndEmailDto> findAllUserAppByEvent(Event event) {
        List<UserApp> list = repository.findAllUserAppByEvent(event);
        return userAppMapper.toUserOnlyIdNameAndEmailDtoFromEntityList(list);
    }

    @Override
    public Set<EventSpaceUserAppAssociation> createAssociation(Space space, Event event, Set<String> userEmailList) {
        Set<SpaceUserAppAssociation> spaceUserList = spaceUserAppAssociationService
                .findAllBySpaceAndEmailList(space, userEmailList);

        return spaceUserList.stream().map(spaceUserAppAssociation -> {
            EventSpaceUserAppAssociation eventSpaceUserAppAssociation = new EventSpaceUserAppAssociation(event, spaceUserAppAssociation);
            return repository.save(eventSpaceUserAppAssociation);
        }).collect(Collectors.toSet());
    }
}
