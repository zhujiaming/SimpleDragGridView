package com.zjm.mydragexpandgrid.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zjm.mydragexpandgrid.R;
import com.zjm.mydragexpandgrid.bean.DragData;
import com.zjm.mydragexpandgrid.utils.CommUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhujiaming on 16/3/21.
 */
public class CustomHoleGridView extends LinearLayout {

    private List<DragData> mDatas = new ArrayList<>();
    private int mRows = 0;
    private static int COLUM_COUNT = 3;
    public static int HORIZONTAL_SPACE = 1;
    public static int VERTICAL_SPACE = 1;

    private StateRecord perState = new StateRecord();
    private OnCustomItemClick onCustomItemClick;
    private int startX;
    private int mFlagIvWidth;

    public CustomHoleGridView(Context context) {
        super(context);
        init();
    }

    public CustomHoleGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomHoleGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        setOrientation(VERTICAL);
        refreshUI();
    }

    private void refreshUI() {
        removeAllViews();
        int size = mDatas.size();
        mRows = size % COLUM_COUNT == 0 ? size / COLUM_COUNT : size / COLUM_COUNT + 1;
        boolean isFull = mRows * COLUM_COUNT == size;
        LayoutParams horizontalItemParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        horizontalItemParam.weight = 1;
        LayoutParams rawParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams horizontalSpaceParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, VERTICAL_SPACE);
        LayoutParams verticalSpaceParam = new LayoutParams(HORIZONTAL_SPACE, ViewGroup.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < mRows; i++) {
            View holeItemView = View.inflate(getContext(), R.layout.item_hole_row, null);
            LinearLayout llContainerRow = (LinearLayout) holeItemView.findViewById(R.id.ll_row_container);
            CustomChildGridView gvRowChild = (CustomChildGridView) holeItemView.findViewById(R.id.gv_child_gv);
            ImageView ivFlag = (ImageView) holeItemView.findViewById(R.id.iv_arrow_flag);
            int measureSpce = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            ivFlag.measure(measureSpce, measureSpce);
            mFlagIvWidth = ivFlag.getMeasuredWidth();
            gvRowChild.setTag(i + "");
            int currentColumCount = COLUM_COUNT;
            if (i == mRows - 1)
                if (!isFull)
                    currentColumCount = size - ((mRows - 1) * COLUM_COUNT);
            for (int j = 0; j < COLUM_COUNT; j++) {
                if (j < currentColumCount) {
                    int position = i * COLUM_COUNT + j;
                    View itemView = View.inflate(getContext(), R.layout.item_gv_parent, null);
                    itemView.setId(position);
                    TextView tv = (TextView) itemView.findViewById(R.id.tv_item_p);
                    tv.setText(mDatas.get(position).name);
                    llContainerRow.addView(itemView, horizontalItemParam);
                    itemView.setOnClickListener(new onRawItemClickListener(gvRowChild, ivFlag, position));
                } else {
                    View emptyview = new View(getContext());
                    emptyview.setBackgroundResource(android.R.color.transparent);
                    llContainerRow.addView(emptyview, horizontalItemParam);
                }
                View view = new View(getContext());
                view.setBackgroundResource(R.color.gap_line);
                llContainerRow.addView(view, verticalSpaceParam);
            }
            View view = new View(getContext());
            view.setBackgroundResource(R.color.gap_line);
            addView(holeItemView, rawParam);
            addView(view, horizontalSpaceParam);
            setPerState(false, null, -1);//初始化
        }
    }

    public void setDatas(List<DragData> datas) {
        this.mDatas = datas;
        refreshUI();
    }

    public List<DragData> getDatas() {
        return mDatas;
    }


    public void setOnCustomItemClick(OnCustomItemClick onCustomItemClick) {
        this.onCustomItemClick = onCustomItemClick;
    }

    class onRawItemClickListener implements OnClickListener, CustomChildGridView.OnChildItemClick {

        private int position;
        private DragData dragData;
        private ImageView ivFlag;
        private CustomChildGridView childGridView;


        public onRawItemClickListener(CustomChildGridView childGridView, ImageView ivFlag, int position) {
            this.position = position;
            this.ivFlag = ivFlag;
            this.childGridView = childGridView;
            this.dragData = getDatas().get(position);
        }

        @Override
        public void onClick(View v) {
            if (dragData.childDatas == null || dragData.childDatas.size() == 0) {
                if (perState.isExpand) {
                    animateCollaping(perState.cTagView);
                    perState.isExpand = false;
                    perState.ivFlag.clearAnimation();
                    perState.ivFlag.setVisibility(View.GONE);
                }
                if (onCustomItemClick != null)
                    onCustomItemClick.onCustomItemClick(position);
                return;
            }
            if (perState.cTagView == null) {
                setPerState(false, childGridView, ivFlag, position);
            }
            boolean isExpand = false;
            String tag = (String) childGridView.getTag();
            if (TextUtils.equals(tag, perState.getCTag())) {
                if (position == perState.position) {
                    if (perState.isExpand) {
                        animateCollaping(childGridView);
                        isExpand = false;
                        ivFlag.clearAnimation();
                        ivFlag.setVisibility(View.GONE);
                    } else {
                        childGridView.notifyDataSetChanged(dragData.childDatas, this);
                        animateExpanding(childGridView);
                        isExpand = true;
                        startX = v.getLeft() + v.getWidth() / 2 - mFlagIvWidth / 2;
                        xAxismoveAnim(ivFlag, startX, startX);
                    }
                    setPerState(isExpand, childGridView, ivFlag, position);
                    return;
                } else {
                    childGridView.notifyDataSetChanged(dragData.childDatas, this);
                    int endx = v.getLeft() + v.getWidth() / 2 - mFlagIvWidth / 2;
                    if (!perState.isExpand) {
                        animateExpanding(childGridView);
                        xAxismoveAnim(ivFlag, endx, endx);
                    } else {
                        int lastheight = perState.cTagView.getHeight();
                        int currentHeight = childGridView.getHeight();
                        createHeightAnimator(childGridView, perState.cTagView.getHeight(), childGridView.getTotalHeight()).start();
                        xAxismoveAnim(ivFlag, startX, endx);
                        startX = endx;
                    }
                    setPerState(true, childGridView, ivFlag, position);
                }
            } else {
                childGridView.notifyDataSetChanged(dragData.childDatas, this);
                animateCollaping(perState.cTagView);
                animateExpanding(childGridView);
                startX = v.getLeft() + v.getWidth() / 2 - mFlagIvWidth / 2;
                perState.ivFlag.clearAnimation();
                perState.ivFlag.setVisibility(View.GONE);
                xAxismoveAnim(ivFlag, startX, startX);
                setPerState(true, childGridView, ivFlag, position);
            }
        }

        @Override
        public void onChildItemClick(int childPosition) {
            if (onCustomItemClick != null)
                onCustomItemClick.onCustomChildItemClick(position, childPosition);
        }
    }


    public interface OnCustomItemClick {
        void onCustomItemClick(int position);

        void onCustomChildItemClick(int parentPosition, int childPosition);
    }

    public void animateExpanding(View view) {
        view.setVisibility(View.VISIBLE);
        int widthSpec = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int HeightSpec = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(widthSpec, HeightSpec);
        createHeightAnimator(view, 0, view.getMeasuredHeight()).start();
    }


    public ValueAnimator createHeightAnimator(final View view, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    public void animateCollaping(final View view) {
        int origHeight = view.getHeight();
        ValueAnimator animator = createHeightAnimator(view, origHeight, 0);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                view.clearAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    public void xAxismoveAnim(View v, int startX, int toX) {
        v.setVisibility(View.VISIBLE);
        moveAnim(v, startX, toX, 0, 0, 200);
    }

    private void moveAnim(View v, int startX, int toX, int startY, int toY, long during) {
        TranslateAnimation anim = new TranslateAnimation(startX, toX, startY, toY);
        anim.setDuration(during);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }


    class StateRecord {
        public int position = -1;
        public boolean isExpand = false;
        public CustomChildGridView cTagView;
        public ImageView ivFlag;
        public int startX;

        public String getCTag() {
            return (String) cTagView.getTag();
        }
    }

    private synchronized void setPerState(boolean isExpand, CustomChildGridView tagview, int position) {
        perState.isExpand = isExpand;
        perState.cTagView = tagview;
        perState.position = position;
        if (tagview == null)
            return;
    }

    private synchronized void setPerState(boolean isExpand, CustomChildGridView tagview, ImageView ivFlag, int position) {
        setPerState(isExpand, tagview, position);
        perState.ivFlag = ivFlag;
    }
}
