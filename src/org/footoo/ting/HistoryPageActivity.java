package org.footoo.ting;

import java.util.ArrayList;

import org.footoo.ting.adapter.HistoryChapterListAdapter;
import org.footoo.ting.adapter.MenuGridAdapter;
import org.footoo.ting.entity.PlayHistoryDbItem;
import org.footoo.ting.media.PlayControlActions;
import org.footoo.ting.media.PlayerEngine;
import org.footoo.ting.storage.DBManager;
import org.footoo.ting.ui.MainHorizontalScrollView;
import org.footoo.ting.util.SizeCallBackForMenu;
import org.footoo.ting.util.ToastUtil;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class HistoryPageActivity extends MainBaseActivity {

	private ViewSwitcher historySwitcher;
	private ListView historyListView;

	private HistoryChapterListAdapter historyChapterListAdapter;

	private ShowToastReceiver showToastReceiver;
	private UpdateSkBarFirstProgress updateSkBarFirstProgress;
	private UpdateSkBarSecondProgress updateSkBarSecondProgress;
	private UpdateNowplayingTitle updateNowplayingTitle;

	private ImageView broadcastController; // 底部控制板的一些View
	private SeekBar skbProgress;
	private TextView nowPlayingTitle;
	private ImageView addMyFavoBtm;

	private PlayerEngine.PlayControlBinder binder;

	private DBManager manager;
	private ArrayList<PlayHistoryDbItem> historyList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();
		initViews();
		registerReceivers();
		BindPlayService();

		registerForContextMenu(historyListView);

		new GetPlayHistoryTask().execute("begin");
	}

	private void initData() {
		manager = new DBManager(HistoryPageActivity.this);
		historyChapterListAdapter = new HistoryChapterListAdapter(
				HistoryPageActivity.this);

		showToastReceiver = new ShowToastReceiver();
		updateSkBarFirstProgress = new UpdateSkBarFirstProgress();
		updateSkBarSecondProgress = new UpdateSkBarSecondProgress();
		updateNowplayingTitle = new UpdateNowplayingTitle();
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
				ToastUtil.makeShortToast(HistoryPageActivity.this,
						getResources().getString(R.string.have_added_myfavo));
				PlayControlActions.currentIsMyFavo = true;
				addMyFavoBtm.setImageResource(R.drawable.favorate_ok);
			} else {
				ToastUtil.makeShortToast(HistoryPageActivity.this,
						getResources().getString(R.string.has_been_myfavo));
			}
		}
	}

	private void initViews() {
		scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView);

		contentPage = LayoutInflater.from(this).inflate(
				R.layout.layout_history_page, null);

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

		historySwitcher = (ViewSwitcher) contentPage
				.findViewById(R.id.viewswicher_history);
		historyListView = (ListView) contentPage
				.findViewById(R.id.history_item_list);
		historyListView.setCacheColorHint(Color.argb(0, 0, 0, 0));
		historyListView
				.setOnItemClickListener(new HistoryListItemClickListener());
		historyListView
				.setOnItemLongClickListener(new HistoryListItemLongClickListener());

		// 为菜单设置adapter
		underView.setAdapter(new MenuGridAdapter(HistoryPageActivity.this,
				MenuGridAdapter.HISTORY_PAGE_ID));

		topBarTitleTv = (TextView) contentPage.findViewById(R.id.topbar_title);
		setTopBarTitle(R.string.playhistory_page_title);

		slideBtn = (Button) contentPage.findViewById(R.id.slideBtn);
		slideBtn.setOnClickListener(new HistoryPageSlideBtnClickListener());

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

	private class GetPlayHistoryTask extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			historySwitcher.showNext();
		}

		@Override
		protected String doInBackground(String... params) {
			historyList = manager.getAllPlayHistory();
			historyChapterListAdapter.setHistoryList(historyList);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			historyListView.setAdapter(historyChapterListAdapter);
			historySwitcher.setDisplayedChild(0);
		}
	}

	private class ClearHistoryTask extends AsyncTask<String, Integer, Boolean> {
		ProgressDialog dlg;

		@Override
		protected void onPreExecute() {
			dlg = new ProgressDialog(HistoryPageActivity.this);
			dlg.setTitle("清空");
			dlg.setMessage("正在清空，请稍后...");
			dlg.setCancelable(false);
			dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dlg.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				manager.clearHistory();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			dlg.dismiss();
			if (result == true) {
				historyList.clear();
				historyChapterListAdapter.setHistoryList(historyList);
				historyChapterListAdapter.notifyDataSetChanged();
				ToastUtil.makeShortToast(HistoryPageActivity.this, "清空完毕");
			} else {
				ToastUtil
						.makeShortToast(HistoryPageActivity.this, "清空失败，请稍后再试");
			}
		}
	}

	private class HistoryPageSlideBtnClickListener implements
			View.OnClickListener {
		public void onClick(View v) {
			scrollView.clickSlideButton();
		}
	}

	private class HistoryListItemClickListener implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.e("chapters_url", historyList.get(position).getChapter_url());

			Intent intent = new Intent(HistoryPageActivity.this,
					PlayerEngine.class);
			intent.putExtra("audieo_name", historyList.get(position)
					.getChapter_name());
			intent.putExtra("audieo_url", historyList.get(position)
					.getChapter_url());
			intent.putExtra("source_name", historyList.get(position)
					.getSource_name());
			startService(intent);
			if (manager.hasBeenMyFavo(historyList.get(position)
					.getChapter_url())) {
				PlayControlActions.currentIsMyFavo = true;
				addMyFavoBtm.setImageResource(R.drawable.favorate_ok);
			} else {
				PlayControlActions.currentIsMyFavo = false;
				addMyFavoBtm.setImageResource(R.drawable.favorate);
			}
			broadcastController.setImageResource(R.drawable.pause_btn_selector);
		}
	}

	private class HistoryListItemLongClickListener implements
			AdapterView.OnItemLongClickListener {
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			ToastUtil.makeShortToast(HistoryPageActivity.this, "" + position);
			return true;
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
			ToastUtil.makeShortToast(HistoryPageActivity.this, "停止播放");
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
		Intent intent = new Intent(HistoryPageActivity.this, PlayerEngine.class);
		bindService(intent, conn, Service.BIND_AUTO_CREATE);
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
			ToastUtil.makeShortToast(HistoryPageActivity.this, content);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu delMenu = menu.addSubMenu(getResources().getString(
				R.string.del_menu_text));
		delMenu.add(0, 123, 0,
				getResources().getString(R.string.del_submenu_text));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem mi) {
		switch (mi.getItemId()) {
		case 123:
			new ClearHistoryTask().execute("begin");
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(getResources().getString(R.string.context_menu_play));
		menu.add(getResources().getString(R.string.context_menu_del));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		return super.onContextItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		manager.closeDB();
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
