package com.example.studyspacesosu;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FilterFragment extends Fragment {


    private TextView mAreaName;
    private TextView mAreaDescription;
    private FirebaseFirestore mDatabase;
    private Button xButton;

    private SpaceInfoViewModel mViewModel;
    private ViewGroup mContainer;
    private Fragment mFragment;
    private Spinner mDistanceSpinner;

    View filterView;

    Intent editIntent;

    OnFilterUpdateListener callback;

    public void setFilterUpdateListener(OnFilterUpdateListener callback) {
        this.callback = callback;
    }

    public interface OnFilterUpdateListener {
        public void onDistanceSelected(float distance);
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i("Info", "Space info onCreateView called");

        super.onCreateView(inflater, container, savedInstanceState);

        mDatabase = FirebaseFirestore.getInstance();
        mContainer = container;
        filterView = inflater.inflate(R.layout.filter, container, false);

        xButton = filterView.findViewById(R.id.filterExitButton);

        Spinner distanceSpinner = (Spinner) filterView.findViewById(R.id.distance_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.distances_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        distanceSpinner.setAdapter(adapter);
        mDistanceSpinner = distanceSpinner;


        //Intent callingIntent = editIntent;
        //final Map<String, Object> markerData = (HashMap<String, Object>) callingIntent.getSerializableExtra("DataMap");




        mFragment = this;

        return filterView;

    }

    public void setEditIntent(Intent mIntent){
        editIntent = mIntent;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        Log.i("Info", "Space info onViewCreated called");

        super.onViewCreated(view, savedInstanceState);

        xButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                String distance = mDistanceSpinner.getSelectedItem().toString();
                float distanceFloat = Float.parseFloat(distance.substring(0, 4));


                callback.onDistanceSelected(distanceFloat);

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
