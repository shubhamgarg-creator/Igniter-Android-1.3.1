package igniter.views.signup;
/**
 * @package com.trioangle.igniter
 * @subpackage view.signup
 * @category CountryCodeActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import igniter.R;
import igniter.adapters.signup.CountryCodeListAdapter;

/*****************************************************************
 Select user country code while user signup or signin
 ****************************************************************/
public class CountryCodeActivity extends ListActivity implements ListView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        CountryCodeListAdapter countryCodeListAdapter = new CountryCodeListAdapter(this);
        setListAdapter(countryCodeListAdapter);

        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String[] countryFlag = this.getResources().getStringArray(R.array.slide_icon);
        String[] countryCode = this.getResources().getStringArray(R.array.country_code);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("countryFlag", countryFlag[position]);
        returnIntent.putExtra("countryCode", countryCode[position]);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
