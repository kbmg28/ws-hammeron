package br.com.kbmg.wshammeron.dto.music;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MusicFullWithOrderDto extends MusicWithSingerAndLinksDto{

    private Integer sequentialOrder;

}
