package com.example.studyspacesosu;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class AddSpaceActivity extends AppCompatActivity {

    private LatLng mClickPosition;
    private TextView mCoordinateView;
    private EditText mAreaName;
    private EditText mAreaDescription;
    private Button mSubmitButton;
    private FirebaseFirestore mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_space);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("latitude", 0);
        double lng = intent.getDoubleExtra("longitude", 0);

        mClickPosition = new LatLng(lat, lng);

        mCoordinateView = findViewById(R.id.coordinate_display);
        mCoordinateView.setText("Coordinates: " + String.format("%.4f", mClickPosition.latitude) + ", " + String.format("%.4f", mClickPosition.longitude));

        mAreaName = findViewById(R.id.name_field);
        mAreaDescription = findViewById(R.id.description_field);

        mSubmitButton = findViewById(R.id.submit_area_add);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = manager.getActiveNetworkInfo();

                if (netInfo != null && netInfo.isConnected()) {

                    if (mAreaName.getText().toString().equals("")) {
                        Toast.makeText(AddSpaceActivity.this, "Name must not be blank.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> studyArea = new HashMap<>();
                    studyArea.put("Name", mAreaName.getText().toString());
                    studyArea.put("Description", mAreaDescription.getText().toString());

                    GeoPoint point = new GeoPoint(mClickPosition.latitude, mClickPosition.longitude);
                    studyArea.put("Coordinates", point);
                    int rating = 0;
                    studyArea.put("up rating", rating);
                    studyArea.put("down rating", rating);
                    final HashMap<String, Object> markerData = new HashMap<>(studyArea);
                    markerData.put("latitude", mClickPosition.latitude);
                    markerData.put("longitude", mClickPosition.longitude);
                    markerData.remove("Coordinates");
                    mDatabase.collection("study area")
                            .add(studyArea)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("DocumentAdd", "DocumentSnapshot added with ID: " + documentReference.getId());
                                    Toast.makeText(AddSpaceActivity.this, "Area Added Successfully.", Toast.LENGTH_SHORT).show();

                                    markerData.put("Id", documentReference.getId());

                                    Intent intent = new Intent();
                                    intent.putExtra("markerData", (HashMap) markerData);
                                    setResult(44, intent);
                                    finish();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("DocumentAdd", "Error adding document", e);
                                }
                            });

                } else {
                    Toast.makeText(AddSpaceActivity.this, "No Internet Connection!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
