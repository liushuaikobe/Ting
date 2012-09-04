package org.footoo.ting.media;

import android.media.MediaPlayer;

public class PlayerState {
	
	private static boolean isPlaying=false;
	private static String nowPlayingUrl;
	public static boolean mediaInitialized=false;
	public static MediaPlayer mMediaPlayer;
	
	public PlayerState(){
	}
	
	/**
	 * 获取播放状态
	 * @return true 正在播放 false 暂停
	 */
	public boolean getPlayingState(){
		return isPlaying;
	}
	
	/**
	 * 设置播放状态
	 * @param state: true 正在播放 false 暂停
	 */
	public void setPlayingState(boolean state){
		isPlaying = state;
	}

	/**
	 * 获取当前播放的歌曲url地址
	 * @return 当前播放的歌曲url地址
	 */
	public String getSongUrl(){
		return nowPlayingUrl;
	}
	
	/**
	 * 设置欲播放的url地址
	 * @param url:音频地址
	 */
	public void setSongUrl(String url){
		nowPlayingUrl = url;
	}
	
	
}
