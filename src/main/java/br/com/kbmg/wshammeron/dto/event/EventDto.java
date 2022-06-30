package br.com.kbmg.wshammeron.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class EventDto extends EventGenericDto {

    private Integer musicQuantity;
    private Integer userQuantity;
    private Boolean isUserLoggedIncluded;

    public EventDto(String id, @NotBlank String name, @NotNull OffsetDateTime utcDateTime, Integer musicQuantity, Integer userQuantity, Boolean isUserLoggedIncluded) {
        super(id, name, utcDateTime);
        this.musicQuantity = musicQuantity;
        this.userQuantity = userQuantity;
        this.isUserLoggedIncluded = isUserLoggedIncluded;
    }
}
