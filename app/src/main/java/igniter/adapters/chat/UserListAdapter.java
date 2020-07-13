package igniter.adapters.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.chat
 * @category UserListAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.obs.CustomCheckBox;
import com.obs.CustomTextView;

import java.util.ArrayList;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import igniter.R;
import igniter.configs.AppController;
import igniter.configs.SessionManager;
import igniter.datamodels.chat.UserModel;
import igniter.utils.CommonMethods;
import igniter.utils.ImageUtils;

/*****************************************************************
 Adapter for user list (optional)
 ****************************************************************/

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.RecyclerViewHolder> implements View.OnClickListener {

    @Inject
    CommonMethods commonMethods;
    @Inject
    ImageUtils imageUtils;
    @Inject
    SessionManager sessionManager;
    private Context context;
    private ArrayList<UserModel> userList;
    private LayoutInflater inflater;
    private int clickedPosition = -1;

    public UserListAdapter(Context context) {
        this.context = context;
        this.userList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    public UserListAdapter(Context context, ArrayList<UserModel> searchUserList) {
        this.context = context;
        this.userList = searchUserList;
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_chat_user_search_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        UserModel userModel = userList.get(position);
        if (userModel != null) {
            if (commonMethods.isNotEmpty(userModel.getUserName())) {
                holder.tvUsername.setText(userModel.getUserName());
            }

//            if (commonMethods.isNotEmpty(userModel.getImagePath())) {
//                imageUtils.loadCircleImage(context, holder.civUserImage, userModel.getImagePath());
//            } else {
//                holder.civUserImage.setImageResource(R.drawable.defaultbanner);
//            }
//
//            holder.cbSelection.setChecked(userModel.getChecked());

            holder.rltRootLayout.setTag(holder);
//            holder.cbSelection.setTag(holder);
//            holder.cbSelection.setOnClickListener(this);
            holder.rltRootLayout.setOnClickListener(this);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public void onClick(View v) {
        RecyclerViewHolder vHolder = (RecyclerViewHolder) v.getTag();
        clickedPosition = vHolder.getAdapterPosition();

        switch (v.getId()) {
            case R.id.cb_selection:
                UserModel userModel = userList.get(clickedPosition);
                if (userModel != null) {
                    userModel.setChecked(!userModel.getChecked());
                    notifyItemChanged(clickedPosition);
                }
                break;
            case R.id.rlt_user_search_root:
                Intent intent = new Intent();
                ((AppCompatActivity) context).setResult(100, intent);
                ((AppCompatActivity) context).finish();
                break;
            default:
                break;
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView civUserImage;
        private CustomTextView tvUsername;
        private CustomCheckBox cbSelection;
        private RelativeLayout rltRootLayout;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            this.civUserImage = (CircleImageView) itemView.findViewById(R.id.civ_user_image);
            this.tvUsername = (CustomTextView) itemView.findViewById(R.id.tv_user_name);
            this.cbSelection = (CustomCheckBox) itemView.findViewById(R.id.cb_selection);
            this.rltRootLayout = (RelativeLayout) itemView.findViewById(R.id.rlt_user_search_root);
        }
    }
}
