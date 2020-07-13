package igniter.adapters.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.chat
 * @category ChatConversationListAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.obs.CustomTextView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import igniter.R;
import igniter.configs.AppController;
import igniter.configs.SessionManager;
import igniter.datamodels.chat.MessageModel;
import igniter.datamodels.chat.ReceiveDateModel;
import igniter.utils.CommonMethods;
import igniter.utils.DateTimeUtility;
import igniter.utils.ImageUtils;
import igniter.views.profile.EnlargeProfileActivity;

/*****************************************************************
 Adapter for chat conversation page
 ****************************************************************/


public class ChatConversationListAdapter extends RecyclerView.Adapter<ChatConversationListAdapter.RecyclerViewHolder> implements View.OnClickListener {

    @Inject
    CommonMethods commonMethods;
    @Inject
    ImageUtils imageUtils;
    @Inject
    SessionManager sessionManager;
    @Inject
    DateTimeUtility dateTimeUtility;
    private Context context;
    private ArrayList<MessageModel> messageModels;
    private LayoutInflater inflater;
    private String otherUserName;
    private String msgDate = "", msgDate_old = "";

    public ChatConversationListAdapter(Context context) {
        this.context = context;
        this.messageModels = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(ChatConversationListAdapter.this);
    }

    public ChatConversationListAdapter(Context context, ArrayList<MessageModel> messageModels, String otherUserName) {
        this.context = context;
        this.messageModels = messageModels;
        this.otherUserName = otherUserName;
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(ChatConversationListAdapter.this);
    }

