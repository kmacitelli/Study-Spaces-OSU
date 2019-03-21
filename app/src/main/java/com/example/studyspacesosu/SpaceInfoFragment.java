package com.example.studyspacesosu;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SpaceInfoFragment extends Fragment {

    private TextView mCoordinateView;
    private EditText mAreaName;
    private EditText mAreaDescription;
    private Button mSubmitButton;
    private Button mDeleteButton;
    private FirebaseFirestore mDatabase;

    private SpaceInfoViewModel mViewModel;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.i("Info", "Space info onCreateView called");

        super.onCreateView(inflater, container, savedInstanceState);

        mDatabase = FirebaseFirestore.getInstance();



        return inflater.inflate(R.layout.space_info_fragment, container, false);


       // Intent callingIntent = getIntent();
        /*final Map<String, Object> markerData = (HashMap<String, Object>) callingIntent.getSerializableExtra("DataMap");

        mCoordinateView = findViewById(R.id.coordinate_display);
        mCoordinateView.setText("Coordinates: " + ((LatLng)markerData.get("Coordinates")).latitude + ", " + ((LatLng)markerData.get("Coordinates")).longitude);

        mAreaName = findViewById(R.id.name_field);
        mAreaName.setText((String) markerData.get("Name"));
        mAreaDescription = findViewById(R.id.description_field);
        mAreaDescription.setText((String)markerData.get("Description"));

        mSubmitButton = findViewById(R.id.submit_area_add);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> updateData = new HashMap<>();
                updateData.put("Name", mAreaName.getText().toString());
                updateData.put("Description", mAreaDescription.getText().toString());

                mDatabase.collection("study area").document((String) markerData.get("Id"))
                        .update(updateData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditSpaceActivity.this, "Area Deleted Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        mDeleteButton = findViewById(R.id.delete_area_add);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.collection("study area").document((String) markerData.get("Id"))
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("EditSpace", "DocumentSnapshot successfully deleted!");
                                Toast.makeText(EditSpaceActivity.this, "Area Deleted Successfully.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("EditSpace", "Error deleting document", e);
                            }
                        });

            }
        });*/



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SpaceInfoViewModel.class);
        // TODO: Use the ViewModel
    }

}
