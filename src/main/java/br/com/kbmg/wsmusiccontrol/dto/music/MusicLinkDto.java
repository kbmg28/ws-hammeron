package br.com.kbmg.wsmusiccontrol.dto.music;

import br.com.kbmg.wsmusiccontrol.enums.MusicTypeLinkEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicLinkDto {

    private String id;

    @NotBlank
    private String link;

    @NotNull
    private MusicTypeLinkEnum typeLink;

}
