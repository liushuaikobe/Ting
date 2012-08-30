package org.footoo.ting;

import org.footoo.ting.adapter.CategoryListAdapter;
import org.footoo.ting.adapter.MenuGridAdapter;
import org.footoo.ting.ui.MainHorizontalScrollView;
import org.footoo.ting.util.SizeCallBackForMenu;
import org.footoo.ting.util.ToastUtil;
import org.footoo.ting.R;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class AllPageActivity extends MainBaseActivity {
	private ListView categoryLv;
	private ViewSwitcher viewSwitcher;

	private BaseAdapter categoryListAdapter; // 类别列表的adapter

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();
		initViews();

		new Handler().postDelayed(new Runnable() {

			public void run() {
				categoryLv.setAdapter(categoryListAdapter);
				viewSwitcher.setDisplayedChild(0);

			}
		}, 2000);

	}

	private void initViews() {

		setContentPage(R.layout.layout_all_page, R.string.all_page_title,
				new MySlideButtonClickListener());

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
	}

	private class MySlideButtonClickListener implements View.OnClickListener {
		public void onClick(View v) {
			scrollView.clickSlideButton();
		}
	}

	/**
	 * 设置content page
	 * 
	 * @param layoutId
	 * @param topBarTitleStringResId
	 * @param clickListener
	 */
	private void setContentPage(int layoutId, int topBarTitleStringResId,
			View.OnClickListener clickListener) {
		if (scrollView == null) {
			scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView); // 初始化HSV
		}
		contentPage = LayoutInflater.from(this).inflate(layoutId, null); // 设置HSV上的ContentPage的内容

		topBarTitleTv = (TextView) contentPage.findViewById(R.id.topbar_title);
		setTopBarTitle(topBarTitleStringResId); // 设置ContentPage上TitleBar的文字

		slideBtn = (Button) contentPage.findViewById(R.id.slideBtn); // 设置ContentPage上SlideButton的点击事件
		slideBtn.setOnClickListener(clickListener);

		View leftView = new View(this); // 设置将ContentPage显示到HSV上+
		leftView.setBackgroundColor(Color.TRANSPARENT);
		((ViewGroup) scrollView.getChildAt(0)).removeAllViews();
		final View[] children = new View[] { leftView, contentPage };
		scrollView.initViews(children, new SizeCallBackForMenu(slideBtn),
				(View) findViewById(R.id.menu_layout));
		scrollView.setSlideButton(slideBtn);
	}

	private class MyItemClickListener implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			setContentPage(R.layout.layout_hot_page, R.string.hot_page_title,
					new MySlideButtonClickListener());
		}
	}
}
