package igniter.views.main;
/**
 * @package com.trioangle.igniter
 * @subpackage view.main
 * @category TutorialFragment
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
 Signin and signup home page slide fragment
 ****************************************************************/
public class TutorialFragment extends Fragment {

    @Inject
    ImageUtils imageUtils;
    @Inject
    Gson gson;
    private View view;
    private Context context;
    private ImageView ivTutorialImg;
    private CustomTextView tvTutorialTitle;
    private int position = 0;
    private String json;
    private ImageListModel imageListModel;

    public static TutorialFragment newInstance(int position, String json) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("json", json);
        TutorialFragment fragment = new TutorialFragment();
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
        context = getActivity();
        if (!TextUtils.isEmpty(json)) imageListModel = gson.fromJson(json, ImageListModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.tutorial_fragment, container, false);

        ivTutorialImg = (ImageView) view.findViewById(R.id.iv_tutorial_img);
        tvTutorialTitle = (CustomTextView) view.findViewById(R.id.tv_tutorial_title);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (imageListModel != null) {
            if (!TextUtils.isEmpty(imageListModel.getTitle())) {
                tvTutorialTitle.setText(imageListModel.getTitle());
            }
            if (!TextUtils.isEmpty(imageListModel.getImageUrl())) {
                imageUtils.loadSliderImage(context, ivTutorialImg, imageListModel.getImageUrl());
            }
        }
    }
}
