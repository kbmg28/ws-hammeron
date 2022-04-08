package br.com.kbmg.wshammeron.util.mapper;

import br.com.kbmg.wshammeron.dto.space.overview.EventOverviewDto;
import br.com.kbmg.wshammeron.dto.space.overview.MusicOverviewDto;
import br.com.kbmg.wshammeron.dto.space.overview.UserOverviewDto;
import br.com.kbmg.wshammeron.repository.projection.OverviewProjection;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OverviewMapper {
    String TO_USER_OVERVIEW_DTO = "toUserOverviewDto";
    String TO_MUSIC_OVERVIEW_DTO = "toMusicOverviewDto";
    String TO_EVENT_OVERVIEW_DTO = "toEventOverviewDto";

    @IterableMapping(elementTargetType = UserOverviewDto.class, qualifiedByName = TO_USER_OVERVIEW_DTO)
    List<UserOverviewDto> toUserOverviewDtoList(Collection<OverviewProjection> overviewProjections);

    @Named(TO_USER_OVERVIEW_DTO)
    @Mapping(target = "permissionName", source = "groupName")
    UserOverviewDto toUserOverviewDto(OverviewProjection overviewProjection);

    @IterableMapping(elementTargetType = MusicOverviewDto.class, qualifiedByName = TO_MUSIC_OVERVIEW_DTO)
    List<MusicOverviewDto> toMusicOverviewDtoList(List<OverviewProjection> list);

    @Named(TO_MUSIC_OVERVIEW_DTO)
    @Mapping(target = "statusName", source = "groupName")
    MusicOverviewDto toMusicOverviewDto(OverviewProjection overviewProjection);

    @IterableMapping(elementTargetType = EventOverviewDto.class, qualifiedByName = TO_EVENT_OVERVIEW_DTO)
    List<EventOverviewDto> toEventOverviewDtoList(List<OverviewProjection> list);

    @Named(TO_EVENT_OVERVIEW_DTO)
    @Mapping(target = "eventType", source = "groupName")
    EventOverviewDto toEventOverviewDto(OverviewProjection overviewProjection);

}
