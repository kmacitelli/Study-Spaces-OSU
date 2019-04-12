package com.example.studyspacesosu;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SpaceInfoFragment extends Fragment {

    private TextView mAreaName;
    private TextView mAreaDescription;
    private TextView mDownVotes;
    private TextView mUpVotes;
    private FirebaseFirestore mDatabase;
    static private Button editButton;
    private Button xButton;
    private Button favoriteButton;
    private Button downButton;
    private Button upButton;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Map<String,Object> markerData;
    private SpaceInfoViewModel mViewModel;
    private ViewGroup mContainer;
    private Fragment mFragment;
    private Context mContext;
    private Boolean mMapNeedsUpdate = false;

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
        favoriteButton = editSpaceView.findViewById(R.id.favoriteButton);
        downButton = editSpaceView.findViewById(R.id.downvoteButton);
        upButton = editSpaceView.findViewById(R.id.upvoteButton);
        mDownVotes = editSpaceView.findViewById(R.id.downvotes);
        mUpVotes = editSpaceView.findViewById(R.id.upvotes);
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        Intent callingIntent = editIntent;
        markerData = (HashMap<String, Object>) callingIntent.getSerializableExtra("DataMap");

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

        mDatabase.collection("study area").document((String) markerData.get("Id")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot docs = task.getResult();
                mUpVotes.setText(docs.get("up rating").toString());
                mDownVotes.setText(docs.get("down rating").toString());
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(editIntent, 43);
                //exitFragment();
            }
        });

        xButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){

                if (mMapNeedsUpdate) {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.refreshMap();
                }
                exitFragment();
            }
        });
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Favorite();
            }
        });
        mDatabase.collection("user").whereEqualTo("username", mUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> docs= task.getResult().getDocuments();
                DocumentReference doc = mDatabase.collection("user").document(docs.get(0).getId());
                List<Object> votes = (List<Object>) docs.get(0).get("Voted");
                if(votes.contains(markerData.get("Id"))){
                    downButton.setVisibility(View.GONE);
                    upButton.setVisibility(View.GONE);
                }
                else{
                    downButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Down();
                        }
                    });
                    upButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Up();
                        }
                    });
                }
            }
        });

        checkFavorites();

    }

    private void Favorite(){
        mContext = getContext();
        mDatabase.collection("user").whereEqualTo("username", mUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> docs= task.getResult().getDocuments();
                DocumentReference doc = mDatabase.collection("user").document(docs.get(0).getId());
                List<Object> favs = (List<Object>) docs.get(0).get("Favorites");
                favoriteButton = editSpaceView.findViewById(R.id.favoriteButton);
                if(favs.size()<10){
                    if(favs.contains(markerData.get("Id"))){
                        favs.remove(markerData.get("Id"));
                        doc.update("Favorites", favs);
                        favoriteButton.setBackgroundResource(R.drawable.heart_unfilled);
                        Toast.makeText(mContext, "Successfully removed " + markerData.get("Name") + " from favorites list.", Toast.LENGTH_LONG).show();
                    }
                    else if(favs.size()<9){
                        favs.add(markerData.get("Id"));
                        doc.update("Favorites", favs);
                        favoriteButton.setBackgroundResource(R.drawable.heart_filled);
                        Toast.makeText(mContext, "Successfully added " + markerData.get("Name") + " to favorites list.", Toast.LENGTH_LONG).show();
                    }

                }

            }
        });
    }

    private void checkFavorites(){
        mContext = getContext();
        mDatabase.collection("user").whereEqualTo("username", mUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> docs= task.getResult().getDocuments();
                boolean result;
                DocumentReference doc = mDatabase.collection("user").document(docs.get(0).getId());
                List<Object> favs = (List<Object>) docs.get(0).get("Favorites");
                favoriteButton = editSpaceView.findViewById(R.id.favoriteButton);
                if(favs.size()<10){
                    if(favs.contains(markerData.get("Id"))){
                        favoriteButton.setBackgroundResource(R.drawable.heart_filled);
                    }
                    else if(favs.size()<9){
                        favoriteButton.setBackgroundResource(R.drawable.heart_unfilled);
                    }

                }

            }
        });


    }

    private void Up(){
        mContext = getContext();
        mDatabase.collection("study area").document((String) markerData.get("Id")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot docs = task.getResult();
                DocumentReference doc = mDatabase.collection("study area").document(docs.getId());
                int rating = Integer.parseInt(docs.get("up rating").toString())+1;
                doc.update("up rating", rating);
                mUpVotes.setText("" + rating);
                mDatabase.collection("user").whereEqualTo("username", mUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> docs = task.getResult().getDocuments();
                        DocumentReference doc = mDatabase.collection("user").document(docs.get(0).getId());
                        List<Object> votes = (List<Object>) docs.get(0).get("Voted");
                        votes.add(markerData.get("Id"));
                        doc.update("Voted", votes);
                        Toast.makeText(mContext, "Successfully up-voted " + markerData.get("Name") + ".", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        downButton.setVisibility(View.GONE);
        upButton.setVisibility(View.GONE);
    }
    private void Down(){
        mContext = getContext();
        mDatabase.collection("study area").document((String) markerData.get("Id")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot docs = task.getResult();
                DocumentReference doc = mDatabase.collection("study area").document(docs.getId());
                int rating =  Integer.parseInt(docs.get("down rating").toString())+1;
                doc.update("down rating", rating);
                mDownVotes.setText("" + rating);
                mDatabase.collection("user").whereEqualTo("username", mUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> docs= task.getResult().getDocuments();
                        DocumentReference doc = mDatabase.collection("user").document(docs.get(0).getId());
                        List<Object> votes = (List<Object>) docs.get(0).get("Voted");
                        votes.add(markerData.get("Id"));
                        doc.update("Voted", votes);
                        Toast.makeText(mContext, "Successfully down-voted " + markerData.get("Name") + ".", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        downButton.setVisibility(View.GONE);
        upButton.setVisibility(View.GONE);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SpaceInfoViewModel.class);
        // TODO: Use the ViewModel
    }

    private void exitFragment() {
        getFragmentManager().beginTransaction().remove(this).commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 43) {
            if (resultCode == 46) {
                ((MainActivity) getActivity()).refreshMap();
                exitFragment();
            } else if (resultCode == 45) {
                Map<String, Object> updatedFields = (HashMap) data.getSerializableExtra("DataMap");

                mAreaName.setText(updatedFields.get("Name").toString());
                mAreaDescription.setText(updatedFields.get("Description").toString());

                setEditIntent(data);
                mMapNeedsUpdate = true;
            }
        }
    }
}

