package com.example.studyspacesosu;

import android.animation.ObjectAnimator;
import android.content.Intent;
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

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("latitude", 0);
        double lng = intent.getDoubleExtra("longitude", 0);

        mClickPosition = new LatLng(lat, lng);

        mCoordinateView = findViewById(R.id.coordinate_display);
        mCoordinateView.setText("Coordinates: " + mClickPosition.latitude + ", " + mClickPosition.longitude);

        mAreaName = findViewById(R.id.name_field);
        mAreaDescription = findViewById(R.id.description_field);

        mSubmitButton = findViewById(R.id.submit_area_add);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Map<String, Object> studyArea = new HashMap<>();
                studyArea.put("Name", mAreaName.getText().toString());
                studyArea.put("Description", mAreaDescription.getText().toString());

                GeoPoint point = new GeoPoint(mClickPosition.latitude, mClickPosition.longitude);
                studyArea.put("Coordinates", point);

                mDatabase.collection("study area")
                        .add(studyArea)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("DocumentAdd", "DocumentSnapshot added with ID: " + documentReference.getId());
                                Toast.makeText(AddSpaceActivity.this, "Area Added Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("DocumentAdd", "Error adding document", e);
                            }
                        });

            }
        });
    }

}
