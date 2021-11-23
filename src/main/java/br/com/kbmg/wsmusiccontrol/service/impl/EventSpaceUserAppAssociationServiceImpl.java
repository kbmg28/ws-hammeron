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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventSpaceUserAppAssociationServiceImpl extends GenericServiceImpl<EventSpaceUserAppAssociation, EventSpaceUserAppAssociationRepository> implements EventSpaceUserAppAssociationService {

    @Autowired
    private SpaceUserAppAssociationService spaceUserAppAssociationService;

    @Override
    public List<UserApp> findAllUserAppByEvent(Event event) {
        List<UserApp> list = repository.findAllUserAppByEvent(event);
        return list;
    }

    @Override
    public Set<EventSpaceUserAppAssociation> createAssociation(Space space, Event event, Set<String> userEmailList) {
        Set<SpaceUserAppAssociation> spaceUserList = spaceUserAppAssociationService
                .findAllBySpaceAndEmailList(space, userEmailList);

        return createAssociationInDatabase(event, spaceUserList);
    }

    @Override
    public Set<EventSpaceUserAppAssociation> updateAssociations(Event eventInDatabase, Set<UserOnlyIdNameAndEmailDto> userList) {
        Set<EventSpaceUserAppAssociation> userListInDatabase = eventInDatabase.getSpaceUserAppAssociationList();
        Map<String, EventSpaceUserAppAssociation> spaceUserInDatabaseMap = userListInDatabase
                .stream()
                .collect(Collectors.toMap(esu -> esu.getSpaceUserAppAssociation().getId(), Function.identity()));

        Set<SpaceUserAppAssociation> spaceUserUpdatedList = getSpaceUserUpdatedList(eventInDatabase, userList);

        Set<SpaceUserAppAssociation> spaceUserToCreateAssociationList = new HashSet<>();

        spaceUserUpdatedList.forEach(spaceUserToUpdate -> {
            EventSpaceUserAppAssociation eventSpaceUserInDatabase = spaceUserInDatabaseMap.get(spaceUserToUpdate.getId());

            if (eventSpaceUserInDatabase == null) {
                spaceUserToCreateAssociationList.add(spaceUserToUpdate);
            }

            spaceUserInDatabaseMap.remove(spaceUserToUpdate.getId());
        });

        if (spaceUserInDatabaseMap.size() > 0) {
            Collection<EventSpaceUserAppAssociation> eventUserAssociationList = spaceUserInDatabaseMap.values();
            userListInDatabase.removeAll(eventUserAssociationList);
            repository.deleteAllInBatch(eventUserAssociationList);
        }

        Set<EventSpaceUserAppAssociation> newAssociations = createAssociationInDatabase(eventInDatabase, spaceUserToCreateAssociationList);
        userListInDatabase.addAll(newAssociations);

        return userListInDatabase;
    }

    private Set<SpaceUserAppAssociation> getSpaceUserUpdatedList(Event eventInDatabase, Set<UserOnlyIdNameAndEmailDto> userList) {
        Set<String> emailList = userList
                .stream()
                .map(UserOnlyIdNameAndEmailDto::getEmail)
                .collect(Collectors.toSet());

        return spaceUserAppAssociationService
                .findAllBySpaceAndEmailList(eventInDatabase.getSpace(), emailList);
    }

    private Set<EventSpaceUserAppAssociation> createAssociationInDatabase(Event event, Set<SpaceUserAppAssociation> spaceUserList) {
        return spaceUserList.stream().map(spaceUserAppAssociation -> {
            EventSpaceUserAppAssociation eventSpaceUserAppAssociation = new EventSpaceUserAppAssociation(event, spaceUserAppAssociation);
            return repository.save(eventSpaceUserAppAssociation);
        }).collect(Collectors.toSet());
    }

}
