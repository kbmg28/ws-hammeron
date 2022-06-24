package br.com.kbmg.wshammeron.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EventSimpleDto extends EventGenericDto {
    public EventSimpleDto(String id, @NotBlank String name, @NotNull OffsetDateTime utcDateTime) {
        super(id, name, utcDateTime);
    }
}
