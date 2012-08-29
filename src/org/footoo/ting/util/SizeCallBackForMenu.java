package org.footoo.ting.util;

import org.footoo.ting.ui.MainHorizontalScrollView;

import android.widget.Button;

public class SizeCallBackForMenu implements SizeCallBack {

	private Button slideButton;
	private int slideButtonWidth;

	public SizeCallBackForMenu(Button slideButton) {
		super();
		this.slideButton = slideButton;
	}

	public void onGlobalLayout() {
		System.out.println("SizeCallBack " + " --SizeCallBackForMenu");
		this.slideButtonWidth = this.slideButton.getMeasuredWidth() + 50;
	}

	public void getViewSize(int idx, int width, int height, int[] dims) {
		dims[0] = width;
		dims[1] = height;

		if (idx != 1) {
			dims[0] = width - this.slideButtonWidth;
		}
	}

}
