package igniter.adapters.matches;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.matches
 * @category MatchesSwipeAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.obs.CustomTextView;

import java.util.ArrayList;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.configs.SessionManager;
import igniter.datamodels.matches.MatchingProfile;
import igniter.utils.ImageUtils;
import igniter.views.profile.EnlargeProfileActivity;

/*****************************************************************
 Adapter for matched swipe user
 ****************************************************************/

public class MatchesSwipeAdapter extends BaseAdapter {

    @Inject
    ImageUtils imageUtils;
    @Inject
    SessionManager sessionManager;
    private ArrayList<MatchingProfile> matchingProfiles;
    private Context context;
    private LayoutInflater inflater;

    public MatchesSwipeAdapter(ArrayList<MatchingProfile> matchingProfilesList, Context context) {
        this.context = context;
        this.matchingProfiles = matchingProfilesList;
        this.inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    @Override
    public int getCount() {
        return matchingProfiles.size();
    }

    @Override
    public Object getItem(int position) {
        return matchingProfiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = inflater.inflate(R.layout.swipe_card_item, parent, false);
        }
        ImageView userImage = (ImageView) v.findViewById(R.id.iv_user_image);
        CustomTextView userNameAge = (CustomTextView) v.findViewById(R.id.tv_user_name_age);
        CustomTextView userDesignation = (CustomTextView) v.findViewById(R.id.tv_designation);
        CustomTextView userProfession = (CustomTextView) v.findViewById(R.id.tv_profession);
        ImageView superlike = (ImageView) v.findViewById(R.id.superlike);
        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.linearLayout2);

        final MatchingProfile currentUser = matchingProfiles.get(position);

        if (currentUser != null) {
            //fill user Image
            imageUtils.loadSliderImage(context, userImage, currentUser.getImages());

            //fill user details
            if (!TextUtils.isEmpty(currentUser.getName()) && currentUser.getAge() > 0) {
                userNameAge.setText(new StringBuilder().append(currentUser.getName()).append(", ").append(currentUser.getAge()).toString());
            } else {
                userNameAge.setText(new StringBuilder().append(currentUser.getName()));
            }

            if (!TextUtils.isEmpty(currentUser.getWork())) {
                userDesignation.setVisibility(View.VISIBLE);
                userDesignation.setText(currentUser.getWork());
            } else {
                userDesignation.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(currentUser.getCollege())) {
                userProfession.setVisibility(View.VISIBLE);
                userProfession.setText(currentUser.getCollege());
            } else {
                userProfession.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(currentUser.getSuperLike()) && currentUser.getSuperLike().equalsIgnoreCase("yes")) {
                superlike.setVisibility(View.VISIBLE);
            } else {
                superlike.setVisibility(View.GONE);
            }
        }

        /*linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EnlargeProfileActivity.class);
                intent.putExtra("navType", 1);
                intent.putExtra("userId",  currentUser.getUserId());
                context.startActivity(intent);
            }
        });*/

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(context, EnlargeProfileActivity.class);
                intent.putExtra("navType", 1);
                intent.putExtra("userId",  currentUser.getUserId());
                context.startActivity(intent);*/

                int x = sessionManager.getTouchX();
                int y = sessionManager.getTouchY();
                if (x <= (v.getRight() / 2) && ((v.getBottom() / 3) * 2.5) >= y) {
                } else if (x > (v.getRight() / 2) && ((v.getBottom() / 3) * 2.2) >= y) {
                } else {
                    Intent intent = new Intent(context, EnlargeProfileActivity.class);
                    intent.putExtra("navType", 1);
                    intent.putExtra("userId", currentUser.getUserId());
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.ub__fade_in, R.anim.ub__fade_out);
                }
            }
        });

        return v;
    }

}
