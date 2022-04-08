package br.com.kbmg.wshammeron.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaceRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String justification;

}
