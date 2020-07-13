package igniter.views.main;
/**
 * @package com.trioangle.igniter
 * @subpackage view.main
 * @category IgniterPlusSlideFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.obs.CustomTextView;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.datamodels.main.ImageListModel;
import igniter.utils.ImageUtils;

/*****************************************************************
 Show all plan slider page
 ****************************************************************/
public class IgniterPlusSliderFragment extends Fragment {

    @Inject
    ImageUtils imageUtils;
    @Inject
    Gson gson;
    private View view;
    private Context context;
    private ImageView ivIgniterPlus;
    private CustomTextView tvPlusTitle, tvPlusSubTitle, tvIgniterPlusTitle;
    private int position = 0;
    private String json, type;
    private ImageListModel imageListModel;

    public static IgniterPlusSliderFragment newInstance(int position, String json, String type) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("json", json);
        args.putString("type", type);
        //args.putString("images", images);
        IgniterPlusSliderFragment fragment = new IgniterPlusSliderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null || !getArguments().containsKey("position") || getActivity() == null)
            return;
        AppController.getAppComponent().inject(this);
        position = getArguments().getInt("position", 0);
        json = getArguments().getString("json");
        type = getArguments().getString("type");
        context = getActivity();
        if (!TextUtils.isEmpty(json)) imageListModel = gson.fromJson(json, ImageListModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.igniter_plus_slider_fragment, container, false);

        ivIgniterPlus = (ImageView) view.findViewById(R.id.iv_igniter_plus);
        tvPlusTitle = (CustomTextView) view.findViewById(R.id.tv_title);
        tvPlusSubTitle = (CustomTextView) view.findViewById(R.id.tv_subtitle);
        tvIgniterPlusTitle = (CustomTextView) view.findViewById(R.id.tv_igniter_plus_title);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (type.equals("gold")) {
            tvIgniterPlusTitle.setTextColor(getResources().getColor(R.color.text_dark));
            tvPlusTitle.setTextColor(getResources().getColor(R.color.text_dark));
            tvPlusSubTitle.setTextColor(getResources().getColor(R.color.text_dark_gray));
        } else {
            tvIgniterPlusTitle.setTextColor(getResources().getColor(R.color.white));
            tvPlusTitle.setTextColor(getResources().getColor(R.color.white));
            tvPlusSubTitle.setTextColor(getResources().getColor(R.color.white));
        }

        if (imageListModel != null) {
            if (!TextUtils.isEmpty(imageListModel.getTitle())) {
                tvIgniterPlusTitle.setText(imageListModel.getTitle());
            }
            if (!TextUtils.isEmpty(imageListModel.getImageUrl())) {
                imageUtils.loadSliderImage(context, ivIgniterPlus, imageListModel.getImageUrl());
            }
            if (!TextUtils.isEmpty(imageListModel.getTitle())) {
                tvPlusTitle.setText(imageListModel.getTitle());
            }
            if (!TextUtils.isEmpty(imageListModel.getDescription())) {
                tvPlusSubTitle.setText(imageListModel.getDescription());
            }
        }

    }
}
