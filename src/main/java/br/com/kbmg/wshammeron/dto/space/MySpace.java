package br.com.kbmg.wshammeron.dto.space;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MySpace {

    private String spaceId;
    private String name;
    private Boolean lastAccessed;
}
