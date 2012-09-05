package org.footoo.ting;

import org.footoo.ting.adapter.ChapterListAdapter;
import org.footoo.ting.adapter.LocalSourceGridAdapter;
import org.footoo.ting.adapter.MenuGridAdapter;
import org.footoo.ting.ui.MainHorizontalScrollView;
import org.footoo.ting.util.SizeCallBackForMenu;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class LocalSourceActivity extends MainBaseActivity {
	private ViewSwitcher localSourceSwitcher;
	private GridView localSourceGrid;

	private int currentPageFlag;

	private static final int SOURCE_PAGE = 1;
	private static final int CHAPTER_PAGE = 2;

	private LocalSourceGridAdapter localSourceGridAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();
		initViews();
	}

	private void initData() {
		localSourceGridAdapter = new LocalSourceGridAdapter(
				LocalSourceActivity.this);
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
						System.out
								.println("**********************************************");
						localSourceGrid
								.setOnItemClickListener(new LocalSourceGridItemClickListener());

						// 制造假象
						new Handler().postDelayed(new Runnable() {
							public void run() {
								localSourceSwitcher.setDisplayedChild(0);
							}
						}, 1000);
					}
				});
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

		}
	}

	/**
	 * 本地资源GridView点击事件
	 */
	private class LocalSourceGridItemClickListener implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			System.out
					.println("fuck!!!----*-------------------------------------------fuck!!!");
			ListView chapterList = new ListView(LocalSourceActivity.this);
			chapterList.setAdapter(new ChapterListAdapter(
					LocalSourceActivity.this));
			LinearLayout tmpLinearLayout = (LinearLayout) contentPage
					.findViewById(R.id.my_ll);
			tmpLinearLayout.removeAllViews();
			tmpLinearLayout.addView(chapterList);
		}
	}

	private interface ConfigContentPageInterface {
		void configPage(View page);
	}
}
