package org.footoo.ting.media;

import org.footoo.ting.entity.MyFavoDbItem;
import org.footoo.ting.util.AppUtil;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class PlayerEngine extends Service {

	Player player;

	private String chapter_url;
	private String source_name;
	private String chapter_name;

	public class PlayControlBinder extends Binder {
		public int getPlayerDuration() {
			return player.mediaPlayer.getDuration();
		}

		public void seekToPlayer(int progress) {
			player.mediaPlayer.seekTo(progress);
		}

		public void goOnPlaying() {
			player.play();
		}

		public void pausePlaying() {
			player.pause();
		}

		public void stopPlaying() {
			player.stop();
		}

		public boolean playerIsPlaying() {
			return player.mediaPlayer.isPlaying() ? true : false;
		}

		public void initPlayer() {
			player.initMediaPlayer();
		}

		public void playTestSource() {
			chapter_url = "http://106.186.17.91/ting/source/popmusic/test.mp3";
			chapter_name = "Lupe Fiasco-The Show Goes On";
			source_name = "The Show Goes On";
			player.playTestSource();
		}

		public MyFavoDbItem getMyFavoDbItem() {
			MyFavoDbItem tmpItem = new MyFavoDbItem();
			tmpItem.setChapter_name(chapter_name);
			tmpItem.setChapter_url(chapter_url);
			tmpItem.setLast_time(AppUtil.getCurrentTimeString());
			tmpItem.setSource_name(source_name);
			return tmpItem;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return new PlayControlBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		player = new Player(PlayerEngine.this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		chapter_name = intent.getStringExtra("audieo_name");
		chapter_url = intent.getStringExtra("audieo_url");
		source_name = intent.getStringExtra("source_name");

		player.setNowPlayingTitle(chapter_name);
		player.playUrl(chapter_url);

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (player != null) {
			player.mediaPlayer.stop();
		}
	}
}
