package com.example.na_at.examen_app_geo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.na_at.examen_app_geo.Service.FetchAddressIntentService;
import com.example.na_at.examen_app_geo.model.Bancos;
import com.example.na_at.examen_app_geo.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final int LOCATION_PERMISSION_CODE = 123;
    private GoogleMap mMap;

    protected static final String TAG = "main-activity";

    protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    protected static final String LOCATION_ADDRESS_KEY = "location-address";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    protected boolean mAddressRequested;

    /**
     * The formatted location address.
     */
    protected String mAddressOutput;

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    /**
     * Displays the location address.
     */
    protected TextView mLocationAddressTextView;

    /**
     * Visible while the address is being fetched.
     */
    ProgressBar mProgressBar;

    /**
     * Kicks off the request to fetch an address when pressed.
     */
    ImageView mFetchAddressButton;
    private Button buscarBancosBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initElements();

        mResultReceiver = new AddressResultReceiver(new Handler());
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";

        buildGoogleApiClient();

    }

    private void initElements() {
        buscarBancosBtn = (Button) findViewById(R.id.buscarBancosID);
        buscarBancosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarBancos();
            }
        });
        mLocationAddressTextView = (TextView) findViewById(R.id.location_address_view1);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar1);
        mFetchAddressButton = (ImageView) findViewById(R.id.fetch_address_button1);
        mFetchAddressButton.setOnClickListener(fetchAddressButtonHandler1);

    }

    private void buscarBancos() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                Constants.URL_API1 + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + Constants.URL_API2,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("URL", Constants.URL_API1 + mLastLocation.getLatitude() + "," + mLastLocation.getLongitude() + Constants.URL_API2);
                        Log.d(TAG, response.toString());
                        new parseJson().execute(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        );
        // Adding request to request queue
        VolleyApp.getmInstance().addToRequestQueue(jsonObjectRequest);


    }

    public class parseJson extends AsyncTask<JSONObject, Void, ArrayList<Bancos>> {
        ArrayList<Bancos> bancosArrayList = new ArrayList<Bancos>();

        @Override
        protected ArrayList<Bancos> doInBackground(JSONObject... jsonObjects) {
            try {
                JSONArray jsonArray = jsonObjects[0].getJSONArray("results");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if(String.valueOf(jsonObject.getDouble("rating"))!=null){
                        Bancos bancos = new Bancos(
                                jsonObject.getString("name"),
                                jsonObject.getString("vicinity"),
                                jsonObject.getDouble("rating"));
                        bancosArrayList.add(bancos);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return bancosArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<Bancos> bancosArrayList) {
            super.onPostExecute(bancosArrayList);
            if (bancosArrayList.size() > 0) {
                Intent intent = new Intent(getApplicationContext(), Bancos_Activity.class);
                intent.putParcelableArrayListExtra("bancos", bancosArrayList);
                startActivity(intent);
            }
        }
    }


    /**
     * Builds a GoogleApiClient. Uses {@code #addApi} to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

    /**
     * Runs when user clicks the Fetch Address button. Starts the service to fetch the address if
     * GoogleApiClient is connected.
     */
    View.OnClickListener fetchAddressButtonHandler1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // We only start the service to fetch the address if GoogleApiClient is connected.
            if (mGoogleApiClient.isConnected() && mLastLocation != null) {
                startIntentService();
            }
            // If GoogleApiClient isn't connected, we process the user's request by setting
            // mAddressRequested to true. Later, when GoogleApiClient connects, we launch the service to
            // fetch the address. As far as the user is concerned, pressing the Fetch Address button
            // immediately kicks off the process of getting the address.
            mAddressRequested = true;
            updateUIWidgets();
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {

        if (canAccessLocation()) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                // Gets the best and most recent location currently available, which may be null
                // in rare cases when a location is not available.
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    // Determine whether a Geocoder is available.
                    if (!Geocoder.isPresent()) {
                        Toast.makeText(getApplicationContext(), R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                        return;
                    }
                    // It is possible that the user presses the button to get the address before the
                    // GoogleApiClient object successfully connects. In such a case, mAddressRequested
                    // is set to true, but no attempt is made to fetch the address (see
                    // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
                    // user has requested an address, since we now have a connection to GoogleApiClient.
                    //if (mAddressRequested) {
                    //  startIntentService();
                    //}
                    startIntentService();
                }
            }
            return;
        }

        //If the app has not the permission then asking for the permission
        requestLocationPermission();

        /*

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Gets the best and most recent location currently available, which may be null
            // in rare cases when a location is not available.
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                // Determine whether a Geocoder is available.
                if (!Geocoder.isPresent()) {
                    Toast.makeText(getContext(), R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                    return;
                }
                // It is possible that the user presses the button to get the address before the
                // GoogleApiClient object successfully connects. In such a case, mAddressRequested
                // is set to true, but no attempt is made to fetch the address (see
                // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
                // user has requested an address, since we now have a connection to GoogleApiClient.
                //if (mAddressRequested) {
                //  startIntentService();
                //}
                startIntentService();
            }
        } */
    }

    //We are calling this method to check the permission status
    private boolean canAccessLocation() {
        //Getting the permission status
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        //If permission is granted returning true
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // Gets the best and most recent location currently available, which may be null
            // in rare cases when a location is not available.
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            return true;
        }

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestLocationPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            Toast.makeText(getApplicationContext(), "Se necesita acceder a la ubicación para el uso correcto del mapa", Toast.LENGTH_SHORT).show();
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == LOCATION_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
                Toast.makeText(getApplicationContext(), "Permission granted now you can access location", Toast.LENGTH_LONG).show();

                if (mLastLocation != null) {
                    // Determine whether a Geocoder is available.
                    if (!Geocoder.isPresent()) {
                        Toast.makeText(getApplicationContext(), R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                        return;
                    }
                    // It is possible that the user presses the button to get the address before the
                    // GoogleApiClient object successfully connects. In such a case, mAddressRequested
                    // is set to true, but no attempt is made to fetch the address (see
                    // fetchAddressButtonHandler()) . Instead, we start the intent service here if the
                    // user has requested an address, since we now have a connection to GoogleApiClient.
                    //if (mAddressRequested) {
                    //  startIntentService();
                    //}
                    startIntentService();
                }

            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getApplicationContext(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(getApplicationContext(), FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        mLocationAddressTextView.setText(mAddressOutput);
    }

    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private void updateUIWidgets() {
        if (mAddressRequested) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
            mFetchAddressButton.setEnabled(false);
        } else {
            mProgressBar.setVisibility(ProgressBar.GONE);
            mFetchAddressButton.setEnabled(true);
        }
    }

    /**
     * Shows a toast with the given text.
     */
    protected void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
                configCamera();

            }

            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            updateUIWidgets();
        }
    }

    private void configCamera() {
        CameraPosition cameraPosition = new CameraPosition(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 16, 20, 40);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        mMap.moveCamera(cameraUpdate);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.addMarker(new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("Marker"));
    }
}
