package br.com.kbmg.wsmusiccontrol.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private String id;
    private LocalDate date;
    private String name;

    @ApiModelProperty(example = "02:30")
    @JsonFormat(pattern = "HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalTime time;

    private Integer musicQuantity;
    private Integer userQuantity;
    private Boolean isUserLoggedIncluded;

}
