package br.com.kbmg.wsmusiccontrol.dto.space.overview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaceOverviewDto {

    private String spaceId;
    private String spaceName;
    private String createdBy;
    private List<UserOverviewDto> userList;
    private List<MusicOverviewDto> musicList;
    private List<EventOverviewDto> eventList;

}
