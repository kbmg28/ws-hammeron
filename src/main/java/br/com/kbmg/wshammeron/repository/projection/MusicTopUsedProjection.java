package br.com.kbmg.wshammeron.repository.projection;

public interface MusicTopUsedProjection {

    String getMusicId();
    String getMusicName();
    String getMusicStatus();
    String getSingerName();
    Integer getAmountUsedInEvents();

}
