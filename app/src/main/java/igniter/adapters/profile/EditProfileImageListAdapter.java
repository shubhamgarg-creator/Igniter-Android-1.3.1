package igniter.adapters.profile;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.profile
 * @category EditProfileImageListAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.obs.CustomTextView;

import java.util.ArrayList;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.utils.ImageUtils;

/*****************************************************************
 Adapter for Edit profile image list adapter
 ****************************************************************/
public class EditProfileImageListAdapter extends RecyclerView.Adapter<EditProfileImageListAdapter.RecyclerViewHolder> implements View.OnClickListener {
    @Inject
    ImageUtils imageUtils;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> imageList;
    private int size = 6;

    public EditProfileImageListAdapter(Context context) {
        this.context = context;
        imageList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    public EditProfileImageListAdapter(Context context, ArrayList<String> imageList) {
        this.context = context;
        this.imageList = imageList;
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.row_edit_profile_list, parent, false);
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
//        String imageUrl = imageList.get(position);
//        if (!TextUtils.isEmpty(imageUrl)) {
        holder.tvCount.setText("" + position);
        if (position == 1) {
            holder.tvCloseIcon.setVisibility(View.VISIBLE);
            holder.tvAddIcon.setVisibility(View.GONE);
            imageUtils.loadImageCurve(context, holder.ivUserImage, "http://i.imgur.com/rFLNqWI.jpg", 0);
        } else {
            holder.tvCloseIcon.setVisibility(View.GONE);
            holder.tvAddIcon.setVisibility(View.VISIBLE);
        }
//        }
    }

    @Override
    public int getItemCount() {
        return size;
    }

    @Override
    public void onClick(View v) {
        RecyclerViewHolder holder = (RecyclerViewHolder) v.getTag();
        int clickedPosition = holder.getAdapterPosition();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivUserImage;
        private CustomTextView tvCount, tvAddIcon, tvCloseIcon;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            ivUserImage = (ImageView) itemView.findViewById(R.id.iv_user_image);
            tvCount = (CustomTextView) itemView.findViewById(R.id.tv_count);
            tvAddIcon = (CustomTextView) itemView.findViewById(R.id.tv_add_icon);
            tvCloseIcon = (CustomTextView) itemView.findViewById(R.id.tv_close_icon);
        }
    }
}