    @Override
    public ChatConversationListAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_chat_conversation_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatConversationListAdapter.RecyclerViewHolder holder, int position) {
        MessageModel messageModel = messageModels.get(position);
        if (messageModel == null) return;

        try {
            holder.ivOthersImage.setVisibility(View.GONE);

            if (sessionManager.getUserId() == 0)
                System.out.println("User id " + sessionManager.getUserId());
            System.out.println("Sender id " + messageModel.getSenderId());
            if (messageModel.getSenderId().equals(sessionManager.getUserId())) {  // Current user
                holder.rltUserChat.setVisibility(View.VISIBLE);
                holder.rltOthersChat.setVisibility(View.GONE);
                ReceiveDateModel receiveDateModel1 = messageModel.getReceivedDate();
                if (!TextUtils.isEmpty(messageModel.getMessage())) {
                    holder.tvUserMsg.setVisibility(View.VISIBLE);
                    holder.tvUserMsg.setText(messageModel.getMessage());
                    if (receiveDateModel1 != null) {
                        if (dateTimeUtility.getChatFormatDate(receiveDateModel1.getDate()).length() == 16) {
                            holder.tv_time_user.setText(dateTimeUtility.getChatFormatDate(receiveDateModel1.getDate()).substring(7, 16));
                        } else {
                            holder.tv_time_user.setText(dateTimeUtility.getChatFormatDate(receiveDateModel1.getDate()).substring(7, 15));
                        }
                    }
                }

                if (!TextUtils.isEmpty(messageModel.getSenderImageUrl())) {
                    holder.ivUserImage.setVisibility(View.GONE);
                    Picasso.with(context).load(messageModel.getSenderImageUrl()).into(holder.ivUserImage);
                    //imageUtils.loadCircleImage(context, holder.ivUserImage, messageModel.getSenderImageUrl());
                } else {
                    holder.ivUserImage.setImageResource(R.color.gray_color);
                }
                if (!TextUtils.isEmpty(messageModel.getLikeStatus()) && messageModel.getLikeStatus().equalsIgnoreCase("Yes")) {
                    holder.tvUserFavourite.setVisibility(View.VISIBLE);
                } else {
                    holder.tvUserFavourite.setVisibility(View.GONE);
                }

                ReceiveDateModel receiveDateModel = messageModel.getReceivedDate();

                if (receiveDateModel != null && !TextUtils.isEmpty(receiveDateModel.getDate())) {
                    holder.tvUserMsgTime.setVisibility(View.GONE);
                    long l = new Date().getTime();
                    holder.tvUserMsgTime.setText(dateTimeUtility.getChatFormatDate(receiveDateModel.getDate()));
                } else {
                    holder.tvUserMsgTime.setVisibility(View.GONE);
                }
            } else {
                holder.rltUserChat.setVisibility(View.GONE);
                holder.rltOthersChat.setVisibility(View.VISIBLE);
                holder.ivOthersImage.setVisibility(View.INVISIBLE);
                ReceiveDateModel receiveDateModel1 = messageModel.getReceivedDate();
                if (!TextUtils.isEmpty(messageModel.getMessage())) {
                    holder.tvOthersMsg.setVisibility(View.VISIBLE);
                    holder.tvOthersMsg.setText(messageModel.getMessage());
                    if (receiveDateModel1 != null) {
                        if (dateTimeUtility.getChatFormatDate(receiveDateModel1.getDate()).length() == 16) {
                            holder.tv_time.setText(dateTimeUtility.getChatFormatDate(receiveDateModel1.getDate()).substring(7, 16));

                        } else {
                            holder.tv_time.setText(dateTimeUtility.getChatFormatDate(receiveDateModel1.getDate()).substring(7, 15));

                        }
                    }
                }

                if (!TextUtils.isEmpty(otherUserName)) {
                    holder.tvOtherUserName.setVisibility(View.GONE);
                    holder.tvOtherUserName.setText(otherUserName);
                }

                if (!TextUtils.isEmpty(messageModel.getLikeStatus()) && messageModel.getLikeStatus().equalsIgnoreCase("Yes")) {
                    holder.tvOtherFavourite.setVisibility(View.VISIBLE);
                } else {
                    holder.tvOtherFavourite.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(messageModel.getSenderImageUrl())) {

                    if (position == 0 || !messageModels.get(position - 1).getSenderId().equals(messageModel.getSenderId()) || position == 0) {
                        holder.ivOthersImage.setVisibility(View.VISIBLE);
                    } else {
                        holder.ivOthersImage.setVisibility(View.GONE);
                    }
                    holder.ivOthersImage.setTag(R.id.civ_other_user_image, position);

                    if (!TextUtils.isEmpty(messageModel.getSenderImageUrl())) {
                        Picasso.with(context).load(messageModel.getSenderImageUrl()).into(holder.ivOthersImage);

                    } else {
                        holder.ivOthersImage.setImageResource(R.color.gray_color);
                    }

                    //imageUtils.loadCircleImage(context, holder.ivOthersImage, messageModel.getSenderImageUrl());
                }
            }
            holder.tvOthersMsgTime.setVisibility(View.GONE);
            ReceiveDateModel receiveDateModel = messageModel.getReceivedDate();
            if (receiveDateModel != null && !TextUtils.isEmpty(receiveDateModel.getDate())) {

                String currentdate = convertCurrentDate(0);
                msgDate = convertDate(receiveDateModel.getDate());

                if (currentdate.equals(msgDate)) {
                    holder.tvOthersMsgTime.setText(context.getString(R.string.today));
                } else if (convertCurrentDate(1).equals(msgDate)) {
                    holder.tvOthersMsgTime.setText(context.getString(R.string.yesterday));
                } else
                    holder.tvOthersMsgTime.setText(msgDate);

                if (messageModel.getIsFirstMessage().equalsIgnoreCase("yes")) {
                    holder.tvOthersMsgTime.setVisibility(View.VISIBLE);
                } else {
                    holder.tvOthersMsgTime.setVisibility(View.GONE);
                }
                /*if(!msgDate_old.equals("")) {
                    if (msgDate.equals(msgDate_old)) {
                        holder.tvOthersMsgTime.setVisibility(View.GONE);
                    } else {
                        //msgDate=receiveDateModel.getDate();
                        holder.tvOthersMsgTime.setVisibility(View.VISIBLE);
                        holder.tvOthersMsgTime.setText(msgDate_old);
                        msgDate_old = msgDate;
                    }
                }else{
                    msgDate_old=msgDate;
                }*/
            } else {
                holder.tvOthersMsgTime.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.ivOthersImage.setTag(holder);
        holder.ivOthersImage.setOnClickListener(this);

    }

    public String convertCurrentDate(int type) {
        String inputPattern = "EEE mmm dd HH:mm:ss z yyyy";
        String outputPattern = "MMMM dd, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
        Date currentdate = new Date();
        Date date = null;
        String str = null;

        //try {
        // date = inputFormat.parse(currentdate);
        if (type == 0) {
            //str = outputFormat.format(date);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 0);
            str = outputFormat.format(cal.getTime());
        } else {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            str = outputFormat.format(cal.getTime());
        }

        /*} catch (ParseException e) {
            e.printStackTrace();
        }*/
        return str;
    }

    public String convertDate(String time) {

        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "MMMM dd, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    public int getItemCount() {
        return messageModels.size();
    }

    @Override
    public void onClick(View v) {
        RecyclerViewHolder holder = (RecyclerViewHolder) v.getTag();
        int clickedPosition = holder.getAdapterPosition();

        MessageModel messageModel = messageModels.get(clickedPosition);
        if (messageModel != null) {
            Intent intent = new Intent(context, EnlargeProfileActivity.class);
            intent.putExtra("navType", 3);
            intent.putExtra("userId", messageModel.getSenderId());
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.ub__fade_in, R.anim.ub__fade_out);
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView tvUserMsgTime, tvUserMsg, tvOthersMsg, tvOthersMsgTime, tvOtherUserName, tv_time, tv_time_user;
        private CustomTextView tvUserFavourite, tvOtherFavourite;
        private CircleImageView ivUserImage, ivOthersImage;
        private RelativeLayout rltUserChat, rltOthersChat, rltChatRoot;
        private LinearLayout lltOtherUserName;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            /*user chat row*/
            this.tvUserMsg = (CustomTextView) itemView.findViewById(R.id.tv_user_message);
            tv_time = (CustomTextView) itemView.findViewById(R.id.tv_time);
            this.tvUserMsgTime = (CustomTextView) itemView.findViewById(R.id.tv_user_msg_time);
            this.tvUserFavourite = (CustomTextView) itemView.findViewById(R.id.tv_user_favorite);
            this.ivUserImage = (CircleImageView) itemView.findViewById(R.id.civ_user_image);
            this.rltUserChat = (RelativeLayout) itemView.findViewById(R.id.rlt_user_chat);

            /*other user chat row*/
            this.tv_time_user = (CustomTextView) itemView.findViewById(R.id.tv_time_user);
            this.tvOthersMsg = (CustomTextView) itemView.findViewById(R.id.tv_other_message);
            this.tvOthersMsgTime = (CustomTextView) itemView.findViewById(R.id.tv_other_msg_time);
            this.tvOtherFavourite = (CustomTextView) itemView.findViewById(R.id.tv_other_favorite);
            this.ivOthersImage = (CircleImageView) itemView.findViewById(R.id.civ_other_user_image);
            this.rltOthersChat = (RelativeLayout) itemView.findViewById(R.id.rlt_other_user_chat);
            this.tvOtherUserName = (CustomTextView) itemView.findViewById(R.id.tv_other_username);
            this.lltOtherUserName = (LinearLayout) itemView.findViewById(R.id.llt_other_username);

            this.rltChatRoot = (RelativeLayout) itemView.findViewById(R.id.rlt_user_search_layout);
        }
    }
}
