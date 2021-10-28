package br.com.kbmg.wsmusiccontrol.util.mapper;

import br.com.kbmg.wsmusiccontrol.dto.space.MySpace;
import br.com.kbmg.wsmusiccontrol.dto.space.SpaceDto;
import br.com.kbmg.wsmusiccontrol.model.Space;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface SpaceMapper {
    String TO_SPACE_DTO = "toSpaceDto";
    String TO_MY_SPACE_DTO = "toMySpace";

    @IterableMapping(elementTargetType = SpaceDto.class, qualifiedByName = TO_SPACE_DTO)
    List<SpaceDto> toSpaceDtoList(Collection<Space> spaceList);

    @Named(TO_SPACE_DTO)
    @Mapping(target = "spaceId", source = "id")
    SpaceDto toSpaceDto(Space space);

    @IterableMapping(elementTargetType = MySpace.class, qualifiedByName = TO_MY_SPACE_DTO)
    List<MySpace> toMySpaceDtoList(Collection<Space> spaceList);

    @Named(TO_MY_SPACE_DTO)
    @Mapping(target = "spaceId", source = "id")
    @Mapping(target = "lastAccessed", ignore = true)
    MySpace toMySpaceDto(Space space);

}
