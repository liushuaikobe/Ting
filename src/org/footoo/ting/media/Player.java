package org.footoo.ting.media;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.footoo.ting.R;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Player implements OnBufferingUpdateListener, OnCompletionListener,
		OnPreparedListener {

	public MediaPlayer mediaPlayer;

	private Context mContext;

	private Timer mTimer;

	private String nowPlayingTitle;

	public Player(Context context) {
		try {
			mTimer = new Timer();
			this.mContext = context;

			initMediaPlayer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mTimer.schedule(mTimerTask, 0, 1000);
	}

	TimerTask mTimerTask = new TimerTask() {
		@Override
		public void run() {
			if (mediaPlayer == null) {
				return;
			}
			if (mediaPlayer.isPlaying() && !PlayControlActions.skBarIsPressed) {
				handler.sendEmptyMessage(0);
			}

		}
	};

	Handler handler = new Handler() {
		Intent intent = new Intent(
				PlayControlActions.ACTION_UPDATE_FIRST_PROGRESS);
		Intent intent_1 = new Intent(
				PlayControlActions.ACTION_UPDATE_NOWPLAYING_TITLE);

		@Override
		public void handleMessage(Message msg) {
			int position = mediaPlayer.getCurrentPosition();
			int duration = mediaPlayer.getDuration();

			if (duration > 0) {
				long pos = PlayControlActions.skBarMax * position / duration;
				// TODO 在这里发广播更新Seekbar的Firstprogress
				intent.putExtra("first_progress", pos);
				mContext.sendBroadcast(intent);
				// TODO 在这里发广播更新底部播放器的标题
				intent_1.putExtra("nowplaying_title", getNowPlayingTitle());
				mContext.sendBroadcast(intent_1);
			}
		}
	};

	public String getNowPlayingTitle() {
		return nowPlayingTitle;
	}

	public void setNowPlayingTitle(String nowPlayingTitle) {
		this.nowPlayingTitle = nowPlayingTitle;
	}

	public void initMediaPlayer() {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnCompletionListener(this);
	}

	public void play() {
		PlayControlActions.playerIsStoping = false;
		mediaPlayer.start();
	}

	public void playTestSource() {
		AssetManager am = mContext.getAssets();
		try {
			AssetFileDescriptor afd = am
					.openFd("Lupe Fiasco-The Show Goes On.mp3");
			mediaPlayer.reset();
			mediaPlayer.setDataSource(afd.getFileDescriptor(),
					afd.getStartOffset(), afd.getLength());
			mediaPlayer.prepare();
			setNowPlayingTitle("Lupe Fiasco-The Show Goes On");
			play();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playUrl(String audioUrl) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(audioUrl);
			mediaPlayer.prepareAsync();
			PlayControlActions.playerIsStoping = false;
			// TODO 在此处显示Toast告知用户开始缓冲
			Intent intent = new Intent(PlayControlActions.ACTION_SHOW_TOAST);
			intent.putExtra("toast_content",
					mContext.getResources().getString(R.string.begin_buffering));
			mContext.sendBroadcast(intent);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void pause() {
		PlayControlActions.playerIsStoping = false;
		mediaPlayer.pause();
	}

	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			PlayControlActions.playerIsStoping = true;
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	public void onPrepared(MediaPlayer mp) {
		mp.start();
		Log.i("mediaPlayer", "onPrepared");
	}

	public void onCompletion(MediaPlayer mp) {
		Log.i("mediaPlayer", "onCompletion");
	}

	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// 以下代码是为了输出Log调试用
		int tmp = mediaPlayer.getDuration();
		if (tmp != 0) {
			int currentProgress = PlayControlActions.skBarMax
					* mediaPlayer.getCurrentPosition() / tmp;
			Log.e(currentProgress + "% play", percent + "% buffer");
		}
		// TODO 在这里发广播更新secondprogress
		Intent intent = new Intent(
				PlayControlActions.ACTION_UPDATE_SECOND_PROGRESS);
		intent.putExtra("second_progress", percent);
		mContext.sendBroadcast(intent);
	}

}
