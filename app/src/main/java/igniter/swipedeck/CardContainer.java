package igniter.swipedeck;
/**
 * @package com.trioangle.igniter
 * @subpackage swipedeck.Utility
 * @category CardContainer
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.view.View;

import igniter.swipedeck.Utility.SwipeCallback;
import igniter.swipedeck.Utility.SwipeListener;

/*****************************************************************
 CardContainer
 ****************************************************************/
public class CardContainer {

    int positionWithinViewGroup = -1;
    int positionWithinAdapter = -1;
    private View view;
    private SwipeListener swipeListener;
    private SwipeCallback callback;
    private igniter.swipedeck.SwipeDeck parent;
    private long id;
    private int swipeDuration = igniter.swipedeck.SwipeDeck.ANIMATION_DURATION;

    public CardContainer(View view, SwipeDeck parent, SwipeCallback callback) {
        this.view = view;
        this.parent = parent;
        this.callback = callback;

        setupSwipeListener();
    }

    public int getPositionWithinViewGroup() {
        return positionWithinViewGroup;
    }

    public void setPositionWithinViewGroup(int pos) {
        this.positionWithinViewGroup = pos;
    }

    public View getCard() {
        return this.view;
    }

    public void cleanupAndRemoveView() {
        //wait for card to render off screen, do cleanup and remove from viewgroup
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                deleteViewFromSwipeDeck();
            }
        }, swipeDuration);
    }

    private void deleteViewFromSwipeDeck() {
        parent.removeView(view);
        parent.removeFromBuffer(this);
    }

    public void setSwipeEnabled(boolean enabled) {
        //also checks in case user doesn't want to be able to swipe the card freely
        if (enabled && parent.SWIPE_ENABLED) {
            view.setOnTouchListener(swipeListener);
        } else {
            view.setOnTouchListener(null);
        }
    }

    public void setLeftImageResource(int leftImageResource) {
        View left = view.findViewById(leftImageResource);
        left.setAlpha(0);
        swipeListener.setLeftView(left);
    }

    public void setRightImageResource(int rightImageResource) {
        View right = view.findViewById(rightImageResource);
        right.setAlpha(0);
        swipeListener.setRightView(right);
    }

    public void setUpImageResource(int upImageResource) {
        View up = view.findViewById(upImageResource);
        up.setAlpha(0);
        swipeListener.setUpView(up);
    }

    public void setBottomImageResource(int bottomImageResource) {
        View bottom = view.findViewById(bottomImageResource);
        bottom.setAlpha(0);
        swipeListener.setBottomView(bottom);
    }

    public void setClickLeftImageResource(int leftImageResource) {
        View left = view.findViewById(leftImageResource);
        left.setAlpha(1);
        swipeListener.setLeftView(left);
    }

    public void setClickRightImageResource(int rightImageResource) {
        View right = view.findViewById(rightImageResource);
        right.setAlpha(1);
        swipeListener.setRightView(right);
    }

    public void setClickUpImageResource(int upImageResource) {
        View up = view.findViewById(upImageResource);
        up.setAlpha(1);
        swipeListener.setUpView(up);
    }

    public void setClickBottomImageResource(int bottomImageResource) {
        View bottom = view.findViewById(bottomImageResource);
        bottom.setAlpha(1);
        swipeListener.setBottomView(bottom);
    }

    public void setupSwipeListener() {
        this.swipeListener = new SwipeListener(
                view,
                callback,
                parent.getPaddingLeft(),
                parent.getPaddingTop(),
                parent.ROTATION_DEGREES,
                parent.OPACITY_END,
                parent
        );
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPositionWithinAdapter() {
        return positionWithinAdapter;
    }

    public void setPositionWithinAdapter(int position) {
        this.positionWithinAdapter = position;
    }

    public void swipeCardLeft(int duration) {
        // Remember how long card would be animating
        swipeDuration = duration;
        // Disable touch events
        setSwipeEnabled(false);
        swipeListener.swipeCardLeft(duration);
    }

    public void swipeCardRight(int duration) {
        swipeDuration = duration;
        setSwipeEnabled(false);
        swipeListener.swipeCardRight(duration);
    }

    public void swipeCardTop(int duration) {
        swipeDuration = duration;
        setSwipeEnabled(false);
        swipeListener.swipeCardTop(duration);
    }
}
