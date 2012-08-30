package org.footoo.ting.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	/**
	 * make long toast
	 * 
	 * @param context
	 * @param text
	 */
	public static void makeLongToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	/**
	 * make short toast
	 * 
	 * @param context
	 * @param text
	 */
	public static void makeShortToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
