package org.footoo.ting.media;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.footoo.ting.R;

import android.content.Context;
import android.content.Intent;
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

		@Override
		public void handleMessage(Message msg) {
			int position = mediaPlayer.getCurrentPosition();
			int duration = mediaPlayer.getDuration();

			if (duration > 0) {
				long pos = PlayControlActions.skBarMax * position / duration;
				// TODO 在这里发广播更新seekbar的firstprogress
				intent.putExtra("first_progress", pos);
				mContext.sendBroadcast(intent);
			}
		}
	};

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

	public void playUrl(String audioUrl) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(audioUrl);
			mediaPlayer.prepareAsync();
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
