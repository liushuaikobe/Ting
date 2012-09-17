package org.footoo.ting.adapter;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.footoo.ting.R;
import org.footoo.ting.entity.Category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategoryListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Category> categories;

	public CategoryListAdapter(Context context) {
		this.mContext = context;
	}

	public CategoryListAdapter(Context context, ArrayList<Category> categories) {
		this(context);
		this.categories = categories;
	}

	public ArrayList<Category> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList<Category> categories) {
		this.categories = categories;
	}

	public int getCount() {
		return categories.size();
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
				.setText(categories.get(position).getCategoryName());
		return convertView;
	}

}
