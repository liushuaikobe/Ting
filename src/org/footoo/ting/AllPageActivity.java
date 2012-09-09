package org.footoo.ting;

import org.footoo.ting.adapter.CategoryListAdapter;
import org.footoo.ting.adapter.ChapterListAdapter;
import org.footoo.ting.adapter.MenuGridAdapter;
import org.footoo.ting.adapter.SourcesListAdapter;
import org.footoo.ting.ui.MainHorizontalScrollView;
import org.footoo.ting.util.SizeCallBackForMenu;
import org.footoo.ting.R;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class AllPageActivity extends MainBaseActivity {
	private ListView categoryLv;
	private ViewSwitcher viewSwitcher;

	private BaseAdapter categoryListAdapter; // 类别列表的adapter
	private BaseAdapter sourcesListAdapter;

	// 资源具体情况页面的一些View
	private ViewSwitcher detailViewSwitcher;
	private ListView chapterLv;

	private int currentPageId; // 标志着当前页面内容的Id
	private View AllPageListView; // 暂存contentPage内容的

	private static final int CATEGORY_PAGE = 0;
	private static final int SOURCES_PAGE = 1;
	private static final int DETAIL_PAGE = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();
		initViews();

		new Handler().postDelayed(new Runnable() {

			public void run() {
				categoryLv.setAdapter(categoryListAdapter);
				currentPageId = CATEGORY_PAGE;
				viewSwitcher.setDisplayedChild(0);

			}
		}, 2000);

	}

	private void initViews() {

		setContentPage(R.layout.layout_all_page, R.string.all_page_title,
				new CategoryPageSlideBtnClickListener(),
				new ConfigContentPageInterface() {

					public void config(View page) {

					}
				});

		// 菜单已经在BaseAvtivity中实例化，在子类中为菜单设置adapter即可
		underView.setAdapter(new MenuGridAdapter(AllPageActivity.this,
				MenuGridAdapter.ALL_PAGE_ID));

		// 初始化ListView
		categoryLv = new ListView(AllPageActivity.this);
		categoryLv.setCacheColorHint(Color.argb(0, 0, 0, 0));
		categoryLv.setDivider(getResources().getDrawable(
				R.drawable.list_divider_line));
		categoryLv.setDividerHeight(5);
		categoryLv.setOnItemClickListener(new MyItemClickListener());

		// 初始化ViewSwithcer
		viewSwitcher = (ViewSwitcher) contentPage
				.findViewById(R.id.viewswicher_category);
		viewSwitcher.addView(categoryLv);
		viewSwitcher.addView(getLayoutInflater().inflate(
				R.layout.layout_progress_page, null));
		viewSwitcher.showNext();
	}

	private void initData() {
		categoryListAdapter = new CategoryListAdapter(AllPageActivity.this);
		sourcesListAdapter = new SourcesListAdapter(AllPageActivity.this);
	}

	/**
	 * 在Category页面中，slideButton点击后是侧滑事件
	 */
	private class CategoryPageSlideBtnClickListener implements
			View.OnClickListener {
		public void onClick(View v) {
			scrollView.clickSlideButton();
		}
	}

	/**
	 * 在某一个Category下的Source页面中，slideButton点击后完成“返回”的功能，即切换当前页面为Category页面
	 */
	private class SourcePageSLideBtnCLickListener implements
			View.OnClickListener {
		public void onClick(View v) {
			currentPageId = CATEGORY_PAGE;
			slideBtn.setBackgroundResource(R.drawable.slidebtn_bg);
			slideBtn.setText("");
			slideBtn.setOnClickListener(new CategoryPageSlideBtnClickListener());
			categoryLv.setAdapter(categoryListAdapter);
		}

	}

	/**
	 * 在某一个Source的具体信息(Chapter)页面中，slideButton点击后完成“返回”的功能，即切换当前页面为Source列表页面
	 */
	private class DetailPageSlideBtnClickListener implements
			View.OnClickListener {
		public void onClick(View v) {
			currentPageId = SOURCES_PAGE;
			setContentPage(AllPageListView, "古典文学",
					new SourcePageSLideBtnCLickListener(),
					new ConfigContentPageInterface() {
						public void config(View page) {
							slideBtn.setBackgroundResource(R.drawable.go_back_selector);
							slideBtn.setOnClickListener(new SourcePageSLideBtnCLickListener());
							slideBtn.setText(R.string.go_back);
						}
					});
		}
	}

	/**
	 * 设置content page
	 * 
	 * @param layoutId
	 *            contentPage的布局Id
	 * @param topBarTitleStringResId
	 *            TitleBar上的文字字符串资源ID
	 * @param clickListener
	 *            Titlebar上的按钮的点击Listener
	 * @param cPageInterface
	 *            回调接口，对contentPage进行具体的配置
	 */
	private void setContentPage(int layoutId, int topBarTitleStringResId,
			View.OnClickListener clickListener,
			ConfigContentPageInterface cPageInterface) {
		if (scrollView == null) {
			scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView); // 初始化HSV
		}
		contentPage = LayoutInflater.from(this).inflate(layoutId, null); // 设置HSV上的ContentPage的内容

		topBarTitleTv = (TextView) contentPage.findViewById(R.id.topbar_title);
		setTopBarTitle(topBarTitleStringResId); // 设置ContentPage上TitleBar的文字

		slideBtn = (Button) contentPage.findViewById(R.id.slideBtn); // 设置ContentPage上SlideButton的点击事件
		slideBtn.setOnClickListener(clickListener);

		cPageInterface.config(contentPage);

		View leftView = new View(this); // 设置将ContentPage显示到HSV上+
		leftView.setBackgroundColor(Color.TRANSPARENT);
		((ViewGroup) scrollView.getChildAt(0)).removeAllViews();
		final View[] children = new View[] { leftView, contentPage };
		scrollView.initViews(children, new SizeCallBackForMenu(slideBtn),
				(View) findViewById(R.id.menu_layout));
		scrollView.setSlideButton(slideBtn);
	}

	/**
	 * 同上一个函数是重载函数，直接给出topbar的文字内容
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

		cPageInterface.config(contentPage);

		View leftView = new View(this); // 设置将ContentPage显示到HSV上+
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

		cPageInterface.config(contentPage);

		View leftView = new View(this); // 设置将ContentPage显示到HSV上+
		leftView.setBackgroundColor(Color.TRANSPARENT);
		((ViewGroup) scrollView.getChildAt(0)).removeAllViews();
		final View[] children = new View[] { leftView, contentPage };
		scrollView.initViews(children, new SizeCallBackForMenu(slideBtn),
				(View) findViewById(R.id.menu_layout));
		scrollView.setSlideButton(slideBtn);
	}

	/**
	 * 具体资源页面的章节列表的Item的点击事件
	 */
	private class MyChapterItemClickListener implements
			AdapterView.OnItemClickListener {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO 在这里播放选中的章节(Chapter)
		}

	}

	/**
	 * 分类列表和每一类资源详情的列表（其实它们是一个ListView，只是Adapter不同）的点击事件
	 */
	private class MyItemClickListener implements
			AdapterView.OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (currentPageId) {
			case CATEGORY_PAGE:
				currentPageId = SOURCES_PAGE;
				categoryLv.setAdapter(sourcesListAdapter);
				setTopBarTitle("古典文学");
				slideBtn.setBackgroundResource(R.drawable.go_back_selector);
				slideBtn.setOnClickListener(new SourcePageSLideBtnCLickListener());
				slideBtn.setText(R.string.go_back);
				break;
			case SOURCES_PAGE:
				currentPageId = DETAIL_PAGE;
				AllPageListView = contentPage; // 将内容是ListView保存起来
				setContentPage(R.layout.layout_source_detail, "红楼梦",
						new DetailPageSlideBtnClickListener(),
						new ConfigContentPageInterface() {
							public void config(View page) {
								slideBtn.setText(R.string.go_back);
								slideBtn.setBackgroundResource(R.drawable.go_back_selector);
								((ImageView) page
										.findViewById(R.id.source_cover_large))
										.setImageResource(R.drawable.sample_source_cover);
								((TextView) page
										.findViewById(R.id.source_desc_detail))
										.setText("本书是一部具有高度思想性和艺术性的伟大作品，作者具有初步的民主主义思想，他对现实社会、宫廷、官场的黑暗，封建贵族阶级及其家族的腐朽，对封建的科举、婚姻、奴婢、等级制度及社会统治思想等都进行了深刻的批判，并且提出了朦胧的带有初步民主主义性质的理想和主张。");
								((TextView) page
										.findViewById(R.id.lastmofify_time))
										.setText("2012-09-01 15:59");
								initChapterList(page);
							}
						});
				break;
			case DETAIL_PAGE:
				break;
			default:
				break;
			}
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

		chapterLv = new ListView(AllPageActivity.this);
		chapterLv.setCacheColorHint(Color.argb(0, 0, 0, 0));
		chapterLv.setDivider(getResources().getDrawable(
				R.drawable.list_divider_line));
		chapterLv.setDividerHeight(3);
		chapterLv.setOnItemClickListener(new MyChapterItemClickListener());

		detailViewSwitcher.addView(chapterLv);
		detailViewSwitcher.addView(getLayoutInflater().inflate(
				R.layout.layout_progress_page, null));
		detailViewSwitcher.showNext();
		// 暂时现在初始化章节列表ListView的时候制造一种假象
		new Handler().postDelayed(new Runnable() {

			public void run() {
				chapterLv.setAdapter(new ChapterListAdapter(
						AllPageActivity.this));

				detailViewSwitcher.setDisplayedChild(0);
			}
		}, 2000);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (MainHorizontalScrollView.underViewIsOut == true) {
				scrollView.clickSlideButton();
			} else {
				switch (currentPageId) {
				case CATEGORY_PAGE:
					this.finish();
					break;
				case SOURCES_PAGE:
					currentPageId = CATEGORY_PAGE;
					slideBtn.setBackgroundResource(R.drawable.slidebtn_bg);
					slideBtn.setText("");
					slideBtn.setOnClickListener(new CategoryPageSlideBtnClickListener());
					categoryLv.setAdapter(categoryListAdapter);
					break;
				case DETAIL_PAGE:
					currentPageId = SOURCES_PAGE;
					setContentPage(AllPageListView, "古典文学",
							new SourcePageSLideBtnCLickListener(),
							new ConfigContentPageInterface() {
								public void config(View page) {
									slideBtn.setBackgroundResource(R.drawable.go_back_selector);
									slideBtn.setOnClickListener(new SourcePageSLideBtnCLickListener());
									slideBtn.setText(R.string.go_back);
								}
							});
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
}
