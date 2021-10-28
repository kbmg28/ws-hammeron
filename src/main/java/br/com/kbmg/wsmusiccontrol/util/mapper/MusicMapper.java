package br.com.kbmg.wsmusiccontrol.util.mapper;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicLinkDto;
import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.dto.music.SingerDto;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.model.MusicLink;
import br.com.kbmg.wsmusiccontrol.model.Singer;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface MusicMapper {
    String TO_MUSIC_WITH_SINGER_AND_LINKS_DTO = "toMusicWithSingerAndLinksDto";
    String TO_SINGER = "toSinger";
    String TO_MUSIC_LINK_LIST = "toMusicLinkList";
    String TO_MUSIC_LINK = "toMusicLink";
    String TO_SINGER_DTO = "toSingerDto";

    @IterableMapping(elementTargetType = MusicWithSingerAndLinksDto.class, qualifiedByName = TO_MUSIC_WITH_SINGER_AND_LINKS_DTO)
    Set<MusicWithSingerAndLinksDto> toMusicWithSingerAndLinksDtoList(Collection<Music> musicList);

    @Named(TO_MUSIC_WITH_SINGER_AND_LINKS_DTO)
    @Mapping(target = "links", source = "musicLinkList")
    MusicWithSingerAndLinksDto toMusicWithSingerAndLinksDto(Music music);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "createdByEmail", ignore = true)
    @Mapping(target = "updatedByEmail", ignore = true)
    @Mapping(target = "eventMusicList", ignore = true)
    @Mapping(target = "singer", ignore = true)
    @Mapping(target = "musicLinkList", ignore = true)
    @Mapping(target = "space", ignore = true)
    Music toMusic(MusicWithSingerAndLinksDto musicWithSingerAndLinksDto);

    @Named(TO_MUSIC_LINK)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "createdByEmail", ignore = true)
    @Mapping(target = "updatedByEmail", ignore = true)
    @Mapping(target = "music", ignore = true)
    MusicLink toMusicLink(MusicLinkDto musicLinkDto);

    @Named(TO_SINGER_DTO)
    SingerDto toSingerDto(Singer singer);

    @IterableMapping(elementTargetType = SingerDto.class, qualifiedByName = TO_SINGER_DTO)
    Set<SingerDto> toSingerDtoList(Collection<Singer> singerList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "createdByEmail", ignore = true)
    @Mapping(target = "updatedByEmail", ignore = true)
    @Mapping(target = "eventMusicList", ignore = true)
    @Mapping(target = "singer", ignore = true)
    @Mapping(target = "musicLinkList", ignore = true)
    @Mapping(target = "space", ignore = true)
    Music updateMusic(@MappingTarget Music musicInDatabase, Music musicUpdated);
}
