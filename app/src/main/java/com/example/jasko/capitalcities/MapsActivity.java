package com.example.jasko.capitalcities;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public ArrayList<Location> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locations = LoadJSON();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned tzuricho the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //boolean success = mMap.setMapStyle();
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        boolean success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));


        // Add a marker in Sydney and move the camera
        LatLng zurich = new LatLng(47.3769, 8.5417);
        mMap.addMarker(new MarkerOptions().position(zurich).title("Marker in zurich"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(zurich));
    }

    //Load cities from JSON
    public ArrayList<Location> LoadJSON() {
        ArrayList<Location> locList = new ArrayList<>();
        String json = null;
        try {
            InputStream is = getAssets().open("capital_cities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        try {
            JSONObject obj = new JSONObject(json);
            JSONArray m_jArry = obj.getJSONArray("capitalCities");

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                Location location = new Location();
                location.setCity(jo_inside.getString("capitalCity"));
                location.setLat(Float.parseFloat( jo_inside.getString("lat")));
                location.setLong(Float.parseFloat( jo_inside.getString("long")));

                //Add your values in your `ArrayList` as below:
                locList.add(location);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locList;
    }
}
