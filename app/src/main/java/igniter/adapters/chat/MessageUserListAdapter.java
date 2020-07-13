package igniter.adapters.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.chat
 * @category MessageUserListAdapter
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
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.obs.CustomTextView;

import java.util.ArrayList;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import igniter.R;
import igniter.configs.AppController;
import igniter.datamodels.chat.NewMatchProfileModel;
import igniter.utils.ImageUtils;
import igniter.views.chat.ChatConversationActivity;

/*****************************************************************
 Adapter for Chatted user list
 ****************************************************************/
public class MessageUserListAdapter extends RecyclerView.Adapter<MessageUserListAdapter.RecyclerViewHolder> implements View.OnClickListener {
    @Inject
    ImageUtils imageUtils;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<NewMatchProfileModel> messageList;

    public MessageUserListAdapter(Context context) {
        this.context = context;
        messageList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    public MessageUserListAdapter(Context context, ArrayList<NewMatchProfileModel> messageList) {
        this.context = context;
        this.messageList = messageList;
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.row_message_user_layout, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        NewMatchProfileModel messageModel = messageList.get(position);
        if (messageModel != null) {
            if (!TextUtils.isEmpty(messageModel.getUserName())) {
                holder.tvUserName.setText(messageModel.getUserName());
            }
            if (!TextUtils.isEmpty(messageModel.getLastMessage())) {
                holder.tvLastMsg.setText(messageModel.getLastMessage());
            }
            if (!TextUtils.isEmpty(messageModel.getIsReply()) && messageModel.getIsReply().equalsIgnoreCase("yes")) {
                holder.ivReply.setVisibility(View.VISIBLE);
            } else {
                holder.ivReply.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(messageModel.getReadStatus()) && messageModel.getReadStatus().equalsIgnoreCase("Unread")) {
                holder.civNewMessageAlert.setVisibility(View.VISIBLE);
                holder.rlt_new_msg_alert.setVisibility(View.VISIBLE);
            } else {
                holder.civNewMessageAlert.setVisibility(View.GONE);
                holder.rlt_new_msg_alert.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(messageModel.getLikeStatus()) && messageModel.getLikeStatus().equalsIgnoreCase("super_like")) {
                holder.civSuperLike.setVisibility(View.VISIBLE);
            } else {
                holder.civSuperLike.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(messageModel.getUserImgUrl())) {
                imageUtils.loadCircleImage(context, holder.civUserImg, messageModel.getUserImgUrl());
            } else {
                holder.civUserImg.setImageResource(R.drawable.chat_user_bg);
            }
            holder.rltUserMessage.setTag(holder);
            holder.rltUserMessage.setOnClickListener(this);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    @Override
    public void onClick(View v) {
        RecyclerViewHolder holder = (RecyclerViewHolder) v.getTag();
        int clickedPosition = holder.getAdapterPosition();

        NewMatchProfileModel matchProfileModel = messageList.get(clickedPosition);

        if (matchProfileModel != null) {
            holder.civNewMessageAlert.setVisibility(View.GONE);
            holder.rlt_new_msg_alert.setVisibility(View.GONE);
            matchProfileModel.setReadStatus("Read");
            Intent intent = new Intent(context, ChatConversationActivity.class);
            intent.putExtra("matchId", matchProfileModel.getMatchId());
            intent.putExtra("userId", matchProfileModel.getUserId());
            context.startActivity(intent);
        }
    }

    public void updateList(ArrayList<NewMatchProfileModel> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView civUserImg;
        private ImageView civNewMessageAlert, civSuperLike;
        private CustomTextView tvUserName, tvLastMsg;
        private RelativeLayout rltUserMessage, rlt_new_msg_alert;
        private ImageView ivReply;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            civUserImg = (CircleImageView) itemView.findViewById(R.id.civ_user_image);
            tvUserName = (CustomTextView) itemView.findViewById(R.id.tv_user_name);
            tvLastMsg = (CustomTextView) itemView.findViewById(R.id.tv_last_msg);
            civNewMessageAlert = (ImageView) itemView.findViewById(R.id.civ_new_msg_alert);
            civSuperLike = (ImageView) itemView.findViewById(R.id.civ_super_like);
            rltUserMessage = (RelativeLayout) itemView.findViewById(R.id.rlt_user_message_root);
            rlt_new_msg_alert = (RelativeLayout) itemView.findViewById(R.id.rlt_new_msg_alert);
            ivReply = (ImageView) itemView.findViewById(R.id.iv_reply);
        }
    }
}