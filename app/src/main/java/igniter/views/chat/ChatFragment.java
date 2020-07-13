package igniter.views.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage view.chat
 * @category ChatFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.obs.CustomEditText;
import com.obs.CustomTextView;

import java.util.ArrayList;

import javax.inject.Inject;

import igniter.R;
import igniter.adapters.chat.MessageUserListAdapter;
import igniter.adapters.chat.NewMatchesListAdapter;
import igniter.configs.AppController;
import igniter.configs.SessionManager;
import igniter.datamodels.chat.MatchedProfileModel;
import igniter.datamodels.chat.NewMatchProfileModel;
import igniter.datamodels.main.JsonResponse;
import igniter.datamodels.main.MatchProfilesModel;
import igniter.interfaces.ActivityListener;
import igniter.interfaces.ApiService;
import igniter.interfaces.ServiceListener;
import igniter.likedusers.LikedUsersActivity;
import igniter.utils.CommonMethods;
import igniter.utils.RequestCallback;
import igniter.utils.SpacesItemDecoration;
import igniter.views.customize.CustomDialog;
import igniter.views.customize.CustomLayoutManager;
import igniter.views.customize.CustomRecyclerView;
import igniter.views.main.HomeActivity;
import igniter.views.main.IgniterPlusDialogActivity;

/*****************************************************************
 User chat list page (Home pages)
 ****************************************************************/
public class ChatFragment extends Fragment implements ServiceListener {
    public int conv_no;
    @Inject
    ApiService apiService;
    @Inject
    CommonMethods commonMethods;
    @Inject
    CustomDialog customDialog;
    @Inject
    SessionManager sessionManager;
    @Inject
    Gson gson;
    private View view;
    private ActivityListener listener;
    private Resources res;
    private HomeActivity mActivity;
    private CustomTextView tvNewMessageCount, tvNewMatchTitle, tvMessageTitle;
    private CustomRecyclerView rvNewMatchesList, rvMessagesList;
    private CustomEditText edtSearch;
    private RelativeLayout rlt_empty_message,rlt_search,rltGold;
    private LinearLayout lltEmptyNewList, lltEmptySearchList;
    private NewMatchesListAdapter newMatchesListAdapter;
    private MessageUserListAdapter messageUserListAdapter;
    private ImageView ivEmptyMessageImage, ivEmptySearchImage,ivEmptyHeart;
    private TextView noMatch,tvLikeCount,tvGoldTitle,tvGoldDescription;
    private AlertDialog dialog;
    private MatchedProfileModel matchedProfileModel;
    private ArrayList<NewMatchProfileModel> newMatchProfileModels = new ArrayList<>();
    private ArrayList<NewMatchProfileModel> messageModels = new ArrayList<>();
    private int width;

    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            listener = (ActivityListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Profile must implement ActivityListener");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();
        AppController.getAppComponent().inject(this);
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.chat_fragment, container, false);

