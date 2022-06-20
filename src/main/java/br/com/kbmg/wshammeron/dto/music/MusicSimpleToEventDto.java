package br.com.kbmg.wshammeron.dto.music;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MusicSimpleToEventDto extends MusicOnlyIdAndMusicNameAndSingerNameDto {

    @NotNull
    private Integer sequentialOrder;

    public MusicSimpleToEventDto(String musicId, String musicName, String singerName, Integer sequentialOrder) {
        super(musicId, musicName, singerName);
        this.sequentialOrder = sequentialOrder;
    }
}
