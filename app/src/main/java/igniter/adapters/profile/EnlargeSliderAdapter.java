package igniter.adapters.profile;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.profile
 * @category EnlargeSliderAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.utils.ImageUtils;

/*****************************************************************
 Adapter for Enlarge profile image slider
 ****************************************************************/
public class EnlargeSliderAdapter extends PagerAdapter {
    @Inject
    ImageUtils imageUtils;
    private Context context;
    private ArrayList<String> imageList = new ArrayList<>();

    public EnlargeSliderAdapter(Context context) {
        this.context = context;
        AppController.getAppComponent().inject(this);
    }

    public EnlargeSliderAdapter(Context context, ArrayList<String> imageList) {
        this.context = context;
        this.imageList = imageList;
        AppController.getAppComponent().inject(this);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.enlarge_slider_image, viewGroup, false);

        ImageView ivSliderImage = (ImageView) view.findViewById(R.id.iv_slider_image);

        try {
            //int image_id= Integer.parseInt(imageList.get(position));


            String url = imageList.get(position);

            if (!TextUtils.isEmpty(url)) {
                imageUtils.loadImage(context, ivSliderImage, url);
            }
        } catch (NumberFormatException nf) {

        }
        viewGroup.addView(view, 0);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
