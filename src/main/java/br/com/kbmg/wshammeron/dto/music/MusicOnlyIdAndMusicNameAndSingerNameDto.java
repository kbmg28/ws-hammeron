package br.com.kbmg.wshammeron.dto.music;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicOnlyIdAndMusicNameAndSingerNameDto {

    @NotNull
    private String musicId;
    private String musicName;
    private String singerName;
}
