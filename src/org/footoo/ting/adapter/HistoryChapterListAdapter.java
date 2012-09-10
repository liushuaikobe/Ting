package org.footoo.ting.adapter;

import java.util.ArrayList;

import org.footoo.ting.R;
import org.footoo.ting.entity.PlayHistoryDbItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HistoryChapterListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<PlayHistoryDbItem> historyList;

	public HistoryChapterListAdapter(Context context) {
		this.mContext = context;
	}

	public ArrayList<PlayHistoryDbItem> getHistoryList() {
		return historyList;
	}

	public void setHistoryList(ArrayList<PlayHistoryDbItem> historyList) {
		this.historyList = historyList;
	}

	public int getCount() {
		return historyList.size();
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
		holder.tv_sourceTitle.setText((String) historyList.get(position)
				.getSource_name());
		holder.tv_lastTimeStamp.setText((String) historyList.get(position)
				.getLast_time());
		holder.tv_chapterName.setText((String) historyList.get(position)
				.getChapter_name());
		return convertView;
	}

	private class HistoryListHolder {
		TextView tv_sourceTitle;
		TextView tv_lastTimeStamp;
		TextView tv_chapterName;

	}
}
