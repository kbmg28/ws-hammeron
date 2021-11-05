package br.com.kbmg.wsmusiccontrol.repository.projection;

public interface MusicTopUsedProjection {

    String getMusicId();
    String getMusicName();
    String getSingerName();
    Integer getAmountUsedInEvents();

}
