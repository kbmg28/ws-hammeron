package br.com.kbmg.wshammeron.event.listener;

import br.com.kbmg.wshammeron.config.logging.LogService;
import br.com.kbmg.wshammeron.dto.event.EventMainDataDto;
import br.com.kbmg.wshammeron.dto.music.MusicSimpleToEventDto;
import br.com.kbmg.wshammeron.event.OnSmsTodayRememberEvent;
import br.com.kbmg.wshammeron.event.view.SmsRememberData;
import br.com.kbmg.wshammeron.model.Event;
import br.com.kbmg.wshammeron.model.EventMusicAssociation;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.UserApp;
import br.com.kbmg.wshammeron.service.EventMailService;
import br.com.kbmg.wshammeron.service.EventMusicAssociationService;
import br.com.kbmg.wshammeron.service.EventSpaceUserAppAssociationService;
import br.com.kbmg.wshammeron.service.SmsService;
import br.com.kbmg.wshammeron.service.UserAppService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.kbmg.wshammeron.constants.AppConstants.isRunningProdProfile;

@Component
@Slf4j
public class SmsTodayRememberEventListener
        extends AbstractEmailListener
        implements ApplicationListener<OnSmsTodayRememberEvent> {

    @Value("${profile}")
    private String profile;

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private EventMailService eventMailService;

    @Autowired
    public SmsService smsService;

    @Autowired
    private LogService logService;

    @Autowired
    private EventMusicAssociationService eventMusicAssociationService;

    @Autowired
    private EventSpaceUserAppAssociationService eventSpaceUserAppAssociationService;

    @Override
    public void onApplicationEvent(OnSmsTodayRememberEvent smsTodayRememberEvent) {
        this.smsTodayRememberEventLogic(smsTodayRememberEvent);
    }

    @Transactional(readOnly = true)
    void smsTodayRememberEventLogic(OnSmsTodayRememberEvent springEvent) {
        SmsRememberData data = springEvent.getData();
        Event eventHammerOn = data.getEventHammerOn();

        logReceivedEvent(data, eventHammerOn);
        EventMainDataDto eventInfo = generateEventMainDataDto(eventHammerOn);
        Set<UserApp> userList = getUserListValidated(eventHammerOn);

        if(CollectionUtils.isEmpty(userList)) {
            String message = String.format("[SCHEDULER][%s]: Not send sms because user's list is empty of eventHammerOn %s (%s)",
                    data.getUuidScheduler(),
                    eventInfo.getName(),
                    eventInfo.getId());

            logService.logMessage(Level.INFO, message);
            return;
        }
        String description = eventMailService.sendNotificationRememberEvent(eventInfo, userList);
        smsService.sendNotificationRememberEvent(eventInfo, userList, description);
    }

    private EventMainDataDto generateEventMainDataDto(Event eventHammerOn) {
        List<EventMusicAssociation> allMusicByEvent = eventMusicAssociationService.findAllMusicByEvent(eventHammerOn);

        Set<MusicSimpleToEventDto> musicList = allMusicByEvent
                .stream()
                .map(association -> {
                    Music music = association.getMusic();
                    return new MusicSimpleToEventDto(music.getId(),
                            music.getName(),
                            music.getSinger().getName(),
                            association.getSequentialOrder());
                })
                .collect(Collectors.toSet());

        return new EventMainDataDto(eventHammerOn, musicList);
    }

    private void logReceivedEvent(SmsRememberData data, Event eventHammerOn) {
        logService.logMessage(Level.INFO, String.format("[SCHEDULER][%s]: Received -> %s (%s) ",
                data.getUuidScheduler(),
                eventHammerOn.getName(),
                eventHammerOn.getId()));
    }

    private Set<UserApp> getUserListValidated(Event eventHammerOn) {
        List<UserApp> userList = eventSpaceUserAppAssociationService.findAllUserAppByEvent(eventHammerOn);

        if(isRunningProdProfile(profile)) {
            return new HashSet<>(userList);
        }

        return userList.stream().filter(UserApp::getIsSysAdmin).collect(Collectors.toSet());
    }

}