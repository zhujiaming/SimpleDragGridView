package com.zjm.mydragexpandgrid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.zjm.mydragexpandgrid.bean.DragData;

import java.util.List;

/**
 * Created by zhujiaming on 16/3/21.
 */
public class CustomContainerView extends ViewGroup {

    private CustomHoleGridView mGridView;
    private OnCustomItemClickListener onCustomItemClickListener;

    public CustomContainerView(Context context) {
        super(context);
        init();
    }

    public CustomContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mGridView = new CustomHoleGridView(getContext());
        addView(mGridView, params);
        mGridView.setOnCustomItemClick(new CustomHoleGridView.OnCustomItemClick() {
            @Override
            public void onCustomItemClick(int position) {
                if (onCustomItemClickListener != null)
                    onCustomItemClickListener.onAboveItemClick(position);
            }

            @Override
            public void onCustomChildItemClick(int parentPosition, int childPosition) {
                if (onCustomItemClickListener != null)
                    onCustomItemClickListener.onBottomItemClick(parentPosition, childPosition);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMeasure = 0;
        int heightMeasure = 0;
        mGridView.measure(widthMeasureSpec, heightMeasureSpec);
        widthMeasure = mGridView.getMeasuredWidth();
        heightMeasure = mGridView.getMeasuredHeight();
        setMeasuredDimension(widthMeasure, heightMeasure);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int measuedHeight = mGridView.getMeasuredHeight();
        mGridView.layout(l, 0, r, measuedHeight + t);
    }

    public void setDatas(List<DragData> datas) {
        mGridView.setDatas(datas);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setOnCustomItemClickListener(OnCustomItemClickListener onCustomItemClickListener) {
        this.onCustomItemClickListener = onCustomItemClickListener;
    }

    public interface OnCustomItemClickListener {
        void onAboveItemClick(int position);
        void onBottomItemClick(int abPosition, int position);
    }
}
