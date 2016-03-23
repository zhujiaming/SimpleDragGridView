package com.zjm.mydragexpandgrid.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zjm.mydragexpandgrid.R;
import com.zjm.mydragexpandgrid.bean.DragChildData;
import com.zjm.mydragexpandgrid.utils.CommUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujiaming on 16/3/22.
 */
public class CustomChildGridView extends LinearLayout {

    private String TAG = "CustomChildGridView";
    private List<DragChildData> mDatas = new ArrayList<>();
    private static int COLUM_COUNT = 4;
    private int mRows;
    private int HORIZONTAL_SPACE = 1;
    private static final int VERTICAL_SPACE = 1;
    private int height = 0;
    private OnChildItemClick onChildItemClick;

    public CustomChildGridView(Context context) {
        super(context);
        init();
    }

    public CustomChildGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomChildGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setBackgroundResource(R.color.color_777572);
        refreshUI();
    }

    public List<DragChildData> getDatas() {
        return mDatas;
    }

    public void setDatas(List<DragChildData> datas) {
        this.mDatas = datas;
    }

    public void refreshUI() {
        removeAllViews();
        height = 0;
        int size = mDatas.size();
        if (mDatas.size() == 0)
            return;
        mRows = size % COLUM_COUNT == 0 ? size / COLUM_COUNT : size / COLUM_COUNT + 1;
        boolean isFull = mRows * COLUM_COUNT == size;
        LayoutParams horizontalItemParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        horizontalItemParam.weight = 1;
        LayoutParams rawParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams horizontalSpaceParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, VERTICAL_SPACE);
        LayoutParams verticalSpaceParam = new LayoutParams(HORIZONTAL_SPACE, ViewGroup.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < mRows; i++) {
            LinearLayout llContainerRow = new LinearLayout(getContext());
            int currentColumCount = COLUM_COUNT;
            if (i == mRows - 1) {
                if (!isFull)
                    currentColumCount = size - ((mRows - 1) * COLUM_COUNT);
            }
            for (int j = 0; j < COLUM_COUNT; j++) {
                if (j < currentColumCount) {
                    final int position = i * COLUM_COUNT + j;
                    View itemView = View.inflate(getContext(), R.layout.item_gv_child, null);
                    itemView.setId(position);
                    TextView tv = (TextView) itemView.findViewById(R.id.tv_child_item);
                    tv.setText(mDatas.get(position).name);
                    llContainerRow.addView(itemView, horizontalItemParam);
                    itemView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onChildItemClick != null)
                                onChildItemClick.onChildItemClick(position);
                        }
                    });
                } else {
                    View emptyview = new View(getContext());
                    emptyview.setBackgroundResource(android.R.color.transparent);
                    llContainerRow.addView(emptyview, horizontalItemParam);
                }
                View view = new View(getContext());
                view.setBackgroundResource(R.color.color_333333);
                llContainerRow.addView(view, verticalSpaceParam);
            }
            View view = new View(getContext());
            view.setBackgroundResource(R.color.color_333333);
            addView(llContainerRow, rawParam);
            addView(view, horizontalSpaceParam);
            int measuerSpce = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            llContainerRow.measure(measuerSpce, measuerSpce);
            height = height + llContainerRow.getMeasuredHeight() + HORIZONTAL_SPACE;
        }
    }


    public void notifyDataSetChanged(List<DragChildData> datas, OnChildItemClick onChildItemClick) {
        if (datas == null)
            setDatas(new ArrayList<DragChildData>());
        else
            setDatas(datas);
        this.onChildItemClick = onChildItemClick;
        refreshUI();
    }

    public int getTotalHeight() {
        return height;
    }

    public interface OnChildItemClick {
        void onChildItemClick(int childPosition);
    }
}
