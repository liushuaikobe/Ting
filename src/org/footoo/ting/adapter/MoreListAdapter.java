package org.footoo.ting.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.footoo.ting.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MoreListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Map<String, String>> itemList; // 搜索、清除缓存、关于

	public MoreListAdapter(Context context) {
		mContext = context;
		initData();
	}

	private void initData() {
		itemList = new ArrayList<Map<String, String>>();

		Map<String, String> itemMap;

		// itemMap = new HashMap<String, String>();
		// itemMap.put(
		// "more_title",
		// mContext.getResources().getString(
		// R.string.more_item_search_text));
		// itemMap.put(
		// "more_desc",
		// mContext.getResources().getString(
		// R.string.more_item_search_desc));
		// itemList.add(itemMap);
		//
		// itemMap = new HashMap<String, String>();
		// itemMap.put(
		// "more_title",
		// mContext.getResources().getString(
		// R.string.more_item_cleanchache_text));
		// itemMap.put("more_desc", "");
		// itemList.add(itemMap);

		itemMap = new HashMap<String, String>();
		itemMap.put("more_title",
				mContext.getResources()
						.getString(R.string.more_item_about_text));
		itemMap.put("more_desc",
				mContext.getResources()
						.getString(R.string.more_item_about_desc));
		itemList.add(itemMap);
	}

	/**
	 * 设置当前缓存的大小
	 */
	public void setCacheSize(String size) {
		itemList.get(1).put("more_desc", size);
	}

	public int getCount() {
		return itemList.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ItemHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.layout_more_item, null);
			holder = new ItemHolder();
			holder.tv_moreTitle = (TextView) convertView
					.findViewById(R.id.more_item_title);
			holder.tv_moreDesc = (TextView) convertView
					.findViewById(R.id.more_item_description);
			convertView.setTag(holder);
		} else {
			holder = (ItemHolder) convertView.getTag();
		}
		holder.tv_moreTitle.setText(itemList.get(position).get("more_title"));
		holder.tv_moreDesc.setText(itemList.get(position).get("more_desc"));
		return convertView;
	}

	private class ItemHolder {
		TextView tv_moreTitle;
		TextView tv_moreDesc;
	}
}
