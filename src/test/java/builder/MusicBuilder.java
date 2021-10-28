package builder;

import br.com.kbmg.wsmusiccontrol.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wsmusiccontrol.dto.music.SingerDto;
import br.com.kbmg.wsmusiccontrol.enums.MusicStatusEnum;
import br.com.kbmg.wsmusiccontrol.model.Music;
import br.com.kbmg.wsmusiccontrol.model.Singer;
import br.com.kbmg.wsmusiccontrol.model.Space;

import static constants.BaseTestsConstants.ANY_VALUE;

public abstract class MusicBuilder {

    public static Music generateMusic(Space space, Singer singer) {
        Music music = new Music();
        music.setName(ANY_VALUE);
        music.setMusicStatus(MusicStatusEnum.ENABLED);
        music.setSinger(singer);
        music.setSpace(space);


        return music;
    }

    public static Singer generateSinger() {
        Singer singer = new Singer();
        singer.setName(ANY_VALUE);

        return singer;
    }

    public static MusicWithSingerAndLinksDto generateMusicWithSingerAndLinksDto() {
        MusicWithSingerAndLinksDto musicWithSingerAndLinksDto = new MusicWithSingerAndLinksDto();
        musicWithSingerAndLinksDto.setName(ANY_VALUE);
        musicWithSingerAndLinksDto.setMusicStatus(MusicStatusEnum.ENABLED);
        musicWithSingerAndLinksDto.setSinger(generateSingerDto());

        return musicWithSingerAndLinksDto;
    }

    private static SingerDto generateSingerDto() {
        SingerDto singerDto = new SingerDto();
        singerDto.setName(ANY_VALUE);

        return singerDto;
    }

}
