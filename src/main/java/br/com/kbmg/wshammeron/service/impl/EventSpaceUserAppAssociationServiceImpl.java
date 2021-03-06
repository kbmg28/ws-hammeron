package br.com.kbmg.wshammeron.service.impl;

import br.com.kbmg.wshammeron.dto.event.EventMainDataDto;
import br.com.kbmg.wshammeron.dto.music.MusicSimpleToEventDto;
import br.com.kbmg.wshammeron.dto.user.UserOnlyIdNameAndEmailDto;
import br.com.kbmg.wshammeron.enums.DatabaseOperationEnum;
import br.com.kbmg.wshammeron.event.producer.UserOfEventOperationProducer;
import br.com.kbmg.wshammeron.event.view.UserOfEventOperation;
import br.com.kbmg.wshammeron.model.Event;
import br.com.kbmg.wshammeron.model.EventSpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.Space;
import br.com.kbmg.wshammeron.model.SpaceUserAppAssociation;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.repository.EventSpaceUserAppAssociationRepository;
import br.com.kbmg.wshammeron.service.EventSpaceUserAppAssociationService;
import br.com.kbmg.wshammeron.service.SpaceUserAppAssociationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventSpaceUserAppAssociationServiceImpl
        extends GenericServiceImpl<EventSpaceUserAppAssociation, EventSpaceUserAppAssociationRepository>
        implements EventSpaceUserAppAssociationService {

    @Autowired
    private SpaceUserAppAssociationService spaceUserAppAssociationService;

    @Autowired
    private UserOfEventOperationProducer userOfEventOperationProducer;

    @Autowired
    private HttpServletRequest request;

    @Override
    public List<UserApp> findAllUserAppByEvent(Event event) {
        List<UserApp> list = repository.findAllUserAppByEvent(event);
        return list;
    }

    @Override
    public Set<EventSpaceUserAppAssociation> createAssociation(Space space,
                                                               Event event,
                                                               Set<String> userEmailList,
                                                               Set<MusicSimpleToEventDto> musicList) {
        Set<SpaceUserAppAssociation> spaceUserList = spaceUserAppAssociationService
                .findAllBySpaceAndEmailList(space, userEmailList);

        Set<EventSpaceUserAppAssociation> newAssociations = createAssociationInDatabase(event, spaceUserList);

        sendNotificationToAssociations(new EventMainDataDto(event, musicList), getUserAppList(newAssociations), DatabaseOperationEnum.INSERT);

        return newAssociations;
    }

    @Override
    public Set<EventSpaceUserAppAssociation> updateAssociations(Event eventInDatabase,
                                                                Set<UserOnlyIdNameAndEmailDto> userList,
                                                                Set<MusicSimpleToEventDto> musicList) {
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

        removeAssociationsOfUsersNotRelatedOnUpdate(eventInDatabase, userListInDatabase, spaceUserInDatabaseMap);

        Set<EventSpaceUserAppAssociation> newAssociations = createAssociationInDatabase(eventInDatabase, spaceUserToCreateAssociationList);
        userListInDatabase.addAll(newAssociations);

        sendNotificationToAssociations(new EventMainDataDto(eventInDatabase, musicList), getUserAppList(newAssociations), DatabaseOperationEnum.UPDATE);

        return userListInDatabase;
    }

    @Override
    public void sendNotificationToAssociations(EventMainDataDto eventMainDataDto,
                                               Set<UserApp> userList,
                                               DatabaseOperationEnum operation) {
        if (!CollectionUtils.isEmpty(userList)) {
            userOfEventOperationProducer.publishEvent(request, new UserOfEventOperation(
                    eventMainDataDto,
                    userList,
                    operation));
        }
    }

    private Set<UserApp> getUserAppList(Collection<EventSpaceUserAppAssociation> eventUserAssociationList) {
        return eventUserAssociationList
                .stream()
                .map(eua -> eua.getSpaceUserAppAssociation().getUserApp())
                .collect(Collectors.toSet());
    }

    private void removeAssociationsOfUsersNotRelatedOnUpdate(
            Event event,
            Set<EventSpaceUserAppAssociation> userListInDatabase,
            Map<String, EventSpaceUserAppAssociation> spaceUserInDatabaseMap) {

        if (spaceUserInDatabaseMap.size() > 0) {
            Collection<EventSpaceUserAppAssociation> eventUserAssociationList = spaceUserInDatabaseMap.values();

            sendNotificationToAssociations(new EventMainDataDto(event, null), getUserAppList(eventUserAssociationList), DatabaseOperationEnum.DELETE);

            userListInDatabase.removeAll(eventUserAssociationList);
            repository.deleteAllInBatch(eventUserAssociationList);
        }
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
