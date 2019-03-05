package com.example.studyspacesosu;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.support.constraint.Constraints.TAG;

public class MapsFragment extends SupportMapFragment implements OnMapReadyCallback {

    //Taken from https://developers.google.com/maps/documentation/android-sdk/start

    private GoogleMap mMap;
    private GoogleApiClient mApiClient;
    private static final String[] LOCATION_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLocation;
    private LatLng mDefaultLocation;
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private boolean mLocationPermissionGranted = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("Map OnCreate", "Map fragment OnCreate called");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mApiClient= new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        getActivity().invalidateOptionsMenu(); }
                    @Override
                    public void onConnectionSuspended(int i) {} }).build();

        getMapAsync(this);
    }

    @Override
    public void onResume() {
        Log.i("Map", "Map fragment OnResume called");
        super.onResume();
        setUpEula();
        findLocation(); }

    @Override
    public void onStart() {
        Log.i("Map", "Map fragment OnStart called");
        getActivity().invalidateOptionsMenu();
        mApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i("Map", "Map fragment OnStop called");
        mApiClient.disconnect();
        super.onStop();
    }

    private void findLocation() {
        Log.i("Map", "Map fragment findLocation called");
        updateLocationUI();
        if (hasLocationPermission()) {
            FusedLocationProviderClient locationProvider = LocationServices.getFusedLocationProviderClient(getActivity());
            Task locationResult = locationProvider.getLastLocation();
            FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
            mDefaultLocation= new LatLng(40.0, -83.0);
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setNumUpdates(1);
            locationRequest.setInterval(0);
            locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        mLocation= (Location) task.getResult();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude(),
                                mLocation.getLongitude()), 16));
                    } else { /* Disable Location */ }  }  }); }
    }

    private void setUpEula() {
        Log.i("Map", "Map fragment setUpEula called");
        SharedPreferences mSettings = getActivity().getSharedPreferences(getString(R.string.prefs), 0);
        boolean isEulaAccepted = mSettings.getBoolean(getString(R.string.eula_accepted_key),
                false);
        if (!isEulaAccepted) {
            DialogFragment eulaDialogFragment= new EulaDialogFragment();
            eulaDialogFragment.show(getActivity().getSupportFragmentManager(), "eula");  }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.i("Map", "Map fragment OnCreateOptionsMenu called");
        inflater.inflate(R.menu.menu_showcurrentlocation, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("Map", "Map fragment OnOptionsItemsSelected called");
        switch (item.getItemId()) {
            case R.id.menu_showcurrentlocation:
                Log.d(TAG, "Showing current location");
                if (hasLocationPermission()) {
                    findLocation(); }
                else { /* Request permissions */ }
                break; }
        return true;
    }

     @Override
     public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted= true;}}}
        updateLocationUI();  }

     private void updateLocationUI() {
        if (mMap== null) { return; }
        try {
            if (mLocationPermissionGranted) {/* Enable location */ }
            else { /* Disable location, request permissions */  }
        } catch (SecurityException e)  { /* . . . */ }   }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i("Map OnMapReady", "Map fragment OnMapReady called");

        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng ohio = new LatLng(40.0, -83.0);
        mMap.addMarker(new MarkerOptions().position(ohio).title("Ohio State University"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ohio));
        //mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
    }

    private boolean hasLocationPermission() {
        /**/
        return true;
    }
}