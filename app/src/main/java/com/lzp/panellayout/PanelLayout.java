package com.lzp.panellayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by lzp on 2018/2/4.
 */

public class PanelLayout extends ViewGroup implements View.OnClickListener {

    public interface PanelIconClickListener{
        void onPanelIconClick(int panelId);
    }

    private SparseArray<PanelInfo> panelInfos;
    private View mCurPanelView;
    private int mCurPanelId;
    private SparseArray<View> mPanelViews;
    private int mPanelHeight;

    public PanelLayout(Context context) {
        this(context, null);
    }

    public PanelLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PanelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusable(true);
        setFocusableInTouchMode(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PanelLayout, defStyleAttr, 0);
        int defaultPanelHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
        mPanelHeight = a.getDimensionPixelSize(R.styleable.PanelLayout_panelHeight, defaultPanelHeight);
    }

    public void addPanel(PanelInfo panelInfo) {
        if (panelInfo == null) {
            return;
        }
        if (panelInfos == null) {
            panelInfos = new SparseArray<>();
        }
        int pos = panelInfos.size();
        panelInfos.append(pos, panelInfo);
        addPanleIcon();
    }

    public void addPanel(SparseArray<PanelInfo> panelInfos) {
        if (panelInfos == null || panelInfos.size() <= 0)
            return;

        if (this.panelInfos == null) {
            this.panelInfos = new SparseArray<>();
        }

        int len = panelInfos.size();
        int pos = this.panelInfos.size();
        for (int i = 0; i < len; i++) {
            this.panelInfos.append(pos + i, panelInfos.get(i));
        }
        addPanleIcon();
    }

    private void addPanleIcon() {
        int len = panelInfos.size();
        for (int i = 0; i < len; i++) {
            ImageView image = new ImageView(getContext());
            image.setTag(panelInfos.get(i).panelId);
            image.setOnClickListener(this);
            image.setImageResource(panelInfos.get(i).iconResid);
            image.setScaleType(ImageView.ScaleType.CENTER);
            LayoutParams layoutParams = image.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }
            image.setLayoutParams(layoutParams);

            addViewInLayout(image, i, layoutParams, true);
        }
        requestLayout();
    }

    @Override
    public void onClick(View v) {
        int panelId = (int) v.getTag();
        int curPanelId = mCurPanelId;

        if (mCurPanelView != null) {//panel显示中
            hidePanel();
        }
        if (panelId != curPanelId) {
            showPanel(panelId);
        }
    }

    public void hidePanel() {
        removeView(mCurPanelView);
        mCurPanelView = null;
        mCurPanelId = -1;
    }

    private void showPanel(int panelId) {
        mCurPanelId = panelId;
        PanelInfo info = findPanelByPanelId(panelId);
        if (mPanelViews == null) {
            mPanelViews = new SparseArray<>();
        }
        mCurPanelView = mPanelViews.get(panelId);
        if (mCurPanelView == null) {
            mCurPanelView = LayoutInflater.from(getContext()).inflate(info.layoutId, null);
        }
        addPanelView();
    }

    private void addPanelView() {
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 200);
        addViewInLayout(mCurPanelView, getChildCount(), lp, true);
        requestLayout();
    }

    private PanelInfo findPanelByPanelId(int panelId) {
        if (panelInfos == null) return null;
        int len = panelInfos.size();
        for (int i = 0; i < len; i++) {
            if (panelInfos.get(i).panelId == panelId)
                return panelInfos.get(i);
        }
        return null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int iconCounts = mCurPanelView == null ? getChildCount() : getChildCount() - 1;
        int parentMeasureWidht = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int parentMeasureHeight = parentMeasureWidht / iconCounts;
        if (mCurPanelView != null) {
            parentMeasureHeight += mPanelHeight;
        }
        setMeasuredDimension(parentMeasureWidht, parentMeasureHeight);

        int iconMeasureHeight = parentMeasureWidht / iconCounts;
        int iconMeasureWidht = parentMeasureWidht / iconCounts;
        for (int i = 0; i < iconCounts; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() != View.GONE) {
                int childMeasureWidth = MeasureSpec.makeMeasureSpec(iconMeasureWidht, MeasureSpec.EXACTLY);
                int childMeasureHeight = MeasureSpec.makeMeasureSpec(iconMeasureHeight, MeasureSpec.EXACTLY);
                view.measure(childMeasureWidth, childMeasureHeight);
            }
        }
        if (mCurPanelView != null && mCurPanelView.getVisibility() != GONE) {
            int childMeasureHeight = MeasureSpec.makeMeasureSpec(mPanelHeight, MeasureSpec.EXACTLY);
            mCurPanelView.measure(widthMeasureSpec, childMeasureHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 0) {
            return;
        }

        int iconCount = mCurPanelView == null ? getChildCount() : getChildCount() - 1;
        int size = getMeasuredWidth() / iconCount;
        int left = 0;

        for (int i = 0; i < iconCount; i++) {
            View view = getChildAt(i);
            if (view.getVisibility() != View.GONE) {
                view.layout(left, 0, left + size, size);
                left += size;
            }
        }
        if (mCurPanelView != null && mCurPanelView.getVisibility() != GONE) {
            mCurPanelView.layout(0, 0 + size, r, b);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCurPanelView != null) {
                hidePanel();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void addView(View child) {
        throw new UnsupportedOperationException("unsupport call addView method,please call addPanel instead");
    }

    @Override
    public void addView(View child, int index) {
        throw new UnsupportedOperationException("unsupport call addView method,please call addPanel instead");
    }

    @Override
    public void addView(View child, int width, int height) {
        throw new UnsupportedOperationException("unsupport call addView method,please call addPanel instead");
    }

    @Override
    public void addView(View child, LayoutParams params) {
        throw new UnsupportedOperationException("unsupport call addView method,please call addPanel instead");
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        throw new UnsupportedOperationException("unsupport call addView method,please call addPanel instead");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mPanelViews != null) {
            mPanelViews.clear();
            mPanelViews = null;
        }
        hidePanel();
    }

    public static class PanelInfo {
        public final int iconResid;
        public final String des;
        public final int panelId;
        public final int layoutId;

        PanelInfo(int panelId, int iconId, int layoutId, String des) {
            this.panelId = panelId;
            this.iconResid = iconId;
            this.des = des;
            this.layoutId = layoutId;
        }
    }
}