           /* view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    //r will be populated with the coordinates of your view that area still visible.
                    view.getWindowVisibleDisplayFrame(r);


                    int heightDiff = view.getRootView().getHeight() - (r.bottom - r.top);
                    if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...

                        edtSearch.setCursorVisible(true);

                    }else if(heightDiff==0){

                        edtSearch.setText("");
                        edtSearch.clearFocus();
                        edtSearch.setCursorVisible(false);

                    }

                }
            });*/

        }

        return view;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        edtSearch = view.findViewById(R.id.et_search);

        tvNewMessageCount = view.findViewById(R.id.tv_new_match_count);
        tvNewMatchTitle = view.findViewById(R.id.tv_new_match_title);
        tvMessageTitle = view.findViewById(R.id.tv_messages_title);
        rvNewMatchesList = view.findViewById(R.id.rv_new_matches_list);
        rvMessagesList = view.findViewById(R.id.rv_message_list);
        rlt_empty_message = view.findViewById(R.id.rlt_empty_message);
        rlt_search = view.findViewById(R.id.rlt_search);
        rltGold = view.findViewById(R.id.rltGold);
        ivEmptyMessageImage = view.findViewById(R.id.iv_empty_message_image);
        ivEmptySearchImage = view.findViewById(R.id.iv_empty_search_image);
        ivEmptyHeart = view.findViewById(R.id.iv_empty_heart);
        noMatch = view.findViewById(R.id.tv_no_match);
        tvLikeCount = view.findViewById(R.id.tv_like_count);
        tvGoldTitle = view.findViewById(R.id.tv_gold_title);
        tvGoldDescription = view.findViewById(R.id.tv_gold_description);
        lltEmptyNewList = view.findViewById(R.id.llt_empty_new_list);
        lltEmptySearchList = view.findViewById(R.id.llt_empty_search_list);
        dialog = commonMethods.getAlertDialog(mActivity);

        initRecyclerView();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_YES) {
        } else if (newConfig.keyboardHidden == Configuration.KEYBOARDHIDDEN_NO) {
        }
    }

    private void initRecyclerView() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (matchedProfileModel != null) {
                    // filter your list from your input
                    filter(s.toString());
                    //you can use runnable postDelayed like 500 ms to delay search text
                }
            }
        });


        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        width = (int) (metrics.widthPixels / 3.5);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rvNewMatchesList.getLayoutParams();
        params.height = width;

        //rvNewMatchesList.setLayoutParams(params);

        CustomLayoutManager layoutManager = new CustomLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        rvNewMatchesList.setLayoutManager(layoutManager);
        rvNewMatchesList.addItemDecoration(new SpacesItemDecoration(2));

        newMatchesListAdapter = new NewMatchesListAdapter(mActivity);
        rvNewMatchesList.setAdapter(newMatchesListAdapter);
        rvNewMatchesList.setHasFixedSize(true);

        CustomLayoutManager customLayoutManager = new CustomLayoutManager(mActivity);
        rvMessagesList.setLayoutManager(customLayoutManager);

        messageUserListAdapter = new MessageUserListAdapter(mActivity);
        rvMessagesList.setAdapter(messageUserListAdapter);
        rvMessagesList.setHasFixedSize(true);
    }

    private void init() {
        if (listener == null) return;

        res = (listener.getRes() != null) ? listener.getRes() : getActivity().getResources();
        mActivity = (listener.getInstance() != null) ? listener.getInstance() : (HomeActivity) getActivity();
    }

    @Override
    public void onDestroy() {
        if (listener != null) listener = null;
        super.onDestroy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && view != null) {

            getMatchedProfile();

            getFragmentManager().popBackStack();

            edtSearch.clearFocus();
            hideKeyboard(getContext());
        }


    }

    private void getMatchedProfile() {
        //commonMethods.showProgressDialog(mActivity, customDialog);
        apiService.matchedDetails(sessionManager.getToken()).enqueue(new RequestCallback(this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(mActivity, dialog, data);
            return;
        }
        if (jsonResp.isSuccess()) {
            onSuccessGetMatchedList(jsonResp);
        } else if (!TextUtils.isEmpty(jsonResp.getStatusMsg())) {
            commonMethods.showMessage(mActivity, dialog, jsonResp.getStatusMsg());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sessionManager.getRefreshChatFragment()) {
            getMatchedProfile();
            sessionManager.setRefreshChatFragment(false);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        edtSearch.clearFocus();
        hideKeyboard(getContext());
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) commonMethods.showMessage(mActivity, dialog, data);
    }

    private void onSuccessGetMatchedList(JsonResponse jsonResp) {
        matchedProfileModel = gson.fromJson(jsonResp.getStrResponse(), MatchedProfileModel.class);
        if (matchedProfileModel != null) {
            updateView();
        } else {
            rlt_empty_message.setVisibility(View.VISIBLE);
            rlt_search.setVisibility(View.GONE);
        }
    }


    private void updateView() {

        conv_no = matchedProfileModel.getNewMatchProfile().size() + matchedProfileModel.getMessage().size();
        edtSearch.setHint(getResources().getText(R.string.search) + " " + conv_no + " " + getResources().getText(R.string.matches));
        if (conv_no > 0) {
            ivEmptySearchImage.setVisibility(View.GONE);
            noMatch.setVisibility(View.GONE);
        }
        if ((matchedProfileModel.getNewMatchProfile() == null || matchedProfileModel.getNewMatchProfile().size() <= 0)
                && (matchedProfileModel.getMessage() == null || matchedProfileModel.getMessage().size() <= 0)) {
            rlt_empty_message.setVisibility(View.VISIBLE);
            rlt_search.setVisibility(View.GONE);
        } else {
            rlt_empty_message.setVisibility(View.GONE);
            rlt_search.setVisibility(View.VISIBLE);
        }


        if (!TextUtils.isEmpty(matchedProfileModel.getIsOrder()) && matchedProfileModel.getIsOrder().equalsIgnoreCase("Yes")) {
            sessionManager.setIsOrder(true);
            sessionManager.setPlanType(matchedProfileModel.getPlanType());
        } else {
            sessionManager.setIsOrder(false);
            sessionManager.setPlanType(matchedProfileModel.getPlanType());
        }


        try{
            if (matchedProfileModel.getUnReadCount() != null && matchedProfileModel.getUnReadCount() > 0) {
                ((HomeActivity) getActivity()).changeChatIcon(1);
            } else {
                ((HomeActivity) getActivity()).changeChatIcon(0);
            }
        }catch (Exception e){

        }

        rltGold.setVisibility(View.VISIBLE);
        if(matchedProfileModel.getLikesCount()>0){
            tvLikeCount.setText(matchedProfileModel.getLikesCount()+"+");
            tvGoldTitle.setText(getResources().getString(R.string.gold_tilte,matchedProfileModel.getLikesCount().toString()));
            tvLikeCount.setVisibility(View.VISIBLE);
            ivEmptyHeart.setVisibility(View.GONE);
        }else {
            tvGoldTitle.setText(getResources().getString(R.string.gold_tilte_empty));
            tvLikeCount.setVisibility(View.GONE);
            ivEmptyHeart.setVisibility(View.VISIBLE);
        }

        rltGold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManager.getIsOrder() && sessionManager.getPlanType().equalsIgnoreCase("Gold")){
                    Intent intent = new Intent(mActivity, LikedUsersActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(mActivity, IgniterPlusDialogActivity.class);
                    intent.putExtra("startwith", "");
                    intent.putExtra("type", "gold");
                    startActivity(intent);
                }
            }
        });

        if (matchedProfileModel.getNewMatchProfile() != null && (matchedProfileModel.getNewMatchProfile().size() > 0)) {// ||matchedProfileModel.getLikesCount()>0) {
            newMatchProfileModels.clear();
            if (matchedProfileModel.getLikesCount() > 0) {
                if (!TextUtils.isEmpty(matchedProfileModel.getIsOrder()) && matchedProfileModel.getIsOrder().equalsIgnoreCase("Yes")) {
                    sessionManager.setIsOrder(true);
                    sessionManager.setPlanType(matchedProfileModel.getPlanType());
                } else {
                    sessionManager.setIsOrder(false);
                    sessionManager.setPlanType(matchedProfileModel.getPlanType());
                }
            }
            newMatchProfileModels.addAll(matchedProfileModel.getNewMatchProfile());

            if (matchedProfileModel.getNewMatchCount() != null && matchedProfileModel.getNewMatchCount() > 0) {
                tvNewMessageCount.setVisibility(View.VISIBLE);
                tvNewMessageCount.setText(String.valueOf(matchedProfileModel.getNewMatchCount()));
            } else {
                tvNewMessageCount.setVisibility(View.GONE);
            }

            tvNewMatchTitle.setVisibility(View.VISIBLE);
            rvNewMatchesList.setVisibility(View.VISIBLE);

            newMatchesListAdapter = new NewMatchesListAdapter(mActivity, newMatchProfileModels, width,matchedProfileModel.getLikesCount());
            rvNewMatchesList.setAdapter(newMatchesListAdapter);
        } else {
            tvNewMatchTitle.setVisibility(View.GONE);
            tvNewMessageCount.setVisibility(View.GONE);
            rvNewMatchesList.setVisibility(View.GONE);
        }

        if (matchedProfileModel.getMessage() != null && matchedProfileModel.getMessage().size() > 0) {
            messageModels.clear();
            messageModels.addAll(matchedProfileModel.getMessage());
            tvMessageTitle.setVisibility(View.VISIBLE);
            rvMessagesList.setVisibility(View.VISIBLE);

            messageUserListAdapter = new MessageUserListAdapter(mActivity, messageModels);
            rvMessagesList.setAdapter(messageUserListAdapter);
        } else {
            tvMessageTitle.setVisibility(View.GONE);
            rvMessagesList.setVisibility(View.GONE);
        }
    }


    void filter(String text) {
        ArrayList temp = new ArrayList();
        ArrayList temp1 = new ArrayList();
        for (NewMatchProfileModel d : messageModels) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getUserName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }
        //update recyclerview
        messageUserListAdapter.updateList(temp);

        for (NewMatchProfileModel d : newMatchProfileModels) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getUserName().toLowerCase().contains(text.toLowerCase())) {
                temp1.add(d);
            }
        }
        //update recyclerview
        newMatchesListAdapter.updateList(temp1);

        if (temp.size() <= 0) {
            ivEmptyMessageImage.setVisibility(View.GONE);
        } else {
            ivEmptyMessageImage.setVisibility(View.GONE);
        }

        if (temp1.size() <= 0) {
            lltEmptyNewList.setVisibility(View.GONE);
        } else {
            lltEmptyNewList.setVisibility(View.VISIBLE);
        }

        if (temp.size() <= 0 && temp1.size() <= 0) {
            lltEmptySearchList.setVisibility(View.GONE);
            ivEmptySearchImage.setVisibility(View.VISIBLE);

            noMatch.setVisibility(View.VISIBLE);
        } else {
            lltEmptySearchList.setVisibility(View.VISIBLE);
            ivEmptySearchImage.setVisibility(View.GONE);
            noMatch.setVisibility(View.GONE);
        }

        if(rlt_empty_message.getVisibility()==View.VISIBLE){
            ivEmptySearchImage.setVisibility(View.GONE);
            noMatch.setVisibility(View.GONE);
        }


    }
}
