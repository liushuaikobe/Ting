package org.footoo.ting;

import java.util.ArrayList;

import org.footoo.ting.adapter.ChapterListAdapter;
import org.footoo.ting.adapter.LocalSourceGridAdapter;
import org.footoo.ting.adapter.MenuGridAdapter;
import org.footoo.ting.entity.Chapter;
import org.footoo.ting.entity.PlayHistoryDbItem;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class LocalSourceActivity extends MainBaseActivity {
	private ViewSwitcher localSourceSwitcher;
	private GridView localSourceGrid;

	private LocalSourceGridAdapter localSourceGridAdapter;
	private ChapterListAdapter chapterListAdapter;

	private ArrayList<Chapter> chapters;

	private PlayerEngine.PlayControlBinder binder;

	private ImageView broadcastController; // 底部控制板的一些View
	private SeekBar skbProgress;
	private TextView nowPlayingTitle;
	private ImageView addMyFAvoBtm;

	private DBManager manager;

	private ShowToastReceiver showToastReceiver;
	private UpdateSkBarFirstProgress updateSkBarFirstProgress;
	private UpdateSkBarSecondProgress updateSkBarSecondProgress;
	private UpdateNowplayingTitle updateNowplayingTitle;

	private int currentPageFlag;

	private static final int SOURCE_PAGE = 1;
	private static final int CHAPTER_PAGE = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();
		initViews();
		registerReceivers();
		BindPlayService();

		new Handler().postDelayed(new Runnable() {
			public void run() {
				localSourceSwitcher.setDisplayedChild(0);
			}
		}, 1000);
	}

	private void initData() {
		localSourceGridAdapter = new LocalSourceGridAdapter(
				LocalSourceActivity.this);
		chapterListAdapter = new ChapterListAdapter(LocalSourceActivity.this);

		chapters = new ArrayList<Chapter>();

		Chapter chapter = new Chapter(0, "Lupe Fiasco-The Show Goes On",
				"http://106.186.17.91/ting/source/popmusic/test.mp3");
		chapters.add(chapter);

		showToastReceiver = new ShowToastReceiver();
		updateSkBarFirstProgress = new UpdateSkBarFirstProgress();
		updateSkBarSecondProgress = new UpdateSkBarSecondProgress();
		updateNowplayingTitle = new UpdateNowplayingTitle();

		manager = new DBManager(LocalSourceActivity.this);
	}

	private void initViews() {
		underView.setAdapter(new MenuGridAdapter(LocalSourceActivity.this,
				MenuGridAdapter.LOCAL_PAGE_ID));

		currentPageFlag = SOURCE_PAGE;
		setContentPage(R.layout.layout_local_page, R.string.local_page_title,
				new LocalSourcePageSlideBtnListener(),
				new ConfigContentPageInterface() {
					public void configPage(View page) {
						localSourceSwitcher = (ViewSwitcher) page
								.findViewById(R.id.viewswicher_local_source);
						localSourceSwitcher.showNext();

						localSourceGrid = (GridView) page
								.findViewById(R.id.local_source_grid);
						localSourceGrid.setAdapter(localSourceGridAdapter);
						localSourceGrid
								.setOnItemClickListener(new LocalSourceGridItemClickListener());
					}
				});
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

	/**
	 * 在本地资源页面，slideButton的点击事件是侧滑
	 */
	private class LocalSourcePageSlideBtnListener implements
			View.OnClickListener {
		public void onClick(View v) {
			scrollView.clickSlideButton();
		}
	}

	/**
	 * 在显示具体本地资源的章节里面，slideButton的点击事件是返回本地收藏的GridView
	 */
	private class ChapterPageSlideBtnListener implements View.OnClickListener {
		public void onClick(View v) {
			currentPageFlag = SOURCE_PAGE;
			initViews();
			localSourceSwitcher.setDisplayedChild(0);
		}
	}

	/**
	 * 本地资源GridView点击事件
	 */
	private class LocalSourceGridItemClickListener implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			currentPageFlag = CHAPTER_PAGE;
			slideBtn.setBackgroundResource(R.drawable.go_back_selector);
			// slideBtn.setText(R.string.go_back);
			slideBtn.setOnClickListener(new ChapterPageSlideBtnListener());
			ListView chapterList = new ListView(LocalSourceActivity.this);
			chapterListAdapter.setChapterList(chapters);
			chapterListAdapter.setManager(manager);
			chapterList.setAdapter(chapterListAdapter);
			chapterList.setOnItemClickListener(new ChapterListClickListener());
			LinearLayout tmpLinearLayout = (LinearLayout) contentPage
					.findViewById(R.id.my_ll);
			tmpLinearLayout.removeAllViews();
			tmpLinearLayout.addView(chapterList);
		}
	}

	private class ChapterListClickListener implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> pqrent, View view, int position,
				long id) {
			Log.i("chapters_url", chapters.get(position).getChapterUrl());

			PlayHistoryDbItem history = new PlayHistoryDbItem();
			history.setChapter_name(chapters.get(position).getChapterName());
			history.setChapter_url(chapters.get(position).getChapterUrl());
			history.setLast_time(AppUtil.getCurrentTimeString());
			history.setSource_name("The Show Goes On");
			manager.insertOneHistory(history);
			binder.playTestSource();
			if (manager.hasBeenMyFavo(chapters.get(position).getChapterUrl())) {
				PlayControlActions.currentIsMyFavo = true;
				addMyFAvoBtm.setImageResource(R.drawable.favorate_ok);
			} else {
				PlayControlActions.currentIsMyFavo = false;
				addMyFAvoBtm.setImageResource(R.drawable.favorate);
			}
			broadcastController.setImageResource(R.drawable.pause_btn_selector);
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
				ToastUtil.makeShortToast(LocalSourceActivity.this,
						getResources().getString(R.string.have_added_myfavo));
				PlayControlActions.currentIsMyFavo = true;
				if (chapterListAdapter != null) {
					chapterListAdapter.notifyDataSetChanged();
				}
				addMyFAvoBtm.setImageResource(R.drawable.favorate_ok);
			} else {
				ToastUtil.makeShortToast(LocalSourceActivity.this,
						getResources().getString(R.string.has_been_myfavo));
			}
		}
	}

	private void setContentPage(int layoutId, int topBarTitleStringResId,
			View.OnClickListener listener,
			ConfigContentPageInterface configInterface) {
		if (scrollView == null) {
			scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView);
		}
		contentPage = LayoutInflater.from(this).inflate(layoutId, null);

		topBarTitleTv = (TextView) contentPage.findViewById(R.id.topbar_title);
		setTopBarTitle(topBarTitleStringResId);

		slideBtn = (Button) contentPage.findViewById(R.id.slideBtn);
		slideBtn.setOnClickListener(listener);

		topBarTitleTv = (TextView) contentPage.findViewById(R.id.topbar_title);
		setTopBarTitle(topBarTitleStringResId);

		slideBtn = (Button) contentPage.findViewById(R.id.slideBtn);
		slideBtn.setOnClickListener(listener);

		skbProgress = (SeekBar) ((View) contentPage // 初始化底部控制面板的View
				.findViewById(R.id.tmp_btm_ctrl)).findViewById(R.id.bottom_skb);
		skbProgress.setOnTouchListener(new SeekBarTouchListener());
		skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());
		PlayControlActions.skBarMax = skbProgress.getMax();

		broadcastController = (ImageView) ((View) contentPage
				.findViewById(R.id.tmp_btm_ctrl))
				.findViewById(R.id.bottom_control_btn);
		broadcastController.setOnClickListener(new BottomCtrlClickListener());
		broadcastController
				.setOnLongClickListener(new BottomCtrlLongClickListener());
		nowPlayingTitle = (TextView) ((View) contentPage
				.findViewById(R.id.tmp_btm_ctrl))
				.findViewById(R.id.bottom_title);
		addMyFAvoBtm = (ImageView) ((View) contentPage
				.findViewById(R.id.tmp_btm_ctrl))
				.findViewById(R.id.bottom_favorate);
		addMyFAvoBtm.setOnClickListener(new AddFavoClickListener());
		if (PlayControlActions.currentIsMyFavo == true) {
			addMyFAvoBtm.setImageResource(R.drawable.favorate_ok);
		} else {
			addMyFAvoBtm.setImageResource(R.drawable.favorate);
		}

		configInterface.configPage(contentPage);

		View leftView = new View(this);
		leftView.setBackgroundColor(Color.TRANSPARENT);
		((ViewGroup) scrollView.getChildAt(0)).removeAllViews();
		final View[] children = new View[] { leftView, contentPage };
		scrollView.initViews(children, new SizeCallBackForMenu(slideBtn),
				(View) findViewById(R.id.menu_layout));
		scrollView.setSlideButton(slideBtn);
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
			ToastUtil.makeShortToast(LocalSourceActivity.this, content);
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
	 * 底部控制面板的按钮的长按事件
	 */
	private class BottomCtrlLongClickListener implements
			View.OnLongClickListener {
		public boolean onLongClick(View v) {
			ToastUtil.makeShortToast(LocalSourceActivity.this, "停止播放");
			broadcastController.setImageResource(R.drawable.play_btn_selector);
			binder.stopPlaying();
			return true;
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
		Intent intent = new Intent(LocalSourceActivity.this, PlayerEngine.class);
		bindService(intent, conn, Service.BIND_AUTO_CREATE);
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (MainHorizontalScrollView.underViewIsOut == true) {
				scrollView.clickSlideButton();
			} else {
				switch (currentPageFlag) {
				case SOURCE_PAGE:
					AppUtil.QuitHintDialog(LocalSourceActivity.this);
					break;
				case CHAPTER_PAGE:
					currentPageFlag = SOURCE_PAGE;
					initViews();
					localSourceSwitcher.setDisplayedChild(0);
					break;
				default:
					break;
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private interface ConfigContentPageInterface {
		void configPage(View page);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceivers();
		unbindService(conn);
		manager.closeDB();
	}

	private void unregisterReceivers() {
		unregisterReceiver(showToastReceiver);
		unregisterReceiver(updateNowplayingTitle);
		unregisterReceiver(updateSkBarFirstProgress);
		unregisterReceiver(updateSkBarSecondProgress);
	}
}
