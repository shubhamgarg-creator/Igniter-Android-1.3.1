package igniter.adapters.profile;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.profile
 * @category LocationListAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.obs.CustomTextView;

import java.util.ArrayList;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.configs.SessionManager;
import igniter.datamodels.main.JsonResponse;
import igniter.datamodels.main.LocationModel;
import igniter.interfaces.ApiService;
import igniter.interfaces.ServiceListener;
import igniter.utils.CommonMethods;
import igniter.utils.DateTimeUtility;
import igniter.utils.ImageUtils;
import igniter.utils.RequestCallback;
import igniter.views.customize.CustomDialog;

import static igniter.utils.Enums.REQ_UPDATE_DEFAULT_LOCATION;

/*****************************************************************
 Adapter for additional location in setting and Igniter plus plan
 ****************************************************************/
public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.RecyclerViewHolder> implements ServiceListener, View.OnClickListener {

    public static int lastCheckedPosition = -1;
    @Inject
    CommonMethods commonMethods;
    @Inject
    ImageUtils imageUtils;
    @Inject
    DateTimeUtility dateTimeUtility;
    @Inject
    ApiService apiService;
    @Inject
    CustomDialog customDialog;
    @Inject
    SessionManager sessionManager;
    @Inject
    Gson gson;
    private Context context;
    private ArrayList<LocationModel> locationModels;
    private LayoutInflater inflater;
    private AlertDialog dialog;

    public LocationListAdapter(Context context) {
        this.context = context;
        this.locationModels = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(LocationListAdapter.this);
    }

    public LocationListAdapter(Context context, ArrayList<LocationModel> locationModels) {
        this.context = context;
        this.locationModels = locationModels;
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(LocationListAdapter.this);
        dialog = commonMethods.getAlertDialog(context);
    }

    @Override
    public LocationListAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_location_list, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LocationListAdapter.RecyclerViewHolder holder, int position) {
        final LocationModel locationModel = locationModels.get(position);
        if (locationModel == null) return;


        if (!TextUtils.isEmpty(locationModel.getAddress()))
            holder.tvAddress.setText(locationModel.getAddress());

        if (!TextUtils.isEmpty(locationModel.getAddress1()))
            holder.tvAddress1.setText(locationModel.getAddress1());

        if (locationModel.getIsDefault() != null && locationModel.getIsDefault().equals("Active")) {  // Check selected location
            lastCheckedPosition = position;
            holder.ivTick.setVisibility(View.VISIBLE);
            if (position > 0) {
                holder.ivImage.setColorFilter(ContextCompat.getColor(context, R.color.blue2), android.graphics.PorterDuff.Mode.MULTIPLY);
                holder.ivImage.setImageDrawable(context.getResources().getDrawable(R.drawable.settings_passport_other_location));
            } else {
                holder.ivImage.setColorFilter(ContextCompat.getColor(context, R.color.blue2), android.graphics.PorterDuff.Mode.MULTIPLY);
                holder.ivImage.setImageDrawable(context.getResources().getDrawable(R.drawable.settings_passport_current_location));
            }
        } else {
            holder.ivTick.setVisibility(View.GONE);
            if (position > 0) {
                holder.ivImage.setColorFilter(ContextCompat.getColor(context, R.color.text_light), android.graphics.PorterDuff.Mode.MULTIPLY);
                holder.ivImage.setImageDrawable(context.getResources().getDrawable(R.drawable.settings_passport_other_location));
            } else {
                holder.ivImage.setColorFilter(ContextCompat.getColor(context, R.color.text_light), android.graphics.PorterDuff.Mode.MULTIPLY);
                holder.ivImage.setImageDrawable(context.getResources().getDrawable(R.drawable.settings_passport_current_location));
            }
        }

        holder.rltLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                locationModels.get(lastCheckedPosition).setIsDefault(null);
                locationModels.get(holder.getAdapterPosition()).setIsDefault("Active");
                lastCheckedPosition = holder.getAdapterPosition();
                changeDefaultLocation(locationModel.getId());
                notifyDataSetChanged();

                //holder.ivImage.setColorFilter(ContextCompat.getColor(context, R.color.blue), android.graphics.PorterDuff.Mode.MULTIPLY);
                //holder.ivTick.setVisibility(View.VISIBLE);
                // notifyAll();
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationModels.size();
    }

    @Override
    public void onClick(View v) {
    }

    private void changeDefaultLocation(int id) {
        sessionManager.setSettingUpdate(true);
        commonMethods.showProgressDialog((AppCompatActivity) context, customDialog);
        apiService.changeDefaultLocation(sessionManager.getToken(), id).enqueue(new RequestCallback(REQ_UPDATE_DEFAULT_LOCATION, this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(context, dialog, data);
            return;
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(context, dialog, data);
            return;
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView tvAddress, tvAddress1;
        private ImageView ivImage, ivTick;
        private RelativeLayout rltLocation;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            /*user location row*/
            this.tvAddress = (CustomTextView) itemView.findViewById(R.id.tv_address);
            this.tvAddress1 = (CustomTextView) itemView.findViewById(R.id.tv_address1);
            this.ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            this.ivTick = (ImageView) itemView.findViewById(R.id.iv_tick);
            this.rltLocation = (RelativeLayout) itemView.findViewById(R.id.rlt_location);
        }
    }
}
