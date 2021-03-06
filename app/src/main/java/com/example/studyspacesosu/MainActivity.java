package com.example.studyspacesosu;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.SupportMapFragment;
import java.util.HashMap;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;




public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FilterFragment.OnFilterUpdateListener {

    private int mMapID;
    private NavigationView mNavView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private MapsFragment mMapFrag;

    static final String STORE_MAPFRAG = "MapFragment1";

    @Override
    public void onAttachFragment(Fragment fragment) {
            if (fragment instanceof FilterFragment) {
            FilterFragment filterFragment = (FilterFragment) fragment;
            filterFragment.setFilterUpdateListener(this);
            }
    }

    public void onDistanceSelected(float distance) {
        Log.i("Main Got search results", "Got distance" + distance);
        getSupportFragmentManager().beginTransaction().remove(mMapFrag).commit();
        mMapFrag = new MapsFragment();
        mMapFrag.setFilterDistance(distance);
        getSupportFragmentManager().beginTransaction().add(R.id.mapFrame, mMapFrag, "Map_Frag_Tag").commit();
        mMapID = mMapFrag.getId();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Main OnCreate", "Main Activity OnCreate called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavView = (NavigationView) findViewById(R.id.nav_view);
        mNavView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null) {
            mMapFrag = new MapsFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.mapFrame, mMapFrag, "Map_Frag_Tag").commit();
            mMapID = mMapFrag.getId();
        } else {
            String mapTag = savedInstanceState.getString(STORE_MAPFRAG);
            mMapFrag = (MapsFragment) getSupportFragmentManager().findFragmentByTag(mapTag);
            mMapID = mMapFrag.getId();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Intent intent = getIntent();
        if (intent.hasExtra("DataMap")) {
            // Launched from search results

            Map<String, Object> markerData = (HashMap<String, Object>) intent.getSerializableExtra("DataMap");


            Log.i("Main Got search results", "Got results" + markerData);

            final Intent infoIntent = new Intent();
            infoIntent.putExtra("DataMap", (HashMap) markerData);
            infoIntent.setClass(getApplicationContext(), EditSpaceActivity.class);


            FragmentManager fm = getSupportFragmentManager();

            SpaceInfoFragment spaceFragment = new SpaceInfoFragment();
            spaceFragment.setEditIntent(infoIntent);

            fm.beginTransaction().add(R.id.infoSpace, spaceFragment).commit();

        }
        else if (intent.hasExtra("distance")){
            Log.i("Main Got search results", "Got distance" + intent.getSerializableExtra("distance"));
            getSupportFragmentManager().beginTransaction().remove(mMapFrag).commit();
            mMapFrag = new MapsFragment();
            final float distance = (float) intent.getSerializableExtra("distance");
            mMapFrag.setFilterDistance(distance);
            getSupportFragmentManager().beginTransaction().add(R.id.mapFrame, mMapFrag, "Map_Frag_Tag").commit();

        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        Log.i("Main OnBackPressed", "Main Activity OnBackPressed called");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i("MainOnCreateOptionsMenu", "Main Activity OnCreateOptionsMenu called");

        getMenuInflater().inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        // Inflate the menu; this adds items to the action bar if it is present.


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.i("MainOnOptionsItemSelect", "Main Activity OnOptionsItemsSelected called");

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.filterButton){
            Log.i("MainOnOptionsItemSelect", "Filter Button Clicked!");

            final Intent intent = new Intent();
            //intent.putExtra("DataMap", markerData);
            //intent.setClass(getContext(), EditSpaceActivity.class);

            FragmentManager fm = getSupportFragmentManager();
            FilterFragment filterFragment = new FilterFragment();
            fm.beginTransaction().add(R.id.infoSpace, filterFragment).commit();
        }
        else if (id == R.id.menu_showcurrentlocation){
            Log.i("MainOnOptionsItemSelect", "Location Button Clicked!");
            mMapFrag.findLocation();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Log.i("Main OnNavItemSelect", "Main Activity OnNavigationItemSelected called");

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorites) {

            FragmentManager fm = getSupportFragmentManager();
            FavoritesFragment favoritesFragment = new FavoritesFragment();
            fm.beginTransaction().add(R.id.infoSpace, favoritesFragment).commit();

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if(mMapFrag != null) {
            savedInstanceState.putString(STORE_MAPFRAG, mMapFrag.getTag());
        }

        super.onSaveInstanceState(savedInstanceState);
    }

    public void refreshMap() {
        getSupportFragmentManager().beginTransaction().remove(mMapFrag).commit();
        mMapFrag = new MapsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mapFrame, mMapFrag, "Map_Frag_Tag").commit();
        mMapID = mMapFrag.getId();
    }
}
