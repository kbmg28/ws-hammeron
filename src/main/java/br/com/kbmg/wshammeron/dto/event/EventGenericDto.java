package br.com.kbmg.wshammeron.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class EventGenericDto {

    private String id;

    @NotBlank
    private String name;

    @NotNull
    private OffsetDateTime utcDateTime;

}
