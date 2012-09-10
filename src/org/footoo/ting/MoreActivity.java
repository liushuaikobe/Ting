package org.footoo.ting;

import org.footoo.ting.adapter.MenuGridAdapter;
import org.footoo.ting.adapter.MoreListAdapter;
import org.footoo.ting.ui.MainHorizontalScrollView;
import org.footoo.ting.util.AppUtil;
import org.footoo.ting.util.SizeCallBackForMenu;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MoreActivity extends MainBaseActivity {

	private ListView menuLv;

	private MoreListAdapter menuLvAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();
		initViews();
	}

	private void initData() {
		menuLvAdapter = new MoreListAdapter(MoreActivity.this);
		menuLvAdapter.setCacheSize("当前缓存4.07MB");
	}

	private void initViews() {
		scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView);

		contentPage = LayoutInflater.from(this).inflate(
				R.layout.layout_more_page, null);

		menuLv = (ListView) contentPage.findViewById(R.id.more_item_list);
		menuLv.setCacheColorHint(Color.argb(0, 0, 0, 0));
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

	private class ItemListClickListener implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
				break;
			case 1:
				break;
			case 2:
				AppUtil.showAboutDialog(MoreActivity.this);
				break;
			default:
				break;
			}
		}
	}

	private class MorepageSlideBtnClickListener implements View.OnClickListener {
		public void onClick(View v) {
			scrollView.clickSlideButton();
		}
	}
}
