package com.example.studyspacesosu;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class SearchableActivity extends AppCompatActivity {

    Intent callIntent;
    private FirebaseFirestore mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("Search OnCreate", "Search fragment OnCreate called");

        setContentView(R.layout.search);

        mDatabase = FirebaseFirestore.getInstance();

        // Get the intent, verify the action and get the query
        Intent callingIntent = getIntent();
        if (Intent.ACTION_SEARCH.equals(callingIntent.getAction())) {
            String query = callingIntent.getStringExtra(SearchManager.QUERY);
            search(query);
        }
    }

    private void search(final String myQuery){
        //search myQuery in DB
        //Build intent from result in db
        //set edit intent with built intent in new space fragment

        final Intent editIntent = new Intent();
        final Map<String, Object> markerData = new HashMap<>();

        markerData.put("Name", "Name not found");
        markerData.put("Description", "Description not found");
        markerData.put("Coordinates", "Coordinates not found");
        markerData.put("Id", "ID not found");


        mDatabase.collection("study area").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.get("Name").equals(myQuery)){

                                    //build intent from document
                                    GeoPoint location = (GeoPoint) document.get("Coordinates");
                                    double lat = location.getLatitude();
                                    double lng = location.getLongitude();
                                    LatLng pos = new LatLng(lat, lng);

                                    markerData.put("Name", document.get("Name"));
                                    markerData.put("Description", document.get("Description"));
                                    markerData.put("Coordinates", pos);
                                    markerData.put("Id", document.getId());
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });




        //Create space info fragment with intent data
        Intent mainIntent = new Intent();
        mainIntent.putExtra("DataMap", (HashMap) markerData);
        mainIntent.setClass(getApplicationContext(), MainActivity.class);
        startActivity(mainIntent);
        finish();


    }

}
