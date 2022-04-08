package br.com.kbmg.wshammeron.dto.music;

import br.com.kbmg.wshammeron.enums.MusicStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicWithSingerAndLinksDto {

    private String id;

    @NotNull
    private String name;

    @NotNull
    private MusicStatusEnum musicStatus;

    @NotNull
    private SingerDto singer;

    private Set<MusicLinkDto> links = new HashSet<>();

}
