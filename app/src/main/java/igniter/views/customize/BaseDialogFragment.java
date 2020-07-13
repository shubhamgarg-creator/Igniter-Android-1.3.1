package igniter.views.customize;
/**
 * @package com.trioangle.igniter
 * @subpackage view.customize
 * @category BaseDialogFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import igniter.R;

/*****************************************************************
 Custom dialog base fragment
 ****************************************************************/
public class BaseDialogFragment extends DialogFragment {
    protected Activity mActivity;
    private int layoutId;
    private boolean isNeedAnimation = true;

    public BaseDialogFragment() {
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public void setAnimation(boolean isNeedAnimation) {
        this.isNeedAnimation = isNeedAnimation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isNeedAnimation) {
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.share_dialog);
        } else {
            setStyle(DialogFragment.STYLE_NO_TITLE, R.style.progress_dialog);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(layoutId, container, false);
        initViews(v);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = context instanceof Activity ? (Activity) context : null;
    }

    public void initViews(View v) {
        getDialog().setCanceledOnTouchOutside(false);
    }
}
