package org.footoo.ting;

import org.footoo.ting.adapter.HotPageLargePicAdapter;
import org.footoo.ting.adapter.HotPageThumbPicAdapter;
import org.footoo.ting.adapter.MenuGridAdapter;
import org.footoo.ting.media.PlayerEngine;
import org.footoo.ting.ui.MainHorizontalScrollView;
import org.footoo.ting.util.SizeCallBackForMenu;
import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class HotPageActivity extends MainBaseActivity {

	public static String URL = "url of data";
	
	
	private ViewFlow viewFlow;
	private GridView thumbGridView;
	private ImageView broadcastController;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("cain", "current thread:"+Thread.currentThread().getId());
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
		// // 为HotPage地下的小GridView设置adapter
		thumbGridView.setAdapter(new HotPageThumbPicAdapter(
				HotPageActivity.this));

		broadcastController = (ImageView) ((View) contentPage
				.findViewById(R.id.tmp_btm_ctrl))
				.findViewById(R.id.bottom_control_btn);

		broadcastController.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				Intent intent = new Intent(HotPageActivity.this, PlayerEngine.class);
				String uri = "http://172.16.244.31/Apologize.mp3";
				intent.putExtra(URL, uri);
				startService(intent);
			}
		});
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
