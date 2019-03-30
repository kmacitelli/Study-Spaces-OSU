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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;
import com.google.firebase.firestore.util.ExecutorEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class FavoritesFragment extends Fragment {

    private FirebaseFirestore mDatabase;
    static private Button editButton;
    private Button xButton;
    private ListView mList;
    private List<String> mPositions;
    private List<String> mNames;

    private SpaceInfoViewModel mViewModel;
    private ViewGroup mContainer;
    private Fragment mFragment;

    View favoritesView;

    Intent editIntent;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.i("Info", "Space info onCreateView called");

        super.onCreateView(inflater, container, savedInstanceState);

        mDatabase = FirebaseFirestore.getInstance();
        mContainer = container;
        favoritesView = inflater.inflate(R.layout.favorites_fragment, container, false);

        xButton = favoritesView.findViewById(R.id.infoExitButton);

        mFragment = this;

        return favoritesView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        Log.i("Info", "Space info onViewCreated called");

        super.onViewCreated(view, savedInstanceState);

        xButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().remove(mFragment).commit();
            }
        });

        mList = (ListView) view.findViewById(R.id.favorites_list);

        mDatabase.collection("user").whereEqualTo("username", FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    List<String> favoritesList = null;

                    List<DocumentSnapshot> docs = task.getResult().getDocuments();
                    favoritesList = (List<String>) docs.get(0).get("Favorites");

                    mPositions = favoritesList;

                    mDatabase.collection("study area").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            mNames = new ArrayList<>();
                            mNames.addAll(mPositions);
                            List<DocumentSnapshot> doc = task.getResult().getDocuments();
                            Iterator<DocumentSnapshot> docIter = doc.iterator();
                            while(docIter.hasNext()) {
                                DocumentSnapshot currentDoc = docIter.next();
                                if(mPositions.contains(currentDoc.getId())) {
                                    int index = mPositions.indexOf(currentDoc.getId());
                                    mNames.set(index, (String) currentDoc.get("Name"));
                                }
                            }
                            mList.setAdapter(new ArrayAdapter<>(getContext(), R.layout.favorite_list_item, mNames));
                        }
                    });
                }
            }
        });


        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mDatabase.collection("study area").whereEqualTo(FieldPath.documentId(), mPositions.get(position)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        HashMap<String, Object> markerData = new HashMap<>();

                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);

                        markerData.put("Name", doc.get("Name"));
                        markerData.put("Description", doc.get("Description"));

                        GeoPoint location = (GeoPoint) doc.get("Coordinates");
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        LatLng pos = new LatLng(lat, lng);

                        markerData.put("Coordinates", pos);
                        markerData.put("Id", doc.getId());

                        final Intent intent = new Intent();
                        intent.putExtra("DataMap", markerData);
                        intent.setClass(getContext(), EditSpaceActivity.class);

                        FragmentManager fm = getFragmentManager();
                        SpaceInfoFragment spaceFragment = new SpaceInfoFragment();
                        fm.beginTransaction().add(R.id.infoSpace, spaceFragment).commit();

                        spaceFragment.setEditIntent(intent);

                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO: Update favorites list if changed from opening a fragment on the favorites page.
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SpaceInfoViewModel.class);
        // TODO: Use the ViewModel
    }

}
