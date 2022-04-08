package br.com.kbmg.wshammeron.dto.space.overview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventOverviewDto {

    private String eventType;
    private Long total;

}
