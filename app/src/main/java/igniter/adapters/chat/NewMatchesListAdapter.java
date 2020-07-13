package igniter.adapters.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.chat
 * @category NewMatchesListAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.obs.CustomTextView;

import java.util.ArrayList;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import igniter.R;
import igniter.configs.AppController;
import igniter.configs.SessionManager;
import igniter.datamodels.chat.NewMatchProfileModel;
import igniter.utils.ImageUtils;
import igniter.views.chat.ChatConversationActivity;
import igniter.views.main.IgniterPlusDialogActivity;

/*****************************************************************
 Adapter for Not chatted used List
 ****************************************************************/

public class NewMatchesListAdapter extends RecyclerView.Adapter<NewMatchesListAdapter.RecyclerViewHolder> implements View.OnClickListener {
    @Inject
    ImageUtils imageUtils;
    @Inject
    SessionManager sessionManager;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<NewMatchProfileModel> matchesList;
    private int width;
    private int likedCount;

    public NewMatchesListAdapter(Context context) {
        this.context = context;
        matchesList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    public NewMatchesListAdapter(Context context, ArrayList<NewMatchProfileModel> matchesList, int width,int likedCount) {
        this.context = context;
        this.matchesList = matchesList;
        this.width = width;
        this.likedCount = likedCount;
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.row_new_matches_list, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        NewMatchProfileModel matchProfileModel = matchesList.get(position);
        if (matchProfileModel != null) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.width = width;
            params.height = width;
            holder.lltNewMatches.setLayoutParams(params);


            if (!TextUtils.isEmpty(matchProfileModel.getUserName())) {
                holder.tvNewMatcherName.setVisibility(View.VISIBLE);
                holder.tvNewMatcherName.setText(matchProfileModel.getUserName());
            } else {
                holder.tvNewMatcherName.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(matchProfileModel.getReadStatus()) && matchProfileModel.getReadStatus().equalsIgnoreCase("Unread")) {
                holder.civNewMessageAlert.setVisibility(View.VISIBLE);
                holder.rlt_new_msg_alert.setVisibility(View.VISIBLE);
            } else {
                holder.civNewMessageAlert.setVisibility(View.GONE);
                holder.rlt_new_msg_alert.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(matchProfileModel.getLikeStatus()) && matchProfileModel.getLikeStatus().equalsIgnoreCase("super_like")) {
                holder.civSuperLike.setVisibility(View.VISIBLE);
            } else {
                holder.civSuperLike.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(matchProfileModel.getUserImgUrl())) {
                imageUtils.loadCircleImage(context, holder.civNewMatchUserImg, matchProfileModel.getUserImgUrl());
            } else {
                holder.civNewMatchUserImg.setImageResource(R.drawable.chat_user_bg);
            }
            if (matchProfileModel.getUserId() == 0) {
                if (!TextUtils.isEmpty(matchProfileModel.getLastMessage())) {

                    holder.tvLikeCount.setText(matchProfileModel.getLastMessage() + context.getString(R.string.plus));


                    holder.tvLikeCount.setVisibility(View.VISIBLE);
                    holder.civNewMatchUserImg.setImageResource(R.drawable.igniter_gold10);
                } else {
                    holder.tvLikeCount.setVisibility(View.GONE);
                }
            } else {
                holder.tvLikeCount.setVisibility(View.GONE);
            }


            holder.lltNewMatches.setTag(holder);
            holder.lltNewMatches.setOnClickListener(this);

        }
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }

    @Override
    public void onClick(View v) {
        RecyclerViewHolder holder = (RecyclerViewHolder) v.getTag();
        int clickedPosition = holder.getAdapterPosition();

        NewMatchProfileModel matchProfileModel = matchesList.get(clickedPosition);


        if (matchProfileModel.getUserId() == 0) {
            if (sessionManager.getIsOrder() && sessionManager.getPlanType().equalsIgnoreCase("Gold")) {
            } else {
                Intent intent = new Intent(context, IgniterPlusDialogActivity.class);
                intent.putExtra("startwith", "");
                intent.putExtra("type", "gold");
                context.startActivity(intent);
            }
        } else {
            holder.civNewMessageAlert.setVisibility(View.GONE);
            holder.rlt_new_msg_alert.setVisibility(View.GONE);
            matchProfileModel.setReadStatus("Read");
            Intent intent = new Intent(context, ChatConversationActivity.class);
            intent.putExtra("matchId", matchProfileModel.getMatchId());
            intent.putExtra("userId", matchProfileModel.getUserId());
            context.startActivity(intent);
        }


    }

    public void updateList(ArrayList<NewMatchProfileModel> matchesList) {
        this.matchesList = matchesList;
        notifyDataSetChanged();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rlt_new_msg_alert;
        private CircleImageView civNewMatchUserImg;
        private ImageView civNewMessageAlert, civSuperLike;
        private RelativeLayout lltNewMatches;
        private CustomTextView tvNewMatcherName, tvLikeCount;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            civNewMatchUserImg = (CircleImageView) itemView.findViewById(R.id.civ_match_user_image);
            tvNewMatcherName = (CustomTextView) itemView.findViewById(R.id.tv_user_name);
            civNewMessageAlert = (ImageView) itemView.findViewById(R.id.civ_new_msg_alert);
            tvLikeCount = (CustomTextView) itemView.findViewById(R.id.tv_like_count);
            civSuperLike = (ImageView) itemView.findViewById(R.id.civ_super_like);
            lltNewMatches = (RelativeLayout) itemView.findViewById(R.id.llt_new_matches);
            rlt_new_msg_alert = (RelativeLayout) itemView.findViewById(R.id.rlt_new_msg_alert);
        }
    }
}
