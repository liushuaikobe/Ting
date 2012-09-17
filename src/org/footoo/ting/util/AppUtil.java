package org.footoo.ting.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.footoo.ting.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AppUtil {
	/**
	 * 显示“关于”对话框
	 * 
	 * @param context
	 */
	public static void showAboutDialog(Context context) {
		Builder builder = new AlertDialog.Builder(context)
				.setTitle(
						context.getResources().getString(
								R.string.about_dialog_title))
				.setIcon(R.drawable.ic_launcher)
				.setMessage(
						context.getResources().getString(
								R.string.about_dialog_string))
				.setPositiveButton(
						context.getResources().getString(
								R.string.about_dialog_btn_text),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
		builder.create().show();
	}

	/**
	 * 判断网络连接是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		} else {
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 获得当前系统时间的标准形式
	 * 
	 * @return
	 */
	public static String getCurrentTimeString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String dateTime = dateFormat.format(new Date());
		return dateTime;
	}

}
