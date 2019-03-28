package com.example.studyspacesosu;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
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
import java.util.Map;




public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FilterFragment.OnFilterUpdateListener {

    private MapsFragment mMapFrag;

    @Override
    public void onAttachFragment(Fragment fragment) {
            if (fragment instanceof FilterFragment) {
            FilterFragment filterFragment = (FilterFragment) fragment;
            filterFragment.setFilterUpdateListener(this);
            }
    }

    public void onDistanceSelected(float distance) {
        Log.i("Main Got search results", "Got distance" + distance);
        SupportMapFragment mapFragment = new MapsFragment();
        ((MapsFragment)mapFragment).setFilterDistance(distance);
        getSupportFragmentManager().beginTransaction().replace(R.id.mapFrame, mapFragment).commit();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Main OnCreate", "Main Activity OnCreate called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        
        SupportMapFragment mapFragment = new MapsFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.mapFrame, mapFragment).commit();
        mMapFrag = (MapsFragment) mapFragment;


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
            SupportMapFragment mapFragment = new MapsFragment();
            final float distance = (float) intent.getSerializableExtra("distance");
            ((MapsFragment)mapFragment).setFilterDistance(distance);
            getSupportFragmentManager().beginTransaction().replace(R.id.mapFrame, mapFragment).commit();

        }


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


        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Log.i("Main OnNavItemSelect", "Main Activity OnNavigationItemSelected called");

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
