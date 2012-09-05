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

public class HistoryChapterListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Map<String, Object>> historyChapterList;

	public HistoryChapterListAdapter(Context context) {
		this.mContext = context;
		initData();
	}

	/**
	 * just for testing
	 */
	private void initData() {
		historyChapterList = new ArrayList<Map<String, Object>>();
		Map<String, Object> itemMap = new HashMap<String, Object>();

		itemMap.put("source_name", "红楼梦");
		itemMap.put("last_timestamp", "上次播放至：23:45:26");
		itemMap.put("chapter_name", "第十回 金寡妇贪利权受辱 张太医论病细穷源");

		historyChapterList.add(itemMap);
		historyChapterList.add(itemMap);
		historyChapterList.add(itemMap);
		historyChapterList.add(itemMap);
		historyChapterList.add(itemMap);
	}

	public int getCount() {
		return historyChapterList.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		HistoryListHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.layout_history_listitem, null);
			holder = new HistoryListHolder();
			holder.tv_sourceTitle = (TextView) convertView
					.findViewById(R.id.history_title);
			holder.tv_lastTimeStamp = (TextView) convertView
					.findViewById(R.id.last_timestamp);
			holder.tv_chapterName = (TextView) convertView
					.findViewById(R.id.history_chapter_name);
			convertView.setTag(holder);
		} else {
			holder = (HistoryListHolder) convertView.getTag();
		}
		holder.tv_sourceTitle.setText((String) historyChapterList.get(position)
				.get("source_name"));
		holder.tv_lastTimeStamp.setText((String) historyChapterList.get(
				position).get("last_timestamp"));
		holder.tv_chapterName.setText((String) historyChapterList.get(position)
				.get("chapter_name"));
		return convertView;
	}

	private class HistoryListHolder {
		TextView tv_sourceTitle;
		TextView tv_lastTimeStamp;
		TextView tv_chapterName;

	}
}
