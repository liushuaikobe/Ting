package org.footoo.ting;

import org.footoo.ting.adapter.MenuGridAdapter;
import org.footoo.ting.adapter.MoreListAdapter;
import org.footoo.ting.media.PlayControlActions;
import org.footoo.ting.media.PlayerEngine;
import org.footoo.ting.storage.DBManager;
import org.footoo.ting.ui.MainHorizontalScrollView;
import org.footoo.ting.util.AppUtil;
import org.footoo.ting.util.SizeCallBackForMenu;
import org.footoo.ting.util.ToastUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MoreActivity extends MainBaseActivity {

	private ListView menuLv;

	private ShowToastReceiver showToastReceiver;
	private UpdateSkBarFirstProgress updateSkBarFirstProgress;
	private UpdateSkBarSecondProgress updateSkBarSecondProgress;
	private UpdateNowplayingTitle updateNowplayingTitle;

	private ImageView broadcastController; // 底部控制板的一些View
	private SeekBar skbProgress;
	private TextView nowPlayingTitle;
	private ImageView addMyFavoBtm;

	private DBManager manager;

	private PlayerEngine.PlayControlBinder binder;

	private MoreListAdapter menuLvAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();
		initViews();
		registerReceivers();
		BindPlayService();
	}

	private void initData() {
		menuLvAdapter = new MoreListAdapter(MoreActivity.this);
		// menuLvAdapter.setCacheSize("当前缓存4.07MB");

		manager = new DBManager(MoreActivity.this);

		showToastReceiver = new ShowToastReceiver();
		updateSkBarFirstProgress = new UpdateSkBarFirstProgress();
		updateSkBarSecondProgress = new UpdateSkBarSecondProgress();
		updateNowplayingTitle = new UpdateNowplayingTitle();
	}

	private void initViews() {
		scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView);

		contentPage = LayoutInflater.from(this).inflate(
				R.layout.layout_more_page, null);

		// 初始化底部的控制面板
		broadcastController = (ImageView) ((View) contentPage
				.findViewById(R.id.tmp_btm_ctrl))
				.findViewById(R.id.bottom_control_btn);
		broadcastController.setOnClickListener(new BottomCtrlClickListener());
		broadcastController
				.setOnLongClickListener(new BottomCtrlLongClickListener());
		skbProgress = (SeekBar) ((View) contentPage
				.findViewById(R.id.tmp_btm_ctrl)).findViewById(R.id.bottom_skb);
		skbProgress.setOnTouchListener(new SeekBarTouchListener());
		skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		PlayControlActions.skBarMax = skbProgress.getMax();
		nowPlayingTitle = (TextView) ((View) contentPage
				.findViewById(R.id.tmp_btm_ctrl))
				.findViewById(R.id.bottom_title);
		addMyFavoBtm = (ImageView) ((View) contentPage
				.findViewById(R.id.tmp_btm_ctrl))
				.findViewById(R.id.bottom_favorate);
		addMyFavoBtm.setOnClickListener(new AddFavoClickListener());
		if (PlayControlActions.currentIsMyFavo == true) {
			addMyFavoBtm.setImageResource(R.drawable.favorate_ok);
		} else {
			addMyFavoBtm.setImageResource(R.drawable.favorate);
		}

		menuLv = (ListView) contentPage.findViewById(R.id.more_item_list);
		menuLv.setCacheColorHint(Color.argb(0, 0, 0, 0));
		menuLv.setSelector(new ColorDrawable(Color.TRANSPARENT));
		menuLv.setAdapter(menuLvAdapter);
		menuLv.setOnItemClickListener(new ItemListClickListener());

		underView.setAdapter(new MenuGridAdapter(MoreActivity.this,
				MenuGridAdapter.MORE_PAGE_ID));

		topBarTitleTv = (TextView) contentPage.findViewById(R.id.topbar_title);
		setTopBarTitle(R.string.more_page_title);

		slideBtn = (Button) contentPage.findViewById(R.id.slideBtn);
		slideBtn.setOnClickListener(new MorepageSlideBtnClickListener());

		View leftView = new View(this);
		leftView.setBackgroundColor(Color.TRANSPARENT);
		final View[] children = new View[] { leftView, contentPage };
		scrollView.initViews(children, new SizeCallBackForMenu(slideBtn),
				(View) findViewById(R.id.menu_layout));
		scrollView.setSlideButton(slideBtn);
	}

	private void registerReceivers() {
		IntentFilter filter;

		filter = new IntentFilter(PlayControlActions.ACTION_SHOW_TOAST);
		registerReceiver(showToastReceiver, filter);

		filter = new IntentFilter(
				PlayControlActions.ACTION_UPDATE_FIRST_PROGRESS);
		registerReceiver(updateSkBarFirstProgress, filter);

		filter = new IntentFilter(
				PlayControlActions.ACTION_UPDATE_SECOND_PROGRESS);
		registerReceiver(updateSkBarSecondProgress, filter);

		filter = new IntentFilter(
				PlayControlActions.ACTION_UPDATE_NOWPLAYING_TITLE);
		registerReceiver(updateNowplayingTitle, filter);
	}

	private class ItemListClickListener implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
				AppUtil.showAboutDialog(MoreActivity.this);
				break;
			case 1:
				break;
			case 2:
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 添加到我的收藏
	 */
	private class AddFavoClickListener implements View.OnClickListener {
		public void onClick(View v) {
			if (PlayControlActions.playerIsStoping == true) {
				return;
			}
			if (manager.insertOneMyFavo(binder.getMyFavoDbItem()) == true) {
				ToastUtil.makeShortToast(MoreActivity.this, getResources()
						.getString(R.string.have_added_myfavo));
				PlayControlActions.currentIsMyFavo = true;
				addMyFavoBtm.setImageResource(R.drawable.favorate_ok);
			} else {
				ToastUtil.makeShortToast(MoreActivity.this, getResources()
						.getString(R.string.has_been_myfavo));
			}
		}
	}

	/**
	 * 更新SeekBar的FirstProgress
	 */
	private class UpdateSkBarFirstProgress extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			skbProgress.setProgress((int) intent.getLongExtra("first_progress",
					0L));
		}
	}

	/**
	 * 更新SeekBar的SecondProgress
	 */
	private class UpdateSkBarSecondProgress extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			skbProgress.setSecondaryProgress(intent.getIntExtra(
					"second_progress", 0));
		}
	}

	/**
	 * 弹出Toast
	 */
	private class ShowToastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String content = intent.getStringExtra("toast_content");
			ToastUtil.makeShortToast(MoreActivity.this, content);
		}
	}

	/**
	 * 设置底部播放器的题目
	 */
	private class UpdateNowplayingTitle extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String title = intent.getStringExtra("nowplaying_title");
			nowPlayingTitle.setText(title);
		}
	}

	/**
	 * seekbar的滑动事件
	 */
	class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
		int progress;

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			this.progress = progress * binder.getPlayerDuration()
					/ seekBar.getMax();
		}

		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			binder.seekToPlayer(progress);
		}
	}

	/**
	 * seekBar的Touch事件，维护一个变量防止定时器与SeekBar拖动时进度冲突
	 */
	private class SeekBarTouchListener implements View.OnTouchListener {
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				PlayControlActions.skBarIsPressed = true;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				PlayControlActions.skBarIsPressed = false;
			}
			return false;
		}
	}

	/**
	 * 底部控制面板的按钮的长按事件
	 */
	private class BottomCtrlLongClickListener implements
			View.OnLongClickListener {
		public boolean onLongClick(View v) {
			ToastUtil.makeShortToast(MoreActivity.this, "停止播放");
			broadcastController.setImageResource(R.drawable.play_btn_selector);
			binder.stopPlaying();
			return true;
		}
	}

	/**
	 * 底部控制面板的按钮的点击事件
	 */
	private class BottomCtrlClickListener implements View.OnClickListener {
		public void onClick(View v) {
			if (PlayControlActions.playerIsStoping) {
				binder.initPlayer();
				binder.playTestSource();
				broadcastController
						.setImageResource(R.drawable.pause_btn_selector);
				return;
			}
			if (binder.playerIsPlaying()) {
				broadcastController
						.setImageResource(R.drawable.play_btn_selector);
				binder.pausePlaying();
			} else {
				broadcastController
						.setImageResource(R.drawable.pause_btn_selector);
				binder.goOnPlaying();
			}
		}
	}

	/**
	 * 初始化一个ServiceConnection对象用于和Service的绑定
	 */
	private ServiceConnection conn = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			Log.i("HotPageActivity", "--Disconnected--");
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			binder = (PlayerEngine.PlayControlBinder) service;
		}
	};

	/**
	 * 绑定到播放的Service
	 */
	private void BindPlayService() {
		Intent intent = new Intent(MoreActivity.this, PlayerEngine.class);
		bindService(intent, conn, Service.BIND_AUTO_CREATE);
	}

	private class MorepageSlideBtnClickListener implements View.OnClickListener {
		public void onClick(View v) {
			scrollView.clickSlideButton();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceivers();
		unbindService(conn);
	}

	private void unregisterReceivers() {
		unregisterReceiver(showToastReceiver);
		unregisterReceiver(updateNowplayingTitle);
		unregisterReceiver(updateSkBarFirstProgress);
		unregisterReceiver(updateSkBarSecondProgress);
	}
}
