package org.footoo.ting.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.footoo.ting.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SourcesListAdapter extends BaseAdapter {

	private Context mContext;
	// 现在暂时在Adapter里面直接把数据源给了，以后要从Activity里面传过来
	private List<Map<String, Object>> listItem;

	public SourcesListAdapter(Context context) {
		this.mContext = context;
		initItem();
	}

	/**
	 * just be used for testing
	 */
	private void initItem() {
		listItem = new ArrayList<Map<String, Object>>();
		Map<String, Object> item;
		for (int i = 0; i < 10; i++) {
			item = new HashMap<String, Object>();
			item.put("source_name", "红楼梦");
			item.put(
					"source_desc",
					"本书是一部具有高度思想性和高度艺术性的伟大作品，作者具有初步的民主主义思想，他对现实社会、宫廷、官场的黑暗，封建贵族阶级及其家族的腐朽，对封建的科举、婚姻、奴婢、等级制度及社会统治思想等都进行了深刻的批判，并且提出了朦胧的带有初步民主主义性质的理想和主张。");
			listItem.add(item);
		}
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
		SourceItemHolder holder = null;
		if (convertView == null) {
			holder = new SourceItemHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.layout_sources_listitem, null);
			holder.cover_iv = (ImageView) convertView
					.findViewById(R.id.source_cover);
			holder.name_tv = (TextView) convertView
					.findViewById(R.id.source_name);
			holder.desc_tv = (TextView) convertView
					.findViewById(R.id.source_desc);
			convertView.setTag(holder);
		} else {
			holder = (SourceItemHolder) convertView.getTag();
		}
		holder.cover_iv.setImageResource(R.drawable.sample_source_cover);
		holder.name_tv.setText((String) (listItem.get(position)
				.get("source_name")));
		holder.desc_tv.setText((String) listItem.get(position).get(
				"source_desc"));
		return convertView;
	}

	private class SourceItemHolder {
		ImageView cover_iv;
		TextView name_tv;
		TextView desc_tv;
	}

}
