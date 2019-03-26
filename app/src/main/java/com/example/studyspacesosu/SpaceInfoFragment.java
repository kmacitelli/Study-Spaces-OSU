package com.example.studyspacesosu;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SpaceInfoFragment extends Fragment {

    private TextView mAreaName;
    private TextView mAreaDescription;
    private FirebaseFirestore mDatabase;
    static private Button editButton;
    private Button xButton;

    private SpaceInfoViewModel mViewModel;
    private ViewGroup mContainer;
    private Fragment mFragment;

    View editSpaceView;

    Intent editIntent;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.i("Info", "Space info onCreateView called");

        super.onCreateView(inflater, container, savedInstanceState);

        mDatabase = FirebaseFirestore.getInstance();
        mContainer = container;
        editSpaceView = inflater.inflate(R.layout.space_info_fragment, container, false);

        editButton = editSpaceView.findViewById(R.id.editButton);
        xButton = editSpaceView.findViewById(R.id.infoExitButton);


        Intent callingIntent = editIntent;
        final Map<String, Object> markerData = (HashMap<String, Object>) callingIntent.getSerializableExtra("DataMap");

        mAreaName = editSpaceView.findViewById(R.id.spaceTitle);
        mAreaName.setText((String) markerData.get("Name"));
        mAreaDescription = editSpaceView.findViewById(R.id.spaceDescription);
        mAreaDescription.setText((String)markerData.get("Description"));

        mFragment = this;

        return editSpaceView;

    }

    public void setEditIntent(Intent mIntent){
        editIntent = mIntent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        Log.i("Info", "Space info onViewCreated called");

        super.onViewCreated(view, savedInstanceState);

        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(editIntent);
            }
        });

        xButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().remove(mFragment).commit();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SpaceInfoViewModel.class);
        // TODO: Use the ViewModel
    }

}
