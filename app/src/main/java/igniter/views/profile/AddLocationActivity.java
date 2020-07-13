package igniter.views.profile;

/**
 * @package com.trioangle.igniter
 * @subpackage view.profile
 * @category AddLocationActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;

//import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.obs.CustomEditText;
import com.obs.CustomTextView;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import igniter.R;
import igniter.adapters.infowindow.CustomInfoWindowGoogleMap;
import igniter.adapters.infowindow.InfoWindowModel;
import igniter.configs.AppController;
import igniter.configs.Constants;
import igniter.configs.RunTimePermission;
import igniter.configs.SessionManager;
import igniter.datamodels.main.InsertLocationModel;
import igniter.datamodels.main.JsonResponse;
import igniter.interfaces.ApiService;
import igniter.interfaces.ServiceListener;
import igniter.placesearch.GoogleMapPlaceSearchAutoCompleteRecyclerView;
import igniter.utils.CommonMethods;
import igniter.utils.MyLocation;
import igniter.utils.RequestCallback;
import igniter.views.customize.CustomDialog;

import static igniter.utils.Enums.REQ_ADD_LOCATION;

/****************************************************************
 Add additional match profile search location after purchase gold or plus plan
 ****************************************************************/

public class AddLocationActivity extends AppCompatActivity implements ServiceListener, View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,GoogleMapPlaceSearchAutoCompleteRecyclerView.AutoCompleteAddressTouchListener {

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-0, 0), new LatLng(0, 0));
    private static String TAG = "MAP LOCATION";
    final AndroidHttpClient ANDROID_HTTP_CLIENT = AndroidHttpClient.newInstance(AddLocationActivity.class.getName());
    public InsertLocationModel insertLocationModel;
    protected GoogleApiClient mGoogleApiClient;
    SupportMapFragment mapFragment;
    ImageView ivBack, ivGps;
    CustomEditText edtSearch;
    Location mLastLocation;
    List<Address> addressList = null; // Addresslist
    String address = null; // Address
    String country = null; // Country name
    String state = null; // Country name
    String city = null; // Country name
    LatLng getLocations = null;
    String getAddress = null;
    String lat, log;
    LatLng currentLatLng;
    String currentCity, currentState;
    String searchlocation;
    LatLng objLatLng;
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
    @Inject
    RunTimePermission runTimePermission;
    private GoogleMap googleMap;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private GoogleMapPlaceSearchAutoCompleteRecyclerView mAutoCompleteAdapter;
    private AlertDialog dialog;
    private List<AutocompletePrediction>addressAutoCompletePredictions = new ArrayList<>() ;

    PlacesClient placesClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();  // Connect Google API client
        setContentView(R.layout.activity_add_location);
        AppController.getAppComponent().inject(this);
        initView();
        placeSearch();
        try {
            initilizeMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivGps = (ImageView) findViewById(R.id.iv_gps);
        edtSearch = (CustomEditText) findViewById(R.id.edt_search);

        dialog = commonMethods.getAlertDialog(this);
        ivBack.setOnClickListener(this);
        ivGps.setOnClickListener(this);

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    //mAutoCompleteAdapter.getFilter().filter(edtSearch.getText().toString()); // Place search
                    getFullAddressUsingEdittextStringFromGooglePlaceSearchAPI(edtSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initilizeMap() {
        if (googleMap == null) {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        googleMap.setOnMapClickListener(this);

        /*googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {

            @Override
            public boolean onMarkerClick(Marker arg0) {
              //  insertLocation(currentLatLng.latitude,currentLatLng.longitude,currentCity,currentState);
                return true;
            }

        });*/

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng latLon = marker.getPosition();
                insertLocation(currentLatLng.latitude, currentLatLng.longitude, currentCity, currentState);
            }
        });

    }

    /**
     * function to get Address and location while click map
     */
    @Override
    public void onMapClick(LatLng latLng) {
        addCustomMarker(latLng, 1, "", "");
    }

    /**
     * function to add custom marker
     */
    private void addCustomMarker(final LatLng position, final int type, final String address, final String state) {
        Log.d("Marker", "addCustomMarker()");
        if (googleMap == null) {
            return;
        }
        googleMap.clear();
        /*googleMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(type, address, state))));*/

        //Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.passport_map_location_marker);
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.passport_map_location_marker);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position)
                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

        InfoWindowModel info = new InfoWindowModel();
        info.setAddress(address);
        info.setType(type);
        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(this);
        googleMap.setInfoWindowAdapter(customInfoWindow);

        Marker m = googleMap.addMarker(markerOptions);
        m.setTag(info);
        m.showInfoWindow();

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(position));

        currentLatLng = position;
        currentCity = address;
        currentState = state;
        if (type == 1) {
            fetchAddress(position, "search");
        }
    }


    /**
     * function to show marker
     */
    public void showCustomMarker(LatLng latLng, String city, String state, String country) {
        if (city != null && state != null)
            addCustomMarker(latLng, 0, city, state);
        else if (state != null && country != null)
            addCustomMarker(latLng, 0, state, country);
        else if (city != null && country != null)
            addCustomMarker(latLng, 0, city, country);
        else if (city != null)
            addCustomMarker(latLng, 0, city, "");
        else if (state != null)
            addCustomMarker(latLng, 0, state, "");
        else if (country != null)
            addCustomMarker(latLng, 0, country, "");
        else
            addCustomMarker(latLng, 2, "", "");
    }

    /**
     * function to add click action
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_gps:
                mRecyclerView.setVisibility(View.GONE);
                //changeMap(mLastLocation);
                checkAllPermission(Constants.PERMISSIONS_LOCATION);
                break;
        }
    }

    /**
     * function to Convert layout to bitmap for custom marker
     */
    private Bitmap getMarkerBitmapFromView(int type, String address, String state) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        CustomTextView markerText = (CustomTextView) customMarkerView.findViewById(R.id.tv_address);
        if (type == 0) {
            String sourceString = getResources().getString(R.string.gotos) + " ";
            if (address != null && address.length() > 0)
                sourceString = sourceString + "" + address;
            if (state != null && state.length() > 0)
                sourceString = sourceString + "" + "\n" + state;

            SpannableString str = new SpannableString(sourceString);
            if (address != null && address.length() > 0)
                str.setSpan(new StyleSpan(Typeface.BOLD), 4, address.length() + 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            markerText.setText(str);
        } else if (type == 1) {
            markerText.setText(getResources().getString(R.string.searching));
        } else if (type == 2)
            markerText.setText(getResources().getString(R.string.not_found));

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    /**
     * function to Place search init
     */
    public void placeSearch() {

        placesClient = com.google.android.libraries.places.api.Places.createClient(this);

        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();

        mAutoCompleteAdapter = new GoogleMapPlaceSearchAutoCompleteRecyclerView(addressAutoCompletePredictions, this, this);

        // Place search list
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_location_search);
        mRecyclerView.setVisibility(View.GONE);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAutoCompleteAdapter);
    }

    public void getFullAddressUsingEdittextStringFromGooglePlaceSearchAPI(String queryAddress){
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request =
                FindAutocompletePredictionsRequest.builder()
                        .setSessionToken(token)
                        .setQuery(queryAddress)
                        .build();
        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener(
                        (response) -> {
                            mAutoCompleteAdapter.updateList(response.getAutocompletePredictions());

                        })
                .addOnFailureListener((exception) -> {
                    exception.printStackTrace();
                });
    }

    /**
     * Connect Google APi Client
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    /**
     * Google APi on Connected
     */
    @Override
    public void onConnected(Bundle bundle) {
        getLocation(0);

    }

    /**
     * Google API suspended
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    public void getLocation(int type) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null && type != 0) {
            LatLng current_latlng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            changeMap(mLastLocation);
            Log.d(TAG, "ON connected");

        } else
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Google API connection failed
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * Move map to current location and get address while move amp
     */
    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + googleMap);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (googleMap != null) {
            LatLng latLong;


            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(16.5f).tilt(0).build();

            googleMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            fetchAddress(latLong, "search");

        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.er_maps_create), Toast.LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            Log.v("Google API", "Connecting");
            mGoogleApiClient.connect();
        }
        initilizeMap();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            Log.v("Google API", "Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Fetch Location from address if Geocode available get from geocode otherwise get location from google
     */
    public void fetchLocation(String addresss, final String type) {
        getAddress = addresss;

        new AsyncTask<Void, Void, String>() {
            String locations = null;

            protected void onPreExecute() {

            }

            ;

            @Override
            protected String doInBackground(Void... params) {

                if (Geocoder.isPresent()) // Check geo code available or not
                {
                    try {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> address;

                        // May throw an IOException
                        address = geocoder.getFromLocationName(getAddress, 5);
                        if (address == null) {
                            return null;
                        }
                        Address location = address.get(0);
                        city = null;
                        state = null;
                        country = null;
                        country = address.get(0).getCountryName();
                        state = address.get(0).getAdminArea();
                        city = address.get(0).getLocality();

                        location.getLatitude();
                        location.getLongitude();

                        lat = String.valueOf(location.getLatitude());
                        log = String.valueOf(location.getLongitude());
                        locations = lat + "," + log;
                    } catch (Exception ignored) {
                        // after a while, Geocoder start to throw "Service not availalbe" exception. really weird since it was working before (same device, same Android version etc..
                    }
                }

                if (locations != null) // i.e., Geocoder succeed
                {
                    return locations;
                } else // i.e., Geocoder failed
                {
                    return fetchLocationUsingGoogleMap(); // If geocode not available or location null call google API
                }
            }

            // Geocoder failed :-(
            // Our B Plan : Google Map
            private String fetchLocationUsingGoogleMap() {
                getAddress = getAddress.replaceAll(" ", "%20");
                String googleMapUrl = "https://maps.google.com/maps/api/geocode/json?address=" + getAddress + "&sensor=false";
                try {
                    JSONObject googleMapResponse = new JSONObject(ANDROID_HTTP_CLIENT.execute(new HttpGet(googleMapUrl),
                            new BasicResponseHandler()));

                    // many nested loops.. not great -> use expression instead
                    // loop among all results

                    if (googleMapResponse.length() > 0) {
                        String longitute = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                .getJSONObject("geometry").getJSONObject("location")
                                .getString("lng");

                        String latitude = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                .getJSONObject("geometry").getJSONObject("location")
                                .getString("lat");

                        int len = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                .getJSONArray("address_components").length();
                        for (int i = 0; i < len; i++) {
                            if (((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                    .getJSONArray("address_components").getJSONObject(i).getJSONArray("types").getString(0).equals("country")) {
                                country = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                        .getJSONArray("address_components").getJSONObject(i).getString("long_name");
                            }
                        }


                        return latitude + "," + longitute;
                    } else {
                        return null;
                    }

                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String location) {
                if (location != null) {
                    String[] parts = location.split(",");
                    String part1 = parts[0]; // 004
                    String part2 = parts[1]; // 034556
                    Double lat = Double.valueOf(parts[0]);
                    Double lng = Double.valueOf(parts[1]);
                    LatLng latLng = new LatLng(lat, lng);
                    showCustomMarker(latLng, city, state, country);
                } else {
                    //addCustomMarker(latLng,2,"","");
                }
            }

            ;
        }.execute();
    }

    /**
     * Fetch address from location if Geocode available get from geocode otherwise get location from google
     */
    public void fetchAddress(LatLng location, final String type) {
        getLocations = location;
        address = null;
        new AsyncTask<Void, Void, String>() {

            protected void onPreExecute() {

            }

            ;

            @Override
            protected String doInBackground(Void... params) {

                if (Geocoder.isPresent()) // Check Geo code available or not
                {
                    try {

                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(getLocations.latitude, getLocations.longitude, 1);
                        if (addresses != null) {

                            city = null;
                            state = null;
                            country = null;
                            country = addresses.get(0).getCountryName();

                            String adress0 = addresses.get(0).getAddressLine(0);
                            String adress1 = addresses.get(0).getAddressLine(1);


                            address = adress0 + " " + adress1; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            city = addresses.get(0).getLocality();
                            state = addresses.get(0).getAdminArea();

                        }
                    } catch (Exception ignored) {
                        // after a while, Geocoder start to throw "Service not availalbe" exception. really weird since it was working before (same device, same Android version etc..
                    }
                }
                if (address != null) // i.e., Geocoder succeed
                {
                    return address;
                } else // i.e., Geocoder failed
                {
                    return fetchAddressUsingGoogleMap();
                }
            }

            // Geocoder failed :-(
            // Our B Plan : Google Map
            private String fetchAddressUsingGoogleMap() {

                addressList = new ArrayList<Address>();
                String googleMapUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + getLocations.latitude + ","
                        + getLocations.longitude + "&key=" + getResources().getString(R.string.google_maps_key) + "&sensor=false";
                try {
                    JSONObject googleMapResponse = new JSONObject(ANDROID_HTTP_CLIENT.execute(new HttpGet(googleMapUrl),
                            new BasicResponseHandler()));

                    // many nested loops.. not great -> use expression instead
                    // loop among all results

                    JSONArray results = (JSONArray) googleMapResponse.get("results");
                    for (int i = 0; i < results.length(); i++) {


                        JSONObject result = results.getJSONObject(i);

                        city = null;
                        state = null;
                        country = null;
                        String indiStr = result.getString("formatted_address");


                        Address addr = new Address(Locale.getDefault());


                        addr.setAddressLine(0, indiStr);
                        //  country=addr.getCountryName();

                        addressList.add(addr);


                    }

                    int len = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                            .getJSONArray("address_components").length();
                    for (int i = 0; i < len; i++) {
                        if (((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                .getJSONArray("address_components").getJSONObject(i).getJSONArray("types").getString(0).equals("country")) {
                            country = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                    .getJSONArray("address_components").getJSONObject(i).getString("long_name");
                        }
                        if (((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                .getJSONArray("address_components").getJSONObject(i).getJSONArray("types").getString(0).equals("administrative_area_level_2")) {
                            city = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                    .getJSONArray("address_components").getJSONObject(i).getString("long_name");
                        }

                        if (((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                .getJSONArray("address_components").getJSONObject(i).getJSONArray("types").getString(0).equals("administrative_area_level_1")) {
                            state = ((JSONArray) googleMapResponse.get("results")).getJSONObject(0)
                                    .getJSONArray("address_components").getJSONObject(i).getString("long_name");
                        }

                    }

                    if (addressList != null) {

                        String adress0 = addressList.get(0).getAddressLine(0);
                        String adress1 = addressList.get(0).getAddressLine(1);
                        address = adress0;//+" "+adress1;
                        //address = adress0+" "+adress1; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                        address.replaceAll("null", "");

                        if (address != null) {
                            return address;
                        }
                    }

                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String address) {
                LatLng latLng = new LatLng(getLocations.latitude, getLocations.longitude);
                if (address != null) {

                    showCustomMarker(latLng, city, state, country);
                } else {
                    addCustomMarker(latLng, 2, "", "");
                }

            }

            ;
        }.execute();
    }


    /**
     * function to add location
     */
    private void insertLocation(Double latitude, Double longitude, String address1, String address2) {
        sessionManager.setSettingUpdate(true);
        commonMethods.showProgressDialog(this, customDialog);
        apiService.insertLocation(sessionManager.getToken(), latitude, longitude, "insert").enqueue(new RequestCallback(REQ_ADD_LOCATION, this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (!jsonResp.isOnline()) {
            commonMethods.showMessage(this, dialog, data);
            return;
        }
        switch (jsonResp.getRequestCode()) {
            case REQ_ADD_LOCATION:
                if (jsonResp.isSuccess()) onSuccessInsertLocation(jsonResp);
                break;
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        //if (!jsonResp.isOnline()) commonMethods.showMessage(this, dialog, data);
        commonMethods.showMessage(this, dialog, data);
    }

    private void onSuccessInsertLocation(JsonResponse jsonResp) {
        insertLocationModel = gson.fromJson(jsonResp.getStrResponse(), InsertLocationModel.class);
        Intent returnIntent = new Intent();
        returnIntent.putExtra("location", (Serializable) insertLocationModel.getLocationModels());
        setResult(REQ_ADD_LOCATION, returnIntent);
        finish();
    }

    private void checkAllPermission(String[] permission) {
        ArrayList<String> blockedPermission = runTimePermission.checkHasPermission(this, permission);
        if (blockedPermission != null && !blockedPermission.isEmpty()) {
            boolean isBlocked = runTimePermission.isPermissionBlocked(this, blockedPermission.toArray(new String[blockedPermission.size()]));
            if (isBlocked) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        //callPermissionSettings();
                        showEnablePermissionDailog(0, getString(R.string.please_enable_permissions));
                    }
                });
            } else {
                ActivityCompat.requestPermissions(this, permission, 150);
            }
        } else {
            checkGpsEnable();
        }
    }

    private void checkGpsEnable() {
        boolean isGpsEnabled = MyLocation.defaultHandler().isLocationAvailable(this);
        if (!isGpsEnabled) {
            //startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 101);
            showEnablePermissionDailog(1, getString(R.string.please_enable_location));
        } else {
            getLocation(1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ArrayList<String> permission = runTimePermission.onRequestPermissionsResult(permissions, grantResults);
        if (permission != null && !permission.isEmpty()) {
            runTimePermission.setFirstTimePermission(true);
            String[] dsf = new String[permission.size()];
            permission.toArray(dsf);
            checkAllPermission(dsf);
        } else {
            checkGpsEnable();
        }
    }

    private void showEnablePermissionDailog(final int type, String message) {
        if (!customDialog.isVisible()) {
            customDialog = new CustomDialog(message, getString(R.string.okay), new CustomDialog.btnAllowClick() {
                @Override
                public void clicked() {
                    if (type == 0)
                        callPermissionSettings();
                    else
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 101);
                }
            });
            customDialog.show(getSupportFragmentManager(), "");
        }
    }

    private void callPermissionSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 300);
    }

    @Override
    public void selectedAddress(AutocompletePrediction autocompletePrediction) {
        final String placeId = String.valueOf(autocompletePrediction.getPlaceId());
        Log.i("TAG", "Autocomplete item selected: " + autocompletePrediction.getFullText(null).toString());

        searchlocation = (String) autocompletePrediction.getFullText(null).toString();

        edtSearch.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
        mRecyclerView.setVisibility(View.GONE);
        //mAutoCompleteAdapter.clear();
        fetchLocation(searchlocation, "search");
    }

}
