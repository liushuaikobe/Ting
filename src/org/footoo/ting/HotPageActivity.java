package org.footoo.ting;

import org.footoo.ting.adapter.HotPageLargePicAdapter;
import org.footoo.ting.adapter.HotPageThumbPicAdapter;
import org.footoo.ting.adapter.MenuGridAdapter;
import org.footoo.ting.ui.MainHorizontalScrollView;
import org.footoo.ting.util.SizeCallBackForMenu;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

public class HotPageActivity extends MainBaseActivity {

	private ViewFlow viewFlow;
	private GridView thumbGridView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initViews();
		initViewFlow();
	}

	private void initViews() {
		scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView);

		contentPage = LayoutInflater.from(this).inflate(
				R.layout.layout_hot_page, null);

		slideBtn = (Button) contentPage.findViewById(R.id.slideBtn);
		slideBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				scrollView.clickSlideButton();
			}
		});

		View leftView = new View(this);
		leftView.setBackgroundColor(Color.TRANSPARENT);
		final View[] children = new View[] { leftView, contentPage };
		scrollView.initViews(children, new SizeCallBackForMenu(slideBtn),
				(View) findViewById(R.id.menu_layout));
		scrollView.setSlideButton(slideBtn);

		// 为菜单设置adapter
		underView.setAdapter(new MenuGridAdapter(HotPageActivity.this,
				MenuGridAdapter.HOT_PAGE_ID));

		thumbGridView = (GridView) findViewById(R.id.hot_thumbnail_gridView);
		// 为HotPage地下的小GridView设置adapter
		thumbGridView.setAdapter(new HotPageThumbPicAdapter(
				HotPageActivity.this));
	}

	private void initViewFlow() {
		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		viewFlow.setAdapter(new HotPageLargePicAdapter(HotPageActivity.this));
		viewFlow.setmSideBuffer(3);

		CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		viewFlow.setFlowIndicator(indic);

		// 为large picture设置点击事件
		viewFlow.getChildAt(0).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(HotPageActivity.this, "fuck", Toast.LENGTH_SHORT)
						.show();
			}
		});

		viewFlow.setSelection(3 * 1000);
		viewFlow.startAutoFlowTimer();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (MainHorizontalScrollView.underViewIsOut == true) {
				scrollView.clickSlideButton();
			} else {
				this.finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
