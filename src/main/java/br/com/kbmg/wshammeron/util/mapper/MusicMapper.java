package br.com.kbmg.wshammeron.util.mapper;

import br.com.kbmg.wshammeron.dto.music.MusicDto;
import br.com.kbmg.wshammeron.dto.music.MusicFullWithOrderDto;
import br.com.kbmg.wshammeron.dto.music.MusicLinkDto;
import br.com.kbmg.wshammeron.dto.music.MusicTopUsedDto;
import br.com.kbmg.wshammeron.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wshammeron.dto.music.SingerDto;
import br.com.kbmg.wshammeron.model.EventMusicAssociation;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.MusicLink;
import br.com.kbmg.wshammeron.model.Singer;
import br.com.kbmg.wshammeron.repository.projection.MusicTopUsedProjection;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface MusicMapper {
    String TO_MUSIC_ONLY_ID_AND_MUSIC_NAME_AND_SINGER_NAME_DTO = "toMusicOnlyIdAndMusicNameAndSingerNameDto";
    String TO_MUSIC_WITH_SINGER_AND_LINKS_DTO = "toMusicWithSingerAndLinksDto";
    String TO_MUSIC_WITH_SINGER_AND_LINKS_DTO_IGNORE_LINKS = "toMusicWithSingerAndLinksDtoIgnoreLinks";
    String TO_MUSIC_FULL_WITH_ORDER_DTO = "toMusicFullWithOrderDto";
    String TO_MUSIC_DTO = "toMusicDto";
    String TO_SINGER = "toSinger";
    String TO_MUSIC_LINK_LIST = "toMusicLinkList";
    String TO_MUSIC_LINK = "toMusicLink";
    String TO_SINGER_DTO = "toSingerDto";

    @IterableMapping(elementTargetType = MusicWithSingerAndLinksDto.class, qualifiedByName = TO_MUSIC_WITH_SINGER_AND_LINKS_DTO)
    Set<MusicWithSingerAndLinksDto> toMusicWithSingerAndLinksDtoList(Collection<Music> musicList);

    @Named(TO_MUSIC_WITH_SINGER_AND_LINKS_DTO)
    @Mapping(target = "links", source = "musicLinkList")
    MusicWithSingerAndLinksDto toMusicWithSingerAndLinksDto(Music music);

    @IterableMapping(elementTargetType = MusicWithSingerAndLinksDto.class, qualifiedByName = TO_MUSIC_WITH_SINGER_AND_LINKS_DTO_IGNORE_LINKS)
    Set<MusicWithSingerAndLinksDto> toMusicWithSingerAndLinksDtoListIgnoreLinks(Collection<Music> musicList);

    @Named(TO_MUSIC_WITH_SINGER_AND_LINKS_DTO_IGNORE_LINKS)
    @Mapping(target = "links",ignore = true)
    MusicWithSingerAndLinksDto toMusicWithSingerAndLinksDtoIgnoreLinks(Music music);

    @IterableMapping(elementTargetType = MusicFullWithOrderDto.class, qualifiedByName = TO_MUSIC_FULL_WITH_ORDER_DTO)
    Set<MusicFullWithOrderDto> toMusicFullWithOrderDtoList(Collection<EventMusicAssociation> associationList);

    @Named(TO_MUSIC_FULL_WITH_ORDER_DTO)
    @Mapping(target = "id", source = "music.id")
    @Mapping(target = "name", source = "music.name")
    @Mapping(target = "musicStatus", source = "music.musicStatus")
    @Mapping(target = "singer", source = "music.singer")
    @Mapping(target = "links", source = "music.musicLinkList")
    MusicFullWithOrderDto toMusicFullWithOrderDto(EventMusicAssociation association);

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

    @IterableMapping(elementTargetType = MusicDto.class, qualifiedByName = TO_MUSIC_DTO)
    Set<MusicDto> toMusicDtoList(Collection<Music> musicList);

    @Named(TO_MUSIC_DTO)
    @Mapping(target = "links", source = "musicLinkList")
    @Mapping(target = "events", ignore = true)
    MusicDto toMusicDto(Music music);

    @IterableMapping(elementTargetType = MusicTopUsedDto.class)
    List<MusicTopUsedDto> toMusicTopUsedDto(List<MusicTopUsedProjection> projectionList);
}
