package org.footoo.ting.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.footoo.ting.R;
import org.footoo.ting.util.ToastUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChapterListAdapter extends BaseAdapter {

	private Context mContext;
	private List<String> chapterList;

	public ChapterListAdapter(Context context) {
		this.mContext = context;
		initList();
	}

	/**
	 * just for testing
	 */
	private void initList() {
		chapterList = new ArrayList<String>();
		chapterList.add("第一回 甄士隐梦幻识通灵 贾雨村风尘怀闺秀");
		chapterList.add("第二回 贾夫人仙逝扬州城 冷子兴演说荣国府");
		chapterList.add("第三回 托内兄如海酬训教 接外孙贾母惜孤女");
		chapterList.add("第四回 薄命女偏逢薄命郎 葫芦僧乱判葫芦案");
		chapterList.add("第五回 游幻境指迷十二钗 饮仙醪曲演红楼梦");
		chapterList.add("第六回 贾宝玉初试云雨情 刘姥姥一进荣国府");
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
		holder.tv_chapterName.setText(chapterList.get(position));
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
