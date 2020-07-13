package igniter.adapters.infowindow;

/**
 * @package com.trioangle.igniter
 * @subpackage adapters.infowindow
 * @category InfoWindowModel
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import igniter.R;

/**************************************************************
 Info Window for add location adapter
 ****************************************************************/

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx) {
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.custom_marker, null);

        TextView tv_address = (TextView) view.findViewById(R.id.tv_address);

        InfoWindowModel infoWindowData = (InfoWindowModel) marker.getTag();

        tv_address.setText(infoWindowData.getAddress());

        String address = infoWindowData.getAddress();
        if (infoWindowData.getType() == 0) {
            String sourceString = context.getResources().getString(R.string.gotos) + " ";
            if (address != null && address.length() > 0)
                sourceString = sourceString + "" + address;
            /*if(state!=null&&state.length()>0)
                sourceString=sourceString+""+"\n" + state;*/

            SpannableString str = new SpannableString(sourceString);
            if (address != null && address.length() > 0)
                str.setSpan(new StyleSpan(Typeface.BOLD), 4, address.length() + 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_address.setText(str);
        } else if (infoWindowData.getType() == 1) {
            tv_address.setText(context.getResources().getString(R.string.searching));
        } else if (infoWindowData.getType() == 2)
            tv_address.setText(context.getResources().getString(R.string.not_found));
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
