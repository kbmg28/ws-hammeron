package br.com.kbmg.wsmusiccontrol.service.impl;

import br.com.kbmg.wsmusiccontrol.constants.KeyMessageConstants;
import br.com.kbmg.wsmusiccontrol.dto.music.MusicLinkDto;
import br.com.kbmg.wsmusiccontrol.enums.MusicTypeLinkEnum;
import br.com.kbmg.wsmusiccontrol.exception.ServiceException;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.model.MusicLink;
import br.com.kbmg.wsmusiccontrol.repository.MusicLinkRepository;
import br.com.kbmg.wsmusiccontrol.service.MusicLinkService;
import br.com.kbmg.wsmusiccontrol.util.mapper.MusicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MusicLinkServiceImpl extends GenericServiceImpl<MusicLink, MusicLinkRepository> implements MusicLinkService {

    @Autowired
    private MusicMapper musicMapper;

    @Override
    public Set<MusicLink> createLinksValidated(Music music, Set<MusicLinkDto> linksDto) {

        Map<MusicTypeLinkEnum, MusicLinkDto> linksDtoMap = linksDto
                .stream()
                .collect(Collectors.toMap(MusicLinkDto::getTypeLink, Function.identity(), (old, actual) -> old));

        return Stream.of(MusicTypeLinkEnum.values()).map(typeLink -> {
            MusicLinkDto linkDto = linksDtoMap.get(typeLink);
            return (linkDto == null) ? null : createMusicLinkCheckingPattern(music, linkDto);
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public void updateMusicLink(Music musicInDatabase, Set<MusicLinkDto> linksDto) {
        Set<MusicLink> musicLinkList = musicInDatabase.getMusicLinkList();

        Map<MusicTypeLinkEnum, MusicLinkDto> linksDtoMap = linksDto
                .stream()
                .collect(Collectors.toMap(MusicLinkDto::getTypeLink, Function.identity(), (old, actual) -> old));

        Map<MusicTypeLinkEnum, MusicLink> linksEntityMap = musicLinkList
                .stream()
                .collect(Collectors.toMap(MusicLink::getTypeLink, Function.identity(), (old, actual) -> old));

        Arrays.asList(MusicTypeLinkEnum.values()).forEach(typeLink -> {
            MusicLink musicLinkEntity = linksEntityMap.get(typeLink);
            MusicLinkDto linkDto = linksDtoMap.get(typeLink);

            if (linkDto != null) {
                String newLink = linkDto.getLink();

                if (musicLinkEntity == null) {
                    MusicLink newMusicLink = createMusicLinkCheckingPattern(musicInDatabase, linkDto);
                    musicInDatabase.getMusicLinkList().add(newMusicLink);
                }
                else if (!musicLinkEntity.getLink().equals(newLink)) {
                    validateLinkByType(linkDto, newLink);
                    musicLinkEntity.setLink(newLink);
                }
            }
            else if (musicLinkEntity != null){
                musicInDatabase.getMusicLinkList().remove(musicLinkEntity);
                super.delete(musicLinkEntity);
            }
        });
    }

    private void validateLinkByType(MusicLinkDto linkDto, String newLink) {
        MusicTypeLinkEnum typeLink = linkDto.getTypeLink();
        boolean isInvalidLink = typeLink.validateUrl(newLink);

        if (isInvalidLink) {
            String template = this.messagesService.get(KeyMessageConstants.MUSIC_INVALID_LINK);
            String messageError = String.format(template, typeLink.getName());
            throw new ServiceException(messageError);
        }
    }

    private MusicLink createMusicLinkCheckingPattern(Music music, MusicLinkDto linkDto) {
        validateLinkByType(linkDto, linkDto.getLink());
        MusicLink musicLink = musicMapper.toMusicLink(linkDto);
        musicLink.setMusic(music);
        return repository.save(musicLink);
    }

}
