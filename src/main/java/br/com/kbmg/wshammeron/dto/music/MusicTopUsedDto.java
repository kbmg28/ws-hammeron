package br.com.kbmg.wshammeron.dto.music;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicTopUsedDto {

    private String musicId;
    private String musicName;
    private String singerName;
    private Integer amountUsedInEvents;

}
