package igniter.views.main;
/**
 * @package com.trioangle.igniter
 * @subpackage view.main
 * @category IgniterGoldSlideFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.obs.CustomTextView;

import javax.inject.Inject;

import igniter.R;
import igniter.datamodels.main.ImageListModel;
import igniter.utils.ImageUtils;

/*****************************************************************
 Show Gold play slide page (Now its dynamic in IgniterPlusDialogActivity)
 ****************************************************************/
public class IgniterGoldSliderFragment extends Fragment {

    @Inject
    ImageUtils imageUtils;
    @Inject
    Gson gson;
    private View view;
    private Context context;
    private ImageView ivIgniterPlus;
    private RelativeLayout backcircle;
    private CustomTextView tvPlusTitle, tvPlusSubTitle;
    private int position = 0;
    private String json;
    private ImageListModel imageListModel;

    public static IgniterGoldSliderFragment newInstance(int position, String json) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("json", json);
        IgniterGoldSliderFragment fragment = new IgniterGoldSliderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null || !getArguments().containsKey("position") || getActivity() == null)
            return;
        position = getArguments().getInt("position", 0);
        json = getArguments().getString("json");
        context = getActivity();
        if (!TextUtils.isEmpty(json)) imageListModel = gson.fromJson(json, ImageListModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_igniter_gold_slider, container, false);

        ivIgniterPlus = (ImageView) view.findViewById(R.id.iv_igniter_plus);
        tvPlusTitle = (CustomTextView) view.findViewById(R.id.tv_title);
        backcircle = (RelativeLayout) view.findViewById(R.id.backcircle);
        tvPlusSubTitle = (CustomTextView) view.findViewById(R.id.tv_subtitle);

        return view;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*if (imageListModel != null) {
            if (!TextUtils.isEmpty(imageListModel.getTitle())) {
                //tvPlusTitle.setText(imageListModel.getTitle());
            }
            if (!TextUtils.isEmpty(imageListModel.getImageUrl())) {
                imageUtils.loadSliderImage(context, ivIgniterPlus, imageListModel.getImageUrl());
            }
        }*/


        final TypedArray icons = getResources().obtainTypedArray(R.array.igniter_gold_slide_icon);
        if (position == 0) {
            android.view.ViewGroup.LayoutParams layoutParams = ivIgniterPlus.getLayoutParams();
            layoutParams.width = 350;
            layoutParams.height = 350;
            ivIgniterPlus.setLayoutParams(layoutParams);


            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(350, 350);
            params.gravity = Gravity.CENTER;
            backcircle.setLayoutParams(params);

            ivIgniterPlus.setImageResource(icons.getResourceId(position, -1));
        } else {
            backcircle.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_white));
            ivIgniterPlus.setImageResource(icons.getResourceId(position, -1));
        }

        String[] titles = context.getResources().getStringArray(R.array.igniter_gold_slide_title);
        tvPlusTitle.setText(titles[position]);

        String[] subTitles = context.getResources().getStringArray(R.array.igniter_gold_slide_subtitle);
        tvPlusSubTitle.setText(subTitles[position]);
    }
}
