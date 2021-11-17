package br.com.kbmg.wsmusiccontrol.dto.music;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicOnlyIdAndMusicNameAndSingerNameDto {

    private String musicId;
    private String musicName;
    private String singerName;
}
