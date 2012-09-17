package org.footoo.ting.adapter;

import java.util.ArrayList;

import org.footoo.ting.AllPageActivity;
import org.footoo.ting.HistoryPageActivity;
import org.footoo.ting.HotPageActivity;
import org.footoo.ting.LocalSourceActivity;
import org.footoo.ting.MoreActivity;
import org.footoo.ting.MyFavoActivity;
import org.footoo.ting.R;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class MenuGridAdapter extends BaseAdapter {

	private Activity mContext;

	private int myId;
	private ArrayList<Integer> menuImgitem;

	public static final int HOT_PAGE_ID = 0;
	public static final int ALL_PAGE_ID = 1;
	public static final int FAVORATE_PAGE_ID = 2;
	public static final int HISTORY_PAGE_ID = 3;
	public static final int LOCAL_PAGE_ID = 4;
	public static final int MORE_PAGE_ID = 5;

	public MenuGridAdapter(Activity context, int currentId) {
		this.mContext = context;
		this.myId = currentId;
		menuImgitem = new ArrayList<Integer>();
		initMenuSource();
	}

	private void initMenuSource() {
		int[] menuItemDrawableId = new int[] { R.drawable.menu_hot,
				R.drawable.menu_all, R.drawable.menu_favorate,
				R.drawable.menu_history, R.drawable.menu_local,
				R.drawable.menu_more };
		for (int i = 0; i < menuItemDrawableId.length; i++) {
			menuImgitem.add(menuItemDrawableId[i]);
		}
	}

	public int getCount() {
		return menuImgitem.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final int pos = position;
		ImageView imageView;
		if (convertView == null) {
			// imageView = (ImageView) (LayoutInflater.from(mContext).inflate(
			// R.layout.layout_menu_grid_item, null));
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}
		imageView.setImageResource(menuImgitem.get(position));
		imageView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				redirectToActivity(pos);
			}
		});
		return imageView;
	}

	/**
	 * redirect to the specific activity by the given id.
	 * 
	 * @param position
	 */
	private void redirectToActivity(int position) {
		Intent intent = new Intent();
		switch (position) {
		case HOT_PAGE_ID:
			if (myId == HOT_PAGE_ID) {
				((HotPageActivity) mContext).getScrollView().clickSlideButton();
			} else {
				intent.setClass(mContext, HotPageActivity.class);
				mContext.startActivity(intent);
				mContext.overridePendingTransition(R.anim.push_in,
						R.anim.push_out);
				mContext.finish();
			}
			break;
		case ALL_PAGE_ID:
			if (myId == ALL_PAGE_ID) {
				((AllPageActivity) mContext).getScrollView().clickSlideButton();
			} else {
				intent.setClass(mContext, AllPageActivity.class);
				mContext.startActivity(intent);
				mContext.overridePendingTransition(R.anim.push_in,
						R.anim.push_out);
				mContext.finish();
			}
			break;
		case FAVORATE_PAGE_ID:
			if (myId == FAVORATE_PAGE_ID) {
				((MyFavoActivity) mContext).getScrollView().clickSlideButton();
			} else {
				intent.setClass(mContext, MyFavoActivity.class);
				mContext.startActivity(intent);
				mContext.overridePendingTransition(R.anim.push_in,
						R.anim.push_out);
				mContext.finish();
			}
			break;
		case HISTORY_PAGE_ID:
			if (myId == HISTORY_PAGE_ID) {
				((HistoryPageActivity) mContext).getScrollView()
						.clickSlideButton();
			} else {
				intent.setClass(mContext, HistoryPageActivity.class);
				mContext.startActivity(intent);
				mContext.overridePendingTransition(R.anim.push_in,
						R.anim.push_out);
				mContext.finish();
			}
			break;
		case LOCAL_PAGE_ID:
			if (myId == LOCAL_PAGE_ID) {
				((LocalSourceActivity) mContext).getScrollView()
						.clickSlideButton();
			} else {
				intent.setClass(mContext, LocalSourceActivity.class);
				mContext.startActivity(intent);
				mContext.overridePendingTransition(R.anim.push_in,
						R.anim.push_out);
				mContext.finish();
			}
			break;
		case MORE_PAGE_ID:
			if (myId == MORE_PAGE_ID) {
				((MoreActivity) mContext).getScrollView().clickSlideButton();
			} else {
				intent.setClass(mContext, MoreActivity.class);
				mContext.startActivity(intent);
				mContext.overridePendingTransition(R.anim.push_in,
						R.anim.push_out);
				mContext.finish();
			}
		default:
			break;
		}
	}
}
