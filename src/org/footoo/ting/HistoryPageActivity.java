package org.footoo.ting;

import org.footoo.ting.adapter.HistoryChapterListAdapter;
import org.footoo.ting.adapter.MenuGridAdapter;
import org.footoo.ting.ui.MainHorizontalScrollView;
import org.footoo.ting.util.SizeCallBackForMenu;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class HistoryPageActivity extends MainBaseActivity {

	private ViewSwitcher historySwitcher;
	private ListView historyList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initViews();
		historySwitcher.showNext();
		new Handler().postDelayed(new Runnable() {
			public void run() {
				historyList.setAdapter(new HistoryChapterListAdapter(
						HistoryPageActivity.this));
				historySwitcher.setDisplayedChild(0);
			}
		}, 1000);
	}

	private void initViews() {
		scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView);

		contentPage = LayoutInflater.from(this).inflate(
				R.layout.layout_history_page, null);

		historySwitcher = (ViewSwitcher) contentPage
				.findViewById(R.id.viewswicher_history);
		historyList = (ListView) contentPage
				.findViewById(R.id.history_item_list);
		historyList.setCacheColorHint(Color.argb(0, 0, 0, 0));

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

	private class HistoryPageSlideBtnClickListener implements
			View.OnClickListener {
		public void onClick(View v) {
			scrollView.clickSlideButton();
		}
	}
}
