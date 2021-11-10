package br.com.kbmg.wsmusiccontrol.dto.music;

import br.com.kbmg.wsmusiccontrol.dto.event.EventSimpleDto;
import br.com.kbmg.wsmusiccontrol.enums.MusicStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicDto {

    private String id;
    private String name;
    private MusicStatusEnum musicStatus;
    private SingerDto singer;
    private Set<MusicLinkDto> links = new HashSet<>();
    private List<EventSimpleDto> events = new ArrayList<>();
}
