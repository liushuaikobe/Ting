package org.footoo.ting.ui;

import org.footoo.ting.R;
import org.footoo.ting.util.SizeCallBack;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;

public class MainHorizontalScrollView extends HorizontalScrollView {

	// the view be blocked
	private View underView;

	// the width of the under view
	private int underViewWidth;

	private Button slideButton;

	public static boolean underViewIsOut;

	// the width to extend
	public static final int ENLARGE_WIDTH = 50;

	private int currentPos;
	private int scrollToViewPos;

	private float lastMotionX = -1;

	/**
	 * constructors of this class
	 */
	public MainHorizontalScrollView(Context context) {
		super(context);
		initThis();
	}

	public MainHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initThis();
	}

	public MainHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initThis();
	}

	/**
	 * initialize this HorizontalScrollView(HSV)
	 */
	private void initThis() {
		MainHorizontalScrollView.this.setHorizontalFadingEdgeEnabled(false);
		MainHorizontalScrollView.this.setVerticalFadingEdgeEnabled(false);
		MainHorizontalScrollView.this.setVisibility(View.INVISIBLE);
		underViewIsOut = false;
	}

	public void initViews(View[] children, SizeCallBack sizeCallBack,
			View underView) {
		MainHorizontalScrollView.this.underView = underView;
		ViewGroup parent = (ViewGroup) getChildAt(0);

		for (View view : children) {
			view.setVisibility(View.INVISIBLE);
			parent.addView(view);
		}

		OnGlobalLayoutListener onGlobalLayoutListener = new MainOnGlobalLayoutListener(
				parent, children, sizeCallBack);
		getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
	}

	/**
	 * set the reference of the Button,which can trigger the sliding event
	 * 
	 * @param slideButton
	 */
	public void setSlideButton(Button slideButton) {
		this.slideButton = slideButton;
	}

	/**
	 * implement the function when the SlideButton is clicked
	 */
	public void clickSlideButton() {
		if (!underViewIsOut) {
			underViewWidth = 0;
		} else {
			underViewWidth = underView.getMeasuredWidth()
					- slideButton.getMeasuredWidth() - ENLARGE_WIDTH;
		}
		slide();
	}

	/**
	 * slide slowly to let the under view show
	 */
	private void slide() {
		if (underViewWidth == 0) {
			underViewIsOut = true;
		} else {
			underViewIsOut = false;
		}
		MainHorizontalScrollView.this.smoothScrollTo(underViewWidth, 0);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		System.out.println("MainHSV " + " --Intercept");
		return false; // 返回值为false，继续向下分发，把如何处理事件的权力交给子view，让子view知晓touch事件，在子view中判断并处理
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		System.out.println("MainHSV " + " --onScrollChanged");
		super.onScrollChanged(l, t, oldl, oldt);
		if (l < (underView.getMeasuredWidth() - slideButton.getMeasuredWidth() - ENLARGE_WIDTH) / 2) {
			underViewWidth = 0;
		} else {
			underViewWidth = underView.getMeasuredWidth()
					- slideButton.getMeasuredWidth() - ENLARGE_WIDTH;
		}
		currentPos = l;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) { // 子view中判断出这个touch事件不是点子view的，而是滑动HSV的touch事件，于是事件被扔到这处理。
		System.out.println("MainHSV " + " --TouchEvent");
		int x = (int) ev.getRawX();

		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			System.out.println("MainHSV " + " --TouchEvent down");
			lastMotionX = x;
		}
		if ((currentPos == 0 && x < scrollToViewPos)
				|| (currentPos == scrollToViewPos * 2 && x > ENLARGE_WIDTH)) {
			System.out.println("MainHSV " + " --TouchEvent false");
			return false;
		}
		if (underViewIsOut == false && lastMotionX > 20) {
			return true;
		} else {
			if (ev.getAction() == MotionEvent.ACTION_UP) {
				slide();
				return false;
			}
		}
		return super.onTouchEvent(ev);
	}

	public class MainOnGlobalLayoutListener implements OnGlobalLayoutListener {

		private ViewGroup parent;
		private View[] children;
		private SizeCallBack sizeCallBack;

		public MainOnGlobalLayoutListener(ViewGroup parent, View[] children,
				SizeCallBack sizeCallBack) {
			this.parent = parent;
			this.children = children;
			this.sizeCallBack = sizeCallBack;
		}

		@SuppressWarnings("deprecation")
		public void onGlobalLayout() {
			System.out.println("MainHSV " + " --onGlobalLayout");
			MainHorizontalScrollView.this.getViewTreeObserver()
					.removeGlobalOnLayoutListener(this);
			this.sizeCallBack.onGlobalLayout();
			this.parent.removeViewsInLayout(0, children.length);

			int hsvWidth = MainHorizontalScrollView.this.getMeasuredWidth();
			int hsvHeight = MainHorizontalScrollView.this.getMeasuredHeight();

			scrollToViewPos = 0;
			int[] dims = new int[2];
			for (int i = 0; i < children.length; i++) {
				this.sizeCallBack.getViewSize(i, hsvWidth, hsvHeight, dims);
				parent.addView(children[i], dims[0], dims[1]);
				children[i].setVisibility(View.VISIBLE);
				if (i == 0) {
					scrollToViewPos += dims[0];
				}
			}
			// create animation
			new Handler().post(new Runnable() {
				public void run() {
					MainHorizontalScrollView.this.scrollBy(scrollToViewPos, 0);
					MainHorizontalScrollView.this.setVisibility(View.VISIBLE);
					underView.setVisibility(View.VISIBLE);
				}
			});
		}
	}
}
