package org.footoo.ting;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.footoo.ting.adapter.ChapterListAdapter;
import org.footoo.ting.adapter.HotPageLargePicAdapter;
import org.footoo.ting.adapter.HotPageThumbPicAdapter;
import org.footoo.ting.adapter.MenuGridAdapter;
import org.footoo.ting.entity.Book;
import org.footoo.ting.entity.Chapter;
import org.footoo.ting.entity.PlayHistoryDbItem;
import org.footoo.ting.media.PlayControlActions;
import org.footoo.ting.media.PlayerEngine;
import org.footoo.ting.media.ServerInfo;
import org.footoo.ting.storage.DBManager;
import org.footoo.ting.ui.MainHorizontalScrollView;
import org.footoo.ting.util.AppUtil;
import org.footoo.ting.util.FileAccess;
import org.footoo.ting.util.HttpUtil;
import org.footoo.ting.util.SizeCallBackForMenu;
import org.footoo.ting.util.ToastUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
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
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class HotPageActivity extends MainBaseActivity {
	public static final String URL = "uri";

	public static final int HOT_CATEGORY_ID = 0;

	private int currentPageFlag;

	private static final int HOT_PAGE = 0;
	private static final int DETAIL_PAGE = 1;

	private ViewFlow viewFlow;
	private GridView thumbGridView;
	private ViewGroup gridParent;

	private ImageView broadcastController; // 底部控制板的一些View
	private SeekBar skbProgress;
	private TextView nowPlayingTitle;
	private ImageView addMyFAvoBtm;

	private PlayerEngine.PlayControlBinder binder;

	private ShowToastReceiver showToastReceiver;
	private UpdateSkBarFirstProgress updateSkBarFirstProgress;
	private UpdateSkBarSecondProgress updateSkBarSecondProgress;
	private UpdateNowplayingTitle updateNowplayingTitle;

	private View cacheView; // 缓存最热页面的View

	private ViewSwitcher detailViewSwitcher; // 具体资源页面的一些View
	private ListView chapterLv;
	private ImageView coverImgViewDetail;
	private Bitmap coverImgDetail;
	private ArrayList<Chapter> chapters; // 存放章节列表的List
	private ChapterListAdapter chapterListAdapter;

	private JSONObject resultJSObject;
	private JSONObject chapterJSObject;

	private DBManager manager;

	private int currentSourcePos;

	private HotPageThumbPicAdapter thumbAdapter;
	private HotPageLargePicAdapter largeAdapter;

	private ArrayList<Book> hotBookLarge;
	private ArrayList<Book> hotBookThumb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();
		initViews();
		initViewFlow();
		BindPlayService();
		registerReceivers();

		new Thread(new Runnable() {
			public void run() {
				getSourceList(ServerInfo.ServerURl + "/hot/hot.json");
			}
		}).start();
	}

	private void initData() {
		hotBookThumb = new ArrayList<Book>();
		hotBookLarge = new ArrayList<Book>();
		chapters = new ArrayList<Chapter>();

		thumbAdapter = new HotPageThumbPicAdapter(HotPageActivity.this);
		largeAdapter = new HotPageLargePicAdapter(HotPageActivity.this);
		chapterListAdapter = new ChapterListAdapter(HotPageActivity.this);

		showToastReceiver = new ShowToastReceiver();
		updateSkBarFirstProgress = new UpdateSkBarFirstProgress();
		updateSkBarSecondProgress = new UpdateSkBarSecondProgress();
		updateNowplayingTitle = new UpdateNowplayingTitle();

		manager = new DBManager(HotPageActivity.this);
	}

	private void initViews() {

		setContentPage(R.layout.layout_hot_page,
				getResources().getString(R.string.hot_page_title),
				new HotPageSlideBtnClickListner(),
				new ConfigContentPageInterface() {
					public void config(View page) {
						thumbGridView = (GridView) page
								.findViewById(R.id.hot_thumbnail_gridView);
						gridParent = (ViewGroup) page.findViewById(R.id.my_ll);
					}
				});

		currentPageFlag = HOT_PAGE;

		// 为菜单设置adapter
		underView.setAdapter(new MenuGridAdapter(HotPageActivity.this,
				MenuGridAdapter.HOT_PAGE_ID));
	}

	private void initViewFlow() {
		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		viewFlow.setmSideBuffer(3);

		CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		viewFlow.setFlowIndicator(indic);

		viewFlow.setOnItemClickListener(new LargePicCLickListener()); // ?
		viewFlow.setSelection(3 * 1000);
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
	 * 最热页面，slideBtn的点击事件是滑动HSV
	 */
	private class HotPageSlideBtnClickListner implements View.OnClickListener {
		public void onClick(View v) {
			scrollView.clickSlideButton();
		}
	}

	/**
	 * 具体资源页面，slideBtn的点击事件是返回最热页面
	 */
	private class SourceDetailPageSlideBtnClickListener implements
			View.OnClickListener {
		public void onClick(View v) {
			setContentPage(cacheView, "最热", new HotPageSlideBtnClickListner(),
					new ConfigContentPageInterface() {
						public void config(View page) {
							slideBtn.setBackgroundResource(R.drawable.slidebtn_bg);
							// slideBtn.setText("");
						}
					});
			currentPageFlag = HOT_PAGE;
		}
	}

	/**
	 * 最热页面上面大图的点击事件
	 */
	private class LargePicCLickListener implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ToastUtil.makeShortToast(HotPageActivity.this, "" + position);
			currentPageFlag = DETAIL_PAGE;
		}
	}

	/**
	 * 最热页面小图的点击事件
	 */
	private class ThumbGridClickListner implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			cacheView = contentPage;
			currentPageFlag = DETAIL_PAGE;
			currentSourcePos = position;
			setContentPage(R.layout.layout_source_detail,
					hotBookThumb.get(position).getSourceName(),
					new SourceDetailPageSlideBtnClickListener(),
					new ConfigContentPageInterface() {
						public void config(View page) {
							// slideBtn.setText(R.string.go_back);
							slideBtn.setBackgroundResource(R.drawable.go_back_selector);
							// 启动新线程获取资源的封面
							new Thread(new Runnable() {
								public void run() {
									coverImgDetail = getAvatarFromServer(hotBookThumb
											.get(currentSourcePos).getImgUrl());
									handler.sendEmptyMessage(4);
								}
							}).start();
							coverImgViewDetail = ((ImageView) page
									.findViewById(R.id.source_cover_large));
							((TextView) page
									.findViewById(R.id.source_desc_detail))
									.setText(hotBookThumb.get(currentSourcePos)
											.getSource_describe());
							((TextView) page.findViewById(R.id.lastmofify_time))
									.setText(hotBookThumb.get(currentSourcePos)
											.getLast_modify());
							initChapterList(page);
						}
					});
		}
	}

	/**
	 * 具体资源页面的章节列表的Item的点击事件
	 */
	private class MyChapterItemClickListener implements
			AdapterView.OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.e("chapters_url", chapters.get(position).getChapterUrl());
			PlayHistoryDbItem history = new PlayHistoryDbItem();
			history.setChapter_name(chapters.get(position).getChapterName());
			history.setChapter_url(chapters.get(position).getChapterUrl());
			history.setLast_time(AppUtil.getCurrentTimeString());
			history.setSource_name(hotBookThumb.get(currentSourcePos)
					.getSourceName());
			manager.insertOneHistory(history);
			Intent intent = new Intent(HotPageActivity.this, PlayerEngine.class);
			intent.putExtra("audieo_name", chapters.get(position)
					.getChapterName());
			intent.putExtra("audieo_url", chapters.get(position)
					.getChapterUrl());
			intent.putExtra("source_name", hotBookThumb.get(currentSourcePos)
					.getSourceName());
			startService(intent);
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
			ToastUtil.makeShortToast(HotPageActivity.this, "停止播放");
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
	 * 添加到我的收藏
	 */
	private class AddFavoClickListener implements View.OnClickListener {
		public void onClick(View v) {
			if (PlayControlActions.playerIsStoping == true) {
				return;
			}
			if (manager.insertOneMyFavo(binder.getMyFavoDbItem()) == true) {
				PlayControlActions.currentIsMyFavo = true;
				ToastUtil.makeShortToast(HotPageActivity.this, getResources()
						.getString(R.string.have_added_myfavo));
				if (chapterListAdapter != null) {
					chapterListAdapter.notifyDataSetChanged();
				}
				addMyFAvoBtm.setImageResource(R.drawable.favorate_ok);
			} else {
				ToastUtil.makeShortToast(HotPageActivity.this, getResources()
						.getString(R.string.has_been_myfavo));
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
		Intent intent = new Intent(HotPageActivity.this, PlayerEngine.class);
		bindService(intent, conn, Service.BIND_AUTO_CREATE);
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
	 * 初始化具体资源页面里面章节列表的List
	 * 
	 * @param Page
	 *            ContentPage
	 */
	private void initChapterList(View page) {
		detailViewSwitcher = (ViewSwitcher) page
				.findViewById(R.id.chapters_switcher);

		chapterLv = new ListView(HotPageActivity.this);
		chapterLv.setCacheColorHint(Color.argb(0, 0, 0, 0));
		chapterLv.setDivider(getResources().getDrawable(
				R.drawable.list_divider_line));
		chapterLv.setDividerHeight(3);
		chapterLv.setOnItemClickListener(new MyChapterItemClickListener());

		detailViewSwitcher.addView(chapterLv);
		detailViewSwitcher.addView(getLayoutInflater().inflate(
				R.layout.layout_progress_page, null));
		detailViewSwitcher.showNext();

		new Thread(new Runnable() {
			public void run() {
				getChapterList(hotBookThumb.get(currentSourcePos)
						.getChapterJonUrl());
			}
		}).start();
	}

	/**
	 * 设置contentPage的内容为某一布局
	 * 
	 * @param layoutId
	 *            contentPage的布局Id
	 * @param topBarTitle
	 *            TitleBar上的文字
	 * @param clickListener
	 *            Titlebar上的按钮的点击Listener
	 * @param cPageInterface
	 *            回调接口，对contentPage进行具体的配置
	 */
	private void setContentPage(int layoutId, String topBarTitle,
			View.OnClickListener clickListener,
			ConfigContentPageInterface cPageInterface) {
		if (scrollView == null) {
			scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView); // 初始化HSV
		}
		contentPage = LayoutInflater.from(this).inflate(layoutId, null); // 设置HSV上的ContentPage的内容

		topBarTitleTv = (TextView) contentPage.findViewById(R.id.topbar_title);
		setTopBarTitle(topBarTitle); // 设置ContentPage上TitleBar的文字

		slideBtn = (Button) contentPage.findViewById(R.id.slideBtn); // 设置ContentPage上SlideButton的点击事件
		slideBtn.setOnClickListener(clickListener);

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

		cPageInterface.config(contentPage);

		View leftView = new View(this); // 设置将ContentPage显示到HSV上
		leftView.setBackgroundColor(Color.TRANSPARENT);
		((ViewGroup) scrollView.getChildAt(0)).removeAllViews();
		final View[] children = new View[] { leftView, contentPage };
		scrollView.initViews(children, new SizeCallBackForMenu(slideBtn),
				(View) findViewById(R.id.menu_layout));
		scrollView.setSlideButton(slideBtn);
	}

	/**
	 * 同上一个函数是重载函数，直接设置contentPage为某个已存在的View
	 * 
	 * @param view
	 *            设置contentPage的内容为View
	 * @param topBarTitle
	 *            TitleBar上的文字
	 * @param clickListener
	 *            Titlebar上的按钮的点击Listener
	 * @param cPageInterface
	 *            回调接口，对contentPage进行具体的配置
	 */
	private void setContentPage(View view, String topBarTitle,
			View.OnClickListener clickListener,
			ConfigContentPageInterface cPageInterface) {
		if (scrollView == null) {
			scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView); // 初始化HSV
		}
		contentPage = view; // 设置HSV上的ContentPage的内容

		topBarTitleTv = (TextView) contentPage.findViewById(R.id.topbar_title);
		setTopBarTitle(topBarTitle); // 设置ContentPage上TitleBar的文字

		slideBtn = (Button) contentPage.findViewById(R.id.slideBtn); // 设置ContentPage上SlideButton的点击事件
		slideBtn.setOnClickListener(clickListener);

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

		cPageInterface.config(contentPage);

		View leftView = new View(this); // 设置将ContentPage显示到HSV上
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
			ToastUtil.makeShortToast(HotPageActivity.this, content);
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
	 * 通过http请求获取书籍列表
	 * 
	 * @param url
	 *            要解析的Json的URl
	 */
	private void getSourceList(String urlStr) {
		if (!AppUtil.isNetworkAvailable(this)) { // 判断网络连接情况
			handler.sendEmptyMessage(0);
			return;
		}
		try {
			URL url = new URL(urlStr);
			URLConnection connection = (URLConnection) url.openConnection();
			connection.connect();

			InputStream is = connection.getInputStream();
			byte[] buf = new byte[2048];
			int count = is.read(buf, 0, buf.length);
			resultJSObject = new JSONObject(new String(buf, 0, count));
			Log.e("JSON", resultJSObject.toString());

			if (resultJSObject == null || resultJSObject.equals("")
					|| resultJSObject.getInt("category_id") != HOT_CATEGORY_ID) {
				handler.sendEmptyMessage(1);
			} else {
				JSONArray hotContent = resultJSObject.getJSONArray("content");
				Book book;
				// HotPage上大图展示
				for (int i = 0; i < 3; i++) {
					JSONObject eachBook = (JSONObject) hotContent.opt(i);
					book = new Book(HOT_CATEGORY_ID,
							eachBook.getInt("source_id"),
							eachBook.getString("source_name"),
							eachBook.getString("img_url"),
							eachBook.getString("source_describe"),
							eachBook.getString("last_modify"),
							eachBook.getString("chapter_json_url"));
					hotBookLarge.add(book);
				}
				// HotPage上下面小图展示
				for (int i = 3; i < hotContent.length(); i++) {
					JSONObject eachBook = (JSONObject) hotContent.opt(i);
					book = new Book(HOT_CATEGORY_ID,
							eachBook.getInt("source_id"),
							eachBook.getString("source_name"),
							eachBook.getString("img_url"),
							eachBook.getString("source_describe"),
							eachBook.getString("last_modify"),
							eachBook.getString("chapter_json_url"));
					hotBookThumb.add(book);
				}
				handler.sendEmptyMessage(2);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过http请求章节列表
	 * 
	 * @param urlStr
	 */
	private void getChapterList(String urlStr) {
		if (!AppUtil.isNetworkAvailable(this)) { // 判断网络连接情况
			handler.sendEmptyMessage(0);
			return;
		}
		try {
			URL url = new URL(urlStr);
			URLConnection connection = (URLConnection) url.openConnection();
			connection.connect();

			InputStream is = connection.getInputStream();
			byte[] buf = new byte[2048];
			int count = is.read(buf, 0, buf.length);
			chapterJSObject = new JSONObject(new String(buf, 0, count));
			Log.e("chapterJSON", chapterJSObject.toString());

			if (chapterJSObject == null || chapterJSObject.equals("")) {
				handler.sendEmptyMessage(1);
			} else {
				JSONArray chapterContent = chapterJSObject
						.getJSONArray("content");
				Chapter chapter;
				chapters.clear();
				for (int i = 0; i < chapterContent.length(); i++) {
					JSONObject eachChapter = (JSONObject) chapterContent.opt(i);
					chapter = new Chapter(eachChapter.getInt("chapter_id"),
							eachChapter.getString("chapter_name"),
							eachChapter.getString("chapter_url"));
					chapters.add(chapter);
				}
				handler.sendEmptyMessage(3);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				ToastUtil.makeShortToast(HotPageActivity.this, getResources()
						.getString(R.string.network_unavaliable));
				break;
			case 1:
				ToastUtil.makeShortToast(HotPageActivity.this, getResources()
						.getString(R.string.service_unavaliable));
				break;
			case 2:
				thumbAdapter.setBook(hotBookThumb);
				largeAdapter.setBooks(hotBookLarge);

				thumbAdapter.setGridParent(gridParent);
				largeAdapter.setParent(gridParent);

				thumbGridView.setAdapter(thumbAdapter);
				viewFlow.setAdapter(largeAdapter);
				viewFlow.startAutoFlowTimer();
				// viewFlow.setOnItemClickListener(new LargePicCLickListener());
				// viewFlow.getChildAt(0).setOnClickListener(
				// new View.OnClickListener() {
				// public void onClick(View v) {
				// Log.e("ping", "0");
				// }
				// });
				// viewFlow.getChildAt(1).setOnClickListener(
				// new View.OnClickListener() {
				// public void onClick(View v) {
				// Log.e("ping", "1");
				// }
				// });
				// viewFlow.getChildAt(2).setOnClickListener(
				// new View.OnClickListener() {
				// public void onClick(View v) {
				// Log.e("ping", "2");
				// }
				// });

				thumbGridView
						.setOnItemClickListener(new ThumbGridClickListner());
				break;
			case 3:
				chapterListAdapter.setChapterList(chapters);
				chapterLv.setAdapter(chapterListAdapter);
				detailViewSwitcher.setDisplayedChild(0);
				break;
			case 4:
				if (coverImgDetail != null) {
					coverImgViewDetail.setImageBitmap(coverImgDetail);
				}
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 从服务器上下载某一具体资源的Bitmap
	 * 
	 * @return 封面的Bitmap
	 */
	private Bitmap getAvatarFromServer(String coverUrlStr) {
		try {
			URL avatarUrl = new URL(coverUrlStr);
			byte[] data = HttpUtil.readInputStream((InputStream) avatarUrl
					.openStream());
			Bitmap bitmap = FileAccess.decodeSampledBitmapFromByteArray(data,
					90, 110);
			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (MainHorizontalScrollView.underViewIsOut == true) {
				scrollView.clickSlideButton();
			} else {
				switch (currentPageFlag) {
				case HOT_PAGE:
					AppUtil.QuitHintDialog(HotPageActivity.this);
					break;
				case DETAIL_PAGE:
					setContentPage(cacheView, "最热",
							new HotPageSlideBtnClickListner(),
							new ConfigContentPageInterface() {
								public void config(View page) {
									slideBtn.setBackgroundResource(R.drawable.slidebtn_bg);
									// slideBtn.setText("");
								}
							});
					currentPageFlag = HOT_PAGE;
					break;
				default:
					break;
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 为配置ContentPage提供回调接口
	 */
	private interface ConfigContentPageInterface {
		void config(View page);
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
