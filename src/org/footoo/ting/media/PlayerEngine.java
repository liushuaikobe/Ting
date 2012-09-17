package org.footoo.ting.media;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Binder;
import android.os.IBinder;

public class PlayerEngine extends Service {

	Player player;

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
			AssetManager am = getAssets();
			try {
				AssetFileDescriptor afd = am
						.openFd("Lupe Fiasco-The Show Goes On.mp3");
				player.mediaPlayer.setDataSource(afd.getFileDescriptor(),
						afd.getStartOffset(), afd.getLength());
				player.mediaPlayer.prepare();
				player.play();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		player.setNowPlayingTitle(intent.getStringExtra("audieo_name"));
		player.playUrl(intent.getStringExtra("audieo_url"));
		return START_STICKY;
	}
}
