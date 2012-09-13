package org.footoo.ting.adapter;

import java.util.ArrayList;
import java.util.List;

import org.footoo.ting.R;
import org.footoo.ting.entity.Chapter;
import org.footoo.ting.util.ToastUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChapterListAdapter extends BaseAdapter {

	private Context mContext;
	private List<Chapter> chapterList;

	public ChapterListAdapter(Context context) {
		this.mContext = context;
	}

	public ChapterListAdapter(Context context, ArrayList<Chapter> chapters) {
		this(context);
		this.chapterList = chapters;
	}

	public List<Chapter> getChapterList() {
		return chapterList;
	}

	public void setChapterList(List<Chapter> chapterList) {
		this.chapterList = chapterList;
	}

	public int getCount() {
		return chapterList.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ChptListholder holder = null;
		if (convertView == null) {
			holder = new ChptListholder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.layout_chapter_listitem, null);
			holder.tv_chapterName = (TextView) convertView
					.findViewById(R.id.chapter_name);
			holder.iv_favo = (ImageView) convertView
					.findViewById(R.id.chapter_favorate);
			holder.iv_download = (ImageView) convertView
					.findViewById(R.id.chapter_download);
			convertView.setTag(holder);

		} else {
			holder = (ChptListholder) convertView.getTag();
		}
		holder.tv_chapterName.setText(chapterList.get(position)
				.getChapterName());
		final int pos = position;
		holder.iv_favo.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				ToastUtil.makeShortToast(mContext, pos + " position,favo");
			}
		});
		holder.iv_download.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				ToastUtil.makeShortToast(mContext, pos + " position,download");
			}
		});
		return convertView;
	}

	private class ChptListholder {
		TextView tv_chapterName;
		ImageView iv_download;
		ImageView iv_favo;
	}
}
