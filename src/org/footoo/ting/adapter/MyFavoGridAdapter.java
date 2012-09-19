package org.footoo.ting.adapter;

import java.util.ArrayList;

import org.footoo.ting.R;
import org.footoo.ting.entity.MyFavoDbItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyFavoGridAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<MyFavoDbItem> myFavoList;

	public MyFavoGridAdapter(Context context) {
		this.mContext = context;
	}

	public MyFavoGridAdapter(Context context,
			ArrayList<MyFavoDbItem> myFavoDbItemList) {
		this(context);
		this.myFavoList = myFavoDbItemList;
	}

	public ArrayList<MyFavoDbItem> getMyFavoList() {
		return myFavoList;
	}

	public void setMyFavoList(ArrayList<MyFavoDbItem> myFavoList) {
		this.myFavoList = myFavoList;
	}

	public void removeOneMyFavo(int position) {
		this.myFavoList.remove(position);
	}

	public int getCount() {
		return myFavoList.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		MyFavoListHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.layout_history_listitem, null);
			holder = new MyFavoListHolder();
			holder.tv_sourceTitle = (TextView) convertView
					.findViewById(R.id.history_title);
			holder.tv_lastTimeStamp = (TextView) convertView
					.findViewById(R.id.last_timestamp);
			holder.tv_chapterName = (TextView) convertView
					.findViewById(R.id.history_chapter_name);
			convertView.setTag(holder);
		} else {
			holder = (MyFavoListHolder) convertView.getTag();
		}
		holder.tv_sourceTitle
				.setText(myFavoList.get(position).getSource_name());
		holder.tv_lastTimeStamp
				.setText(myFavoList.get(position).getLast_time());
		holder.tv_chapterName.setText(myFavoList.get(position)
				.getChapter_name());
		return convertView;
	}

	private class MyFavoListHolder {
		TextView tv_sourceTitle;
		TextView tv_lastTimeStamp;
		TextView tv_chapterName;
	}
}
