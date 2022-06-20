package br.com.kbmg.wshammeron.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSimpleDto {

    private String id;
    private LocalDate date;
    private String name;

}
