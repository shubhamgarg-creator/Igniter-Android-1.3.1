package com.obs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;

/**
 * Share functionality across multiple view subclasses using composition.
 */
class FontCustomTextViewHelper {

    FontCustomTextViewHelper() {

    }

    /**
     * For identify class in Log
     */
    private static final String TAG = "ViewHelper";
    private static final HashMap<String, Typeface> TYPEFACE_CACHE = new HashMap<String, Typeface>();

    /**
     * Corresponds to the constructor of TextView and children
     */
    public static void initialize(TextView view, Context context, AttributeSet attributeSet) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.FontCustom, R.attr.typeface, 0);

        try {
            String typefaceName = attributes.getString(R.styleable.FontCustom_typeface);

            if (!TextUtils.isEmpty(typefaceName)) {
                if (TYPEFACE_CACHE.containsKey(typefaceName)) {
                    Typeface typeface = TYPEFACE_CACHE.get(typefaceName);
                    view.setTypeface(typeface);
                } else {
                    Typeface typeface = Typeface.createFromAsset(context.getAssets(), typefaceName);

                    if (typeface != null) {
                        TYPEFACE_CACHE.put(typefaceName, typeface);
                        view.setTypeface(typeface);
                    } else {
                        Log.w(TAG, "View had typeface attribute but no typeface was found in /assets");
                    }
                }
                // Note: This flag is required for proper typeface rendering
                view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            }
        } finally {
            attributes.recycle();
        }
    }
}
