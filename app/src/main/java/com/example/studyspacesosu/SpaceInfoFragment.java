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

        mDatabase.collection("study area").whereEqualTo("Name",(String) markerData.get("Name")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> docs= task.getResult().getDocuments();
                mUpVotes.setText(docs.get(0).getString("up rating"));
                mDownVotes.setText(docs.get(0).getString("down rating"));
            }
        });

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
                if(votes.contains(markerData.get("Name"))){
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

    }

    private void Favorite(){
        mDatabase.collection("user").whereEqualTo("username", mUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> docs= task.getResult().getDocuments();
                DocumentReference doc = mDatabase.collection("user").document(docs.get(0).getId());
                List<Object> favs = (List<Object>) docs.get(0).get("Favorites");
                if(favs.size()<10){
                    if(favs.contains(markerData.get("Name"))){
                        favs.remove(markerData.get("Name"));
                        doc.update("Favorites", favs);
                    }
                    else if(favs.size()<9){
                        favs.add(markerData.get("Name"));
                        doc.update("Favorites", favs);
                    }

                }

            }
        });
    }
    private void Up(){
        mDatabase.collection("study area").whereEqualTo("Name", markerData.get("Name")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> docs= task.getResult().getDocuments();
                DocumentReference doc = mDatabase.collection("study area").document(docs.get(0).getId());
                int rating = (int) docs.get(0).get("up rating")+1;
                doc.update("up rating", rating);
                mUpVotes.setText(rating);
                mDatabase.collection("user").whereEqualTo("username", mUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> docs= task.getResult().getDocuments();
                        DocumentReference doc = mDatabase.collection("user").document(docs.get(0).getId());
                        List<Object> votes = (List<Object>) docs.get(0).get("Voted");
                        votes.add(markerData.get("Name"));
                        doc.update("Voted", votes);
                    }
                });
            }
        });
    }
    private void Down(){
        mDatabase.collection("study area").whereEqualTo("Name", markerData.get("Name")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<DocumentSnapshot> docs= task.getResult().getDocuments();
                DocumentReference doc = mDatabase.collection("study area").document(docs.get(0).getId());
                int rating = (int) docs.get(0).get("down rating")+1;
                doc.update("down rating", rating);
                mDownVotes.setText(rating);
                mDatabase.collection("user").whereEqualTo("username", mUser.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<DocumentSnapshot> docs= task.getResult().getDocuments();
                        DocumentReference doc = mDatabase.collection("user").document(docs.get(0).getId());
                        List<Object> votes = (List<Object>) docs.get(0).get("Voted");
                        votes.add(markerData.get("Name"));
                        doc.update("Voted", votes);
                    }
                });
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
