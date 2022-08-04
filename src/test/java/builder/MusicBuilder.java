package builder;

import br.com.kbmg.wshammeron.dto.music.MusicFullWithOrderDto;
import br.com.kbmg.wshammeron.dto.music.MusicLinkDto;
import br.com.kbmg.wshammeron.dto.music.MusicSimpleToEventDto;
import br.com.kbmg.wshammeron.dto.music.MusicWithSingerAndLinksDto;
import br.com.kbmg.wshammeron.dto.music.SingerDto;
import br.com.kbmg.wshammeron.enums.MusicStatusEnum;
import br.com.kbmg.wshammeron.enums.MusicTypeLinkEnum;
import br.com.kbmg.wshammeron.model.Music;
import br.com.kbmg.wshammeron.model.MusicLink;
import br.com.kbmg.wshammeron.model.Singer;
import br.com.kbmg.wshammeron.model.Space;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static constants.BaseTestsConstants.ANY_VALUE;
import static constants.BaseTestsConstants.LINK_CHORD_MUSIC_TEST;
import static constants.BaseTestsConstants.LINK_SPOTIFY_MUSIC_TEST;
import static constants.BaseTestsConstants.LINK_YOUTUBE_MUSIC_TEST;

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

    public static Set<MusicLink> generateMusicLinks(Music music) {

        Map<MusicTypeLinkEnum, String> linksMap = Map.of(MusicTypeLinkEnum.YOUTUBE, LINK_YOUTUBE_MUSIC_TEST,
                MusicTypeLinkEnum.SPOTIFY, LINK_SPOTIFY_MUSIC_TEST,
                MusicTypeLinkEnum.CHORD, LINK_CHORD_MUSIC_TEST);

        return linksMap.keySet().stream().map(typeLinkKey -> {
            String link = linksMap.get(typeLinkKey);
            MusicLink musicLink = new MusicLink();

            musicLink.setLink(link);
            musicLink.setTypeLink(typeLinkKey);
            musicLink.setMusic(music);

            return musicLink;
        }).collect(Collectors.toSet());
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

    private static Set<MusicLinkDto> generateMusicLinkDto(Set<MusicLink> musicLinkList) {
        return musicLinkList
                .stream()
                .map(musicLink -> new MusicLinkDto(musicLink.getId(), musicLink.getLink(), musicLink.getTypeLink()))
                .collect(Collectors.toSet());
    }

    public static MusicFullWithOrderDto generateMusicFullWithOrderDto(Music music, Integer sequentialOrder) {
        return new MusicFullWithOrderDto() {{
            setId(music.getId());
            setName(music.getName());
            setMusicStatus(music.getMusicStatus());
            setSinger(generateSingerDto());
            setLinks(generateMusicLinkDto(music.getMusicLinkList()));
            setSequentialOrder(sequentialOrder);
        }};
    }

    public static MusicSimpleToEventDto generateMusicSimpleToEventDto(Music music, Integer sequentialOrder) {
        return new MusicSimpleToEventDto() {{
            setMusicId(music.getId());
            setMusicName(music.getName());
            setSingerName(music.getSinger().getName());
            setSequentialOrder(sequentialOrder);
        }};
    }

}
