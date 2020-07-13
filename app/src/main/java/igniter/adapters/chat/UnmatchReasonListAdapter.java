package igniter.adapters.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.chat
 * @category UnmatchReasonListAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.obs.CustomTextView;

import java.util.ArrayList;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.configs.SessionManager;
import igniter.datamodels.chat.ReasonModel;
import igniter.utils.CommonMethods;
import igniter.utils.ImageUtils;

/*****************************************************************
 Adapter for Unmatch user reason list
 ****************************************************************/

public class UnmatchReasonListAdapter extends RecyclerView.Adapter<UnmatchReasonListAdapter.RecyclerViewHolder> implements View.OnClickListener {

    private final OnItemClickListener onItemClickListener;
    @Inject
    CommonMethods commonMethods;
    @Inject
    ImageUtils imageUtils;
    @Inject
    SessionManager sessionManager;
    private Context context;
    private ArrayList<ReasonModel> reasonModels;
    private LayoutInflater inflater;
    private int clickedPosition = -1;
    private int previousPosition = 0;

    public UnmatchReasonListAdapter(Context context,OnItemClickListener onItemClickListener) {
        this.context = context;
        this.reasonModels = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    public UnmatchReasonListAdapter(Context context, ArrayList<ReasonModel> reasonModels,OnItemClickListener onItemClickListener) {
        this.context = context;
        this.reasonModels = reasonModels;
        this.onItemClickListener = onItemClickListener;
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_reason_unmatch_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        ReasonModel reasonModel = reasonModels.get(position);
        if (reasonModel != null) {
            holder.tvReason.setText(reasonModel.getReasonMessage());
            if (!TextUtils.isEmpty(reasonModel.getReasonImage())) {
                holder.ivReasonImage.setVisibility(View.VISIBLE);
                imageUtils.loadImage(context, holder.ivReasonImage, reasonModel.getReasonImage());
            } else {
                holder.ivReasonImage.setVisibility(View.GONE);
            }
            if (position == previousPosition) {
                holder.ivReasonSelect.setVisibility(View.GONE);
                sessionManager.setUnMatchReasonId(reasonModel.getReasonId());
                sessionManager.setIsUnMatchReasonReq(reasonModel.getMessageReq());
                //previousPosition=position;
            } else
                holder.ivReasonSelect.setVisibility(View.GONE);

            holder.lltRootLayout.setTag(holder);
            holder.lltRootLayout.setOnClickListener(this);
        }
    }

    @Override
    public int getItemCount() {
        return reasonModels.size();
    }

    public interface OnItemClickListener {

        void onItemClicked(int position,boolean messageReq);

    }

    @Override
    public void onClick(View v) {
        RecyclerViewHolder vHolder = (RecyclerViewHolder) v.getTag();
        clickedPosition = vHolder.getAdapterPosition();

        switch (v.getId()) {
            case R.id.llt_reason_unmatch_root:
                /*Intent intent = new Intent();
                ((AppCompatActivity) context).setResult(100, intent);
                ((AppCompatActivity) context).finish();*/
                vHolder.ivReasonSelect.setVisibility(View.VISIBLE);
                previousPosition = clickedPosition;
                notifyDataSetChanged();
                onItemClickListener.onItemClicked(clickedPosition,reasonModels.get(clickedPosition).getMessageReq());
                break;
            default:
                break;
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivReasonImage;
        private CustomTextView tvReason;
        private ImageView ivReasonSelect;
        private LinearLayout lltRootLayout;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            this.ivReasonImage = (ImageView) itemView.findViewById(R.id.iv_reason_image);
            this.tvReason = (CustomTextView) itemView.findViewById(R.id.tv_reason);
            this.ivReasonSelect = (ImageView) itemView.findViewById(R.id.iv_reason_select);
            this.lltRootLayout = (LinearLayout) itemView.findViewById(R.id.llt_reason_unmatch_root);
        }
    }
}
