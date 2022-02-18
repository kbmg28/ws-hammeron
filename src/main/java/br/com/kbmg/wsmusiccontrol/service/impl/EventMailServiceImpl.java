package br.com.kbmg.wsmusiccontrol.service.impl;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.parameter.ParticipationLevel;
import biweekly.property.Attendee;
import biweekly.property.Method;
import biweekly.property.Organizer;
import biweekly.util.Duration;
import br.com.kbmg.wsmusiccontrol.config.logging.LogService;
import br.com.kbmg.wsmusiccontrol.config.messages.MessagesService;
import br.com.kbmg.wsmusiccontrol.dto.event.EventMainDataDto;
import br.com.kbmg.wsmusiccontrol.dto.music.MusicOnlyIdAndMusicNameAndSingerNameDto;
import br.com.kbmg.wsmusiccontrol.model.UserApp;
import br.com.kbmg.wsmusiccontrol.service.EventMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.util.ByteArrayDataSource;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.kbmg.wsmusiccontrol.constants.AppConstants.APP_NAME;

@Service
public class EventMailServiceImpl implements EventMailService {

    @Value("${spring.mail.username}")
    private String mailNoReply;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    public MessagesService messagesService;

    @Autowired
    public LogService logService;

    @Override
    public void sendNewOrUpdateCalendarEvent(EventMainDataDto event, Set<UserApp> userList) {
        sendCalendarEvent(event, userList, Method.REQUEST, null);
    }

    @Override
    public void sendCancelCalendarEvent(EventMainDataDto event, Set<UserApp> userList) {
        sendCalendarEvent(event, userList, Method.CANCEL, null);
    }

    @Override
    public String sendNotificationRememberEvent(EventMainDataDto eventInfo, Set<UserApp> userList) {
        String description = getDescription(eventInfo, userList);
        sendCalendarEvent(eventInfo, userList, Method.REQUEST, description);
        return description;
    }

    private void sendCalendarEvent(EventMainDataDto event, Set<UserApp> userList, String type, String description) {
        Organizer organizer = new Organizer(APP_NAME, mailNoReply);
        List<Attendee> attendeeList = new ArrayList<>();
        List<InternetAddress> addressList = new ArrayList<>();

        try {
            for (UserApp userApp : userList) {
                attendeeList.add(new Attendee(userApp.getName(), userApp.getEmail()));
                addressList.add(new InternetAddress(userApp.getEmail()));
            }

            final MimeMessage mimeMessage = createEvent(event, description, organizer, attendeeList, null, type);

            final InternetAddress fromAddress = new InternetAddress(mailNoReply, APP_NAME);

            mimeMessage.setRecipients(Message.RecipientType.TO, addressList.toArray(Address[]::new));
            mimeMessage.setSender(fromAddress);
            mimeMessage.setSubject(event.getNameEvent());

            mailSender.send(mimeMessage);
        } catch (Exception exception) {
            exception.printStackTrace();
            logService.logUnexpectedException(exception);
        }
    }

    private String getDescription(EventMainDataDto event, Set<UserApp> userList) {
        Set<MusicOnlyIdAndMusicNameAndSingerNameDto> musicList = event.getMusicList();
        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("dd/MM");
        String formattedDate = event.getDateEvent().format(formatterDate);

        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("hh:mm");
        String formattedTime = event.getTimeEvent().format(formatterTime);
        String delimiter = "\n";

        String formattedUsers = userList
                .stream()
                .map(user -> " - " + user.getName())
                .collect(Collectors.joining(delimiter));

        String formattedMusics;

        if(CollectionUtils.isEmpty(musicList)) {
            formattedMusics = messagesService.get("event.section.songs.noData");
        } else {
            formattedMusics = musicList
                    .stream()
                    .map(user -> String.format(" - %s (%s)", user.getMusicName(), user.getSingerName()))
                    .collect(Collectors.joining(delimiter));
        }

        String template = "%s (%s - %s)\n\n%s\n%s\n\n%s\n%s";

        return String.format(template,
                event.getNameEvent(),
                formattedDate,
                formattedTime,
                messagesService.get("event.section.peoples"),
                formattedUsers,
                messagesService.get("event.section.songs"),
                formattedMusics);
    }

    private MimeMessage createEvent(EventMainDataDto eventHammerOn,
                                    String description,
                                    Organizer organizer,
                                    List<Attendee> attendeeList,
                                    MimeBodyPart attachment,
                                    String type) throws Exception {

        final MimeMessage message = mailSender.createMimeMessage();
        final MimeMultipart mixedMultipart = new MimeMultipart("mixed");

        MimeBodyPart mimeBodyPart = createMainContentEvent(eventHammerOn, description,
                new MimeBodyPart(), organizer, attendeeList, type);

        mixedMultipart.addBodyPart(mimeBodyPart);

        if(attachment != null) {
            mixedMultipart.addBodyPart(attachment);
        }

        message.setContent(mixedMultipart);

        return message;
    }

    public <T extends MimePart> T createMainContentEvent(EventMainDataDto eventHammerOn,
                                                         String description,
                                                         T mimePartForCalendar,
                                                         Organizer organizer,
                                                         List<Attendee> attendeeList,
                                                         String type) throws Exception {
        String eventInfo = generateEventInfo(eventHammerOn,
                description,
                organizer,
                attendeeList,
                type);

        final DataSource source = new ByteArrayDataSource(eventInfo, "text/calendar; charset=UTF-8");
        mimePartForCalendar.setDataHandler(new DataHandler(source));
        mimePartForCalendar.setHeader("Content-Type", "text/calendar; charset=UTF-8; method=REQUEST");
        return mimePartForCalendar;
    }

    public MimeBodyPart createAttachment(String fileName, String extension, String typeContent, Object data) throws Exception {
        final MimeBodyPart textAttachmentPart =  new MimeBodyPart();
        textAttachmentPart.setContent(data, String.format("%s; charset=UTF-8", typeContent));
        textAttachmentPart.addHeader("Content-Disposition", String.format("attachment; filename=%s.%s", fileName, extension));
        return textAttachmentPart;
    }

    private String generateEventInfo(EventMainDataDto eventHammerOn,
                                     String description,
                                     Organizer organizer,
                                     List<Attendee> attendeeList,
                                     String type) {

        ICalendar calendarEvent = new ICalendar();
        calendarEvent.setMethod(type);

        VEvent event = new VEvent();
        event.setSummary(eventHammerOn.getNameEvent());
        event.setDescription(description);
        event.setUid(eventHammerOn.getId());

        LocalDateTime localDateTime = LocalDateTime.of(eventHammerOn.getDateEvent(), eventHammerOn.getTimeEvent());

        OffsetDateTime off = OffsetDateTime.of(eventHammerOn.getDateEvent(), eventHammerOn.getTimeEvent(), ZoneOffset.UTC);
        ZonedDateTime zoned = off.atZoneSameInstant(ZoneId.of(eventHammerOn.getTimeZoneName()));
        Date date = java.util.Date.from(zoned.toInstant());

        event.setDateStart(date);
        event.setDuration(new Duration.Builder().hours(1).build());

        event.setOrganizer(organizer);
        attendeeList.forEach(event::addAttendee);

        calendarEvent.addEvent(event);

        return Biweekly.write(calendarEvent).go();
    }

    private Attendee generateAttendee(String name, String email) {
        Attendee a = new Attendee(name, email);
        a.setParticipationLevel(ParticipationLevel.REQUIRED);
        return a;
    }
}
