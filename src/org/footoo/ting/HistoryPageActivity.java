package org.footoo.ting;

import java.util.ArrayList;

import org.footoo.ting.adapter.HistoryChapterListAdapter;
import org.footoo.ting.adapter.MenuGridAdapter;
import org.footoo.ting.entity.PlayHistoryDbItem;
import org.footoo.ting.storage.DBManager;
import org.footoo.ting.ui.MainHorizontalScrollView;
import org.footoo.ting.util.SizeCallBackForMenu;
import org.footoo.ting.util.ToastUtil;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class HistoryPageActivity extends MainBaseActivity {

	private ViewSwitcher historySwitcher;
	private ListView historyListView;

	private HistoryChapterListAdapter historyChapterListAdapter;

	private DBManager manager;
	private ArrayList<PlayHistoryDbItem> historyList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initData();
		initViews();

		registerForContextMenu(historyListView);

		// 测试向数据库中插入数据
		for (int i = 0; i < 10; i++) {
			PlayHistoryDbItem item = new PlayHistoryDbItem();
			item.setChapter_name("第十一回 庆寿辰宁府排家宴 见熙凤贾瑞起淫心");
			item.setChapter_url("http://172.16.0.46/mp3/hongloumeng/11.mp3");
			item.setLast_time("2012-09-09 15:03");
			item.setSource_name("红楼梦");
			manager.insertOneHistory(item);
		}

		new GetPlayHistoryTask().execute("begin");
	}

	private void initData() {
		manager = new DBManager(HistoryPageActivity.this);
		historyChapterListAdapter = new HistoryChapterListAdapter(
				HistoryPageActivity.this);
	}

	private void initViews() {
		scrollView = (MainHorizontalScrollView) findViewById(R.id.mScrollView);

		contentPage = LayoutInflater.from(this).inflate(
				R.layout.layout_history_page, null);

		historySwitcher = (ViewSwitcher) contentPage
				.findViewById(R.id.viewswicher_history);
		historyListView = (ListView) contentPage
				.findViewById(R.id.history_item_list);
		historyListView.setCacheColorHint(Color.argb(0, 0, 0, 0));
		historyListView
				.setOnItemClickListener(new HistoryListItemClickListener());
		historyListView
				.setOnItemLongClickListener(new HistoryListItemLongClickListener());

		// 为菜单设置adapter
		underView.setAdapter(new MenuGridAdapter(HistoryPageActivity.this,
				MenuGridAdapter.HISTORY_PAGE_ID));

		topBarTitleTv = (TextView) contentPage.findViewById(R.id.topbar_title);
		setTopBarTitle(R.string.playhistory_page_title);

		slideBtn = (Button) contentPage.findViewById(R.id.slideBtn);
		slideBtn.setOnClickListener(new HistoryPageSlideBtnClickListener());

		View leftView = new View(this);
		leftView.setBackgroundColor(Color.TRANSPARENT);
		final View[] children = new View[] { leftView, contentPage };
		scrollView.initViews(children, new SizeCallBackForMenu(slideBtn),
				(View) findViewById(R.id.menu_layout));
		scrollView.setSlideButton(slideBtn);
	}

	private class GetPlayHistoryTask extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			historySwitcher.showNext();
		}

		@Override
		protected String doInBackground(String... params) {
			historyList = manager.getAllPlayHistory();
			historyChapterListAdapter.setHistoryList(historyList);
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			historyListView.setAdapter(historyChapterListAdapter);
			historySwitcher.setDisplayedChild(0);
		}
	}

	private class ClearHistoryTask extends AsyncTask<String, Integer, Boolean> {
		ProgressDialog dlg;

		@Override
		protected void onPreExecute() {
			dlg = new ProgressDialog(HistoryPageActivity.this);
			dlg.setTitle("清空历史");
			dlg.setMessage("正在清空，请稍后...");
			dlg.setCancelable(false);
			dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dlg.show();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				manager.clearHistory();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			dlg.dismiss();
			if (result == true) {
				historyChapterListAdapter
						.setHistoryList(new ArrayList<PlayHistoryDbItem>());
				historyChapterListAdapter.notifyDataSetChanged();
				ToastUtil.makeShortToast(HistoryPageActivity.this, "清空完毕");
			} else {
				ToastUtil
						.makeShortToast(HistoryPageActivity.this, "清空失败，请稍后再试");
			}
		}
	}

	private class HistoryPageSlideBtnClickListener implements
			View.OnClickListener {
		public void onClick(View v) {
			scrollView.clickSlideButton();
		}
	}

	private class HistoryListItemClickListener implements
			AdapterView.OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO 在这里播放选中的历史资源
		}
	}

	private class HistoryListItemLongClickListener implements
			AdapterView.OnItemLongClickListener {
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			ToastUtil.makeShortToast(HistoryPageActivity.this, "" + position);
			return true;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		SubMenu delMenu = menu.addSubMenu(getResources().getString(
				R.string.del_menu_text));
		delMenu.add(0, 123, 0,
				getResources().getString(R.string.del_submenu_text));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem mi) {
		switch (mi.getItemId()) {
		case 123:
			new ClearHistoryTask().execute("begin");
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(getResources().getString(R.string.context_menu_play));
		menu.add(getResources().getString(R.string.context_menu_del));
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		return super.onContextItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		manager.closeDB();
		super.onDestroy();
	}
}
