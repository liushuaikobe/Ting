package org.footoo.ting;

import org.footoo.ting.adapter.ChapterListAdapter;
import org.footoo.ting.adapter.MenuGridAdapter;
import org.footoo.ting.adapter.MyFavoGridAdapter;
import org.footoo.ting.ui.MainHorizontalScrollView;
import org.footoo.ting.util.SizeCallBackForMenu;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class MyFavoActivity extends MainBaseActivity {
	private ViewSwitcher myFavoSwitcher;
	private GridView myFavoGridView;

	private MyFavoGridAdapter myFavoGridAdapter;

	private int currentPageFlag;

	private static final int SOURCE_PAGE = 1;
	private static final int CHAPTER_PAGE = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();
		initViews();
	}

	private void initViews() {

		underView.setAdapter(new MenuGridAdapter(MyFavoActivity.this,
				MenuGridAdapter.FAVORATE_PAGE_ID));

		currentPageFlag = SOURCE_PAGE;
		setContentPage(R.layout.layout_myfavo_page, R.string.myfavo_page_title,
				new MyFaveGridPageSlideBtnClickListener(),
				new ConfigContentPageInterface() {
					public void configPage(View page) {
						myFavoSwitcher = (ViewSwitcher) page
								.findViewById(R.id.viewswicher_myfavo);
						myFavoSwitcher.showNext();

						myFavoGridView = (GridView) page
								.findViewById(R.id.myfavo_grid);
						myFavoGridView.setAdapter(myFavoGridAdapter);
						myFavoGridView
								.setOnItemClickListener(new MyFavoGridItemClickListener());
						// 制造假象
						new Handler().postDelayed(new Runnable() {
							public void run() {
								myFavoSwitcher.setDisplayedChild(0);
							}
						}, 1000);
					}
				});
	}

	private void initData() {
		myFavoGridAdapter = new MyFavoGridAdapter(MyFavoActivity.this);
	}

	/**
	 * 在显示我的最爱的Grid页面中，slideButton的点击事件是侧滑
	 */
	private class MyFaveGridPageSlideBtnClickListener implements
			View.OnClickListener {
		public void onClick(View v) {
			scrollView.clickSlideButton();
		}
	}

	/**
	 * 在具体的收藏的章节列表页面里面，slideButton的点击事件是返回我的最爱的Grid
	 */
	private class ChapterListPageSlideBtnListener implements
			View.OnClickListener {
		public void onClick(View v) {

		}
	}

	/**
	 * 我的最爱页面中Grid的点击事件
	 */
	private class MyFavoGridItemClickListener implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			ListView chapterList = new ListView(MyFavoActivity.this);
			chapterList.setAdapter(new ChapterListAdapter(MyFavoActivity.this));
			LinearLayout tmpLinearLayout = (LinearLayout) contentPage
					.findViewById(R.id.my_ll);
			tmpLinearLayout.removeAllViews();
			tmpLinearLayout.addView(chapterList);
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

		configInterface.configPage(contentPage);

		View leftView = new View(this);
		leftView.setBackgroundColor(Color.TRANSPARENT);
		((ViewGroup) scrollView.getChildAt(0)).removeAllViews();
		final View[] children = new View[] { leftView, contentPage };
		scrollView.initViews(children, new SizeCallBackForMenu(slideBtn),
				(View) findViewById(R.id.menu_layout));
		scrollView.setSlideButton(slideBtn);
	}

	private interface ConfigContentPageInterface {
		void configPage(View page);
	}
}
