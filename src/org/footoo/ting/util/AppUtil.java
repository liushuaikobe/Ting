package org.footoo.ting.util;

import org.footoo.ting.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;

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
}
