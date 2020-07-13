/*
 * Copyright (C) 2018
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package igniter.layoutmanager;

import android.graphics.Canvas;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import javax.inject.Inject;

import igniter.configs.AppController;
import igniter.layoutmanager.touchelper.ItemTouchHelper;
import igniter.likedusers.LikedUserAdapter;
import igniter.utils.CommonMethods;


public class SwipeableTouchHelperCallback extends ItemTouchHelper.Callback {
    @Inject
    CommonMethods commonMethods;

    private OnItemSwiped onItemSwiped;
    private int tempValue = 0;

    public SwipeableTouchHelperCallback(OnItemSwiped onItemSwiped) {
        super();
        AppController.getAppComponent().inject(this);
        this.onItemSwiped = onItemSwiped;
    }


    public int getAllowedSwipeDirectionsMovementFlags() {
        return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    }

    public int getAllowedSwipeDirectionsMovementFlags(RecyclerView.ViewHolder viewHolder) {
        return getAllowedSwipeDirectionsMovementFlags();
    }

    public int getAllowedDirectionsMovementFlags(RecyclerView.ViewHolder holder) {
        return getAllowedDirectionsMovementFlags();
    }

    public int getAllowedDirectionsMovementFlags() {
        return ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
    }

    @Override
    public final int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, getAllowedDirectionsMovementFlags(viewHolder));
    }

    public final float getThreshold(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.itemView.getWidth() * 0.9f;
    }

    @Override
    public final boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public final void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int allowedSwipeDirections = getAllowedSwipeDirectionsMovementFlags(viewHolder);
        if (direction == ItemTouchHelper.LEFT && (allowedSwipeDirections & ItemTouchHelper.LEFT) != 0) {
            onItemSwiped.onItemSwipedLeft(viewHolder.getAdapterPosition());
            onItemSwiped.onItemSwiped(viewHolder.getAdapterPosition(), viewHolder);
        } else if (direction == ItemTouchHelper.RIGHT
                && (allowedSwipeDirections & ItemTouchHelper.RIGHT) != 0) {
            onItemSwiped.onItemSwipedRight(viewHolder.getAdapterPosition());
            onItemSwiped.onItemSwiped(viewHolder.getAdapterPosition(), viewHolder);
        }
        viewHolder.itemView.invalidate();
    }

    @Override
    public final float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return 0.5f;
    }

    @Override
    public final long getAnimationDuration(RecyclerView recyclerView, int animationType,
                                           float animateDx, float animateDy) {
        return commonMethods.getAnimationDuration();
    }

    @Override
    public final void onChildDraw(Canvas c, RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
                                  boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        double swipValue = Math.sqrt(dX * dX + dY * dY);
        double fraction = swipValue / getThreshold(viewHolder);
        fraction = Math.min(1, fraction);
        double getSwipeDirection=Math.max(-1, Math.min(1, dX / recyclerView.getMeasuredWidth()));

        if (getSwipeDirection > 0) {
            //Like
            onItemSwiped.onItemSwiping("like", viewHolder.getAdapterPosition());
        } else if (getSwipeDirection < 0) {
            //Nope
            onItemSwiped.onItemSwiping("nope", viewHolder.getAdapterPosition());
        } else if (getSwipeDirection == 0) {
            //Normal
            onItemSwiped.onItemSwiping("none", viewHolder.getAdapterPosition());
        }

        LikedUserAdapter.ProfilesLikedYouViewHolder likedUserViewHolder =(LikedUserAdapter.ProfilesLikedYouViewHolder) viewHolder;

        if(getSwipeDirection > 0 ){
            likedUserViewHolder.getTvLikes().setAlpha(1f);
            likedUserViewHolder.getTvNopes().setAlpha(0f);
        }else if(getSwipeDirection < 0){
            likedUserViewHolder.getTvLikes().setAlpha(0f);
            likedUserViewHolder.getTvNopes().setAlpha(1f);
        }else{
            likedUserViewHolder.getTvLikes().setAlpha(0f);
            likedUserViewHolder.getTvNopes().setAlpha(0f);
        }


        if (viewHolder instanceof OnItemSwipePercentageListener) {
            ((OnItemSwipePercentageListener) viewHolder).onItemSwipePercentage(
                    Math.max(-1, Math.min(1, dX / recyclerView.getMeasuredWidth())));
        }

        int childCount = recyclerView.getChildCount();
        viewHolder.itemView.setRotation(
                commonMethods.getAngle() * (dX / recyclerView.getMeasuredWidth()));
        viewHolder.itemView.setScaleX(1);
        viewHolder.itemView.setScaleY(1);


    /*for (int i = 0; i < childCount; i++) {
      View child = recyclerView.getChildAt(i);
      int level = childCount - i - 1;

      if (level > 0) {
        float scale = Math.max(0, Math.min(1,
            (float) (1 - commonMethods.getScaleGap() * level
                + fraction * commonMethods.getScaleGap())));
        child.setScaleX(scale);

        if (level < commonMethods.getMaxCount() - 1) {
          child.setScaleY(scale);
          child.setTranslationY(Math.max(0, (float) (commonMethods.getTransYGap() * level
                  - fraction * commonMethods.getTransYGap())));
        }
      }

    }*/
    }

    @Override
    public final void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setRotation(0);
        viewHolder.itemView.setScaleX(1);
        viewHolder.itemView.setScaleY(1);
        onItemSwiped.onItemSwiped(viewHolder.getAdapterPosition(),viewHolder);
    }
}
