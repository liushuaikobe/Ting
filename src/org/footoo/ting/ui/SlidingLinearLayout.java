package org.footoo.ting.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class SlidingLinearLayout extends LinearLayout {

	private float lastMotionX = -1;

	public SlidingLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		System.out.println("SlidingLinearLayout " + " --Intercept");
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			lastMotionX = ev.getRawX();
		}
		if (lastMotionX < 20) { // 就不往下传了，交由自己的onTouchEvent处理（这里自己的onTouchEvent返回值为false，实际把事件上交由上层处理）
			return true;
		} else if (MainHorizontalScrollView.underViewIsOut) { // 同上
			return true;
		} else { // 不是以上两种情况，返回值为false，继续向下分发事件（说明是“真正”的触摸，而不是滑动HSV）
			return super.onInterceptTouchEvent(ev);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		System.out.println("SlidingLinearLayout " + " --TouchEvent");
		return super.onTouchEvent(ev);
	}

}
