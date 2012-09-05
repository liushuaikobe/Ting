package org.footoo.ting.adapter;

import java.util.ArrayList;

import org.footoo.ting.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategoryListAdapter extends BaseAdapter {

	private Context mContext;
	// 现在暂时在Adapter里面直接把数据源给了，以后要从Activity里面传过来
	private ArrayList<String> listItem;

	public CategoryListAdapter(Context context) {
		this.mContext = context;
		initItem();
	}

	/**
	 * just be used for testing
	 */
	private void initItem() {
		listItem = new ArrayList<String>();
		listItem.add("都市言情");
		listItem.add("卖萌无罪");
		listItem.add("古典文学");
		listItem.add("百家讲坛");
	}

	public int getCount() {
		return listItem.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
		if (convertView == null) {
			v = LayoutInflater.from(mContext).inflate(
					R.layout.layout_category_listitem, null);
			convertView = v;
		}
		((TextView) convertView.findViewById(R.id.category_name))
				.setText(listItem.get(position));
		return convertView;
	}

}
