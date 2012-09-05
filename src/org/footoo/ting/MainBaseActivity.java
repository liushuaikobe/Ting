package org.footoo.ting;

import java.util.ArrayList;
import java.util.HashMap;

import org.footoo.ting.adapter.MenuGridAdapter;
import org.footoo.ting.ui.MainHorizontalScrollView;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainBaseActivity extends Activity {
	protected MainHorizontalScrollView scrollView;
	protected GridView underView;
	protected View contentPage;
	protected Button slideBtn;

	protected TextView topBarTitleTv; // TopBar的题目TextView，在子类中实例化

	public MainHorizontalScrollView getScrollView() {
		return scrollView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		underView = (GridView) findViewById(R.id.mGridView);
		// initUnderView();
		// TODO set the adapter for the menu GridView in the subclass
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

	/**
	 * set the title bar's title
	 * 
	 * @param stringResId
	 */
	protected void setTopBarTitle(int stringResId) {
		topBarTitleTv.setText(stringResId);
	}

	protected void setTopBarTitle(String title) {
		topBarTitleTv.setText(title);
	}

	/**
	 * This fun is just for test
	 */
	private void initUnderView() {
		ArrayList<HashMap<String, Object>> menuImgitem = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map;
		int[] menuItemDrawableId = new int[] { R.drawable.menu_hot,
				R.drawable.menu_all, R.drawable.menu_favorate,
				R.drawable.menu_history, R.drawable.menu_local,
				R.drawable.menu_more };
		for (int i = 0; i < menuItemDrawableId.length; i++) {
			map = new HashMap<String, Object>();
			map.put("menu_item_img", menuItemDrawableId[i]);
			menuImgitem.add(map);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, menuImgitem,
				R.layout.layout_menu_grid_item,
				new String[] { "menu_item_img" }, new int[] { R.id.menu_item });
		underView.setAdapter(simpleAdapter);
	}
}
