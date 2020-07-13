package igniter.adapters.profile;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.profile
 * @category IgniterSliderAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.content.res.TypedArray;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.obs.CustomTextView;

import javax.inject.Inject;

import igniter.R;
import igniter.configs.AppController;
import igniter.utils.ImageUtils;

/*****************************************************************
 Adapter for plan slider list
 ****************************************************************/
public class IgniterSliderAdapter extends PagerAdapter {
    @Inject
    ImageUtils imageUtils;
    private Context context;

    public IgniterSliderAdapter(Context context) {
        this.context = context;
        AppController.getAppComponent().inject(this);
    }

    @Override
    public int getCount() {
        String[] description = context.getResources().getStringArray(R.array.slide_description);
        return description.length;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup viewGroup, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.igniter_slider_item, viewGroup, false);

        ImageView tvSlideIcon = (ImageView) view.findViewById(R.id.tv_slide_icon);
        CustomTextView tvSlideTitle = (CustomTextView) view.findViewById(R.id.tv_slide_title);
        CustomTextView tvSlideDescription = (CustomTextView) view.findViewById(R.id.tv_slide_description);

        String[] titles = context.getResources().getStringArray(R.array.slide_title);

        String[] description = context.getResources().getStringArray(R.array.slide_description);

        final TypedArray icons = context.getResources().obtainTypedArray(R.array.slide_icon);
        tvSlideIcon.setImageResource(icons.getResourceId(position, -1));
        tvSlideTitle.setText(titles[position]);
        tvSlideDescription.setText(description[position]);

        /*switch (position) {
            case 0:
                tvSlideIcon.setTextColor(ContextCompat.getColor(context, R.color.btn_yellow));
                break;
            case 1:
                tvSlideIcon.setTextColor(ContextCompat.getColor(context, R.color.btn_violet));
                break;
            case 2:
                tvSlideIcon.setTextColor(ContextCompat.getColor(context, R.color.btn_blue));
                break;
            case 3:
                tvSlideIcon.setTextColor(ContextCompat.getColor(context, R.color.login_fb_bg));
                break;
            case 4:
                tvSlideIcon.setTextColor(ContextCompat.getColor(context, R.color.brown));
                break;
            case 5:
                tvSlideIcon.setTextColor(ContextCompat.getColor(context, R.color.btn_yellow));
                break;
            case 6:
                tvSlideIcon.setTextColor(ContextCompat.getColor(context, R.color.btn_green));
                break;
            default:
                break;
        }*/

        viewGroup.addView(view, 0);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
