package igniter.adapters.signup;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.signup
 * @category CountryCodeListAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.obs.CustomTextView;

import igniter.R;

/*****************************************************************
 Adapter for country code while signup (Optional)
 ****************************************************************/
public class CountryCodeListAdapter extends BaseAdapter {
    LayoutInflater inflater;
    private Context context;
    private String[] countryNames, countryCode, countryFlag;

    public CountryCodeListAdapter(Context context) {
        this.context = context;
        inflater = (LayoutInflater.from(context));
        this.countryFlag = context.getResources().getStringArray(R.array.slide_icon);
        this.countryCode = context.getResources().getStringArray(R.array.country_code);
        this.countryNames = context.getResources().getStringArray(R.array.country_name);
    }

    @Override
    public int getCount() {
        return countryNames.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.row_country_code_spinner, null);
            holder.tvCountryFlag = (CustomTextView) view.findViewById(R.id.tv_country_flag);
            holder.tvCountryName = (CustomTextView) view.findViewById(R.id.tv_country_name);
            holder.tvCountryCode = (CustomTextView) view.findViewById(R.id.tv_country_code);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvCountryFlag.setText(countryFlag[position]);
        holder.tvCountryName.setText(countryNames[position]);
        holder.tvCountryCode.setText(countryCode[position]);
        holder.tvCountryFlag.setVisibility(View.GONE);

        return view;
    }

    private class ViewHolder {
        CustomTextView tvCountryFlag, tvCountryName, tvCountryCode;
    }
}