package igniter.adapters.main;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.main
 * @category ProfileSliderAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.utils.ImageUtils;

/*****************************************************************
 Adapter for chat enlarge profile slider
 ****************************************************************/

public class ProfileSliderAdapter extends PagerAdapter {
    @Inject
    ImageUtils imageUtils;
    private Context context;
    private List<String> images;
    private LayoutInflater inflater;

    public ProfileSliderAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.slider_item, view, false);
        ImageView ivSliderImage = (ImageView) myImageLayout
                .findViewById(R.id.iv_slider_image);
        imageUtils.loadSliderImage(context, ivSliderImage, images.get(position));
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
