package org.footoo.ting.media;

import java.io.IOException;

import org.footoo.ting.HotPageActivity;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;

import android.util.Log;
import android.widget.Toast;

public class PlayerEngine extends Service {

	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private PlayerState mPlayerState;

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// Normally we would do some work here, like download a file.
			// For our sample, we just sleep for 5 seconds.
			// long endTime = System.currentTimeMillis() + 5 * 1000;
			// while (System.currentTimeMillis() < endTime) {
			// synchronized (this) {
			// try {
			// wait(endTime - System.currentTimeMillis());
			// } catch (Exception e) {
			// }
			// }
			// }
			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			// stopSelf(msg.arg1);
			Log.v("cain", "current thread:" + Thread.currentThread().getId());

			switch (msg.arg1) {
			case 1:
				mPlayerState = new PlayerState();
				PlayerState.mMediaPlayer = new MediaPlayer();
				Log.v("cain", "initialize success");
				break;
			case 2:
				if (!PlayerState.mediaInitialized) {
					PlayerState.mMediaPlayer
							.setAudioStreamType(AudioManager.STREAM_MUSIC);
					mPlayerState.setSongUrl(msg.getData().getString("sourceUrl"));

					try {
						PlayerState.mMediaPlayer.setDataSource(mPlayerState
								.getSongUrl());
					} catch (IllegalArgumentException e) {

						e.printStackTrace();
					} catch (SecurityException e) {

						e.printStackTrace();
					} catch (IllegalStateException e) {

						e.printStackTrace();
					} catch (IOException e) {

						e.printStackTrace();
					}

					try {
						PlayerState.mMediaPlayer.prepare();
					} catch (IllegalStateException e) {

						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					PlayerState.mediaInitialized = true;
				}
				if (mPlayerState.getPlayingState()) {
					PlayerState.mMediaPlayer.pause();
					mPlayerState.setPlayingState(false);
				} else {
					PlayerState.mMediaPlayer.start();
					mPlayerState.setPlayingState(true);
				}
				break;
			}
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.v("cain", "PlayerEngine Service Create");
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);

		Message msg = mServiceHandler.obtainMessage();
		// 1 stand for initialize;
		msg.arg1 = 1;
		mServiceHandler.sendMessage(msg);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("cain", "PlayerEngine Service Start");
		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the
		// job
		
		Bundle bundle = new Bundle();
		bundle.putString("sourceUrl", intent.getStringExtra(HotPageActivity.URL));
		
		Message msg = mServiceHandler.obtainMessage();
		
		msg.setData(bundle);
		// 2 stand for control
		msg.arg1 = 2;
		msg.arg2 = startId;
		mServiceHandler.sendMessage(msg);
		
		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.v("cain", "PlayerEngine Service Destroy");
		Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
	}

}
