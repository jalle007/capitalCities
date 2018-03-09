package com.example.jasko.capitalcities;

import android.media.MediaPlayer;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

    private EditText txtCitiesPlaced;
    private EditText txtkmLeft;
    private EditText txtNextCity;
    private Button btPlace;

    ArrayList<Location> locations;
    ArrayList<LocationDistance> answers ;
    LatLng curPoint;
    MediaPlayer mp;
    int totalNum;
    int index  = 0 ;
    int totalDistance = 1500;
    boolean isGameOver  =false;

    Location curLocation;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //init views
        txtCitiesPlaced = (EditText) findViewById(R.id.txtCitiesPlaced);
        txtkmLeft = (EditText) findViewById(R.id.txtkmLeft);
        txtNextCity = (EditText) findViewById(R.id.txtNextCity);
        btPlace = (Button) findViewById(R.id.btPlace);

        //load locations from JSON file
        locations = LoadJSON();
        totalNum = locations.size();
        answers = new ArrayList<LocationDistance>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // initialize player
        mp = MediaPlayer.create(this, R.raw.bell);
    }

    public void drawUI()
    {
        location = locations.get(index);
        txtCitiesPlaced.setText(index + "/" + totalNum + " cities placed");

        txtkmLeft.setText(totalDistance + " kilemeters left");

        txtNextCity.setText("Select the location of \n'" + location.getCity() + "'");
        mMap.clear();
    }

    public void btnPlace(View v)
    {
        //stop sound if already playing
        mp.stop();
        location = locations.get(index);

        double distance =Utils.getDistance(location.getLat(), curPoint.latitude, location.getLong(), curPoint.longitude, 0, 0);
        distance = Math.round(distance);

        LocationDistance answer = new LocationDistance() ;
        answer.setCity(location.getCity());
        answer.setLat((float)curPoint.latitude);
        answer.setLong((float)curPoint.longitude);
        answer.setDistance(distance);
        answers.add(answer);

        //decrease total score
        totalDistance -= distance;

        //play sound
        mp.start();

        // if those 2 conditions are met, finish the game
        if(totalDistance < 0 || index == totalNum)
        {
            FinishGame();
        }else    {
            // show next question
            index+=1;
            drawUI();
        }
    }

    public void FinishGame()
    {
        mMap.clear();

        //display all correct and user locations.
        int totalAnswers = answers.size();
        for (int index = 0; index < totalAnswers; index++) {
            //correct answer- add green marker
            LatLng P1 = new LatLng(locations.get(index).getLat(), locations.get(index).getLong());
            mMap.addMarker(new MarkerOptions().position(P1)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            );

            //user answer- add red marker
            LatLng P2 = new LatLng(answers.get(index).getLat(), answers.get(index).getLong());
            mMap.addMarker(new MarkerOptions().position(P2)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );
        }

      /* Zoom to Europe ?
        LatLng dor24 = new  LatLng(52.519325, 13.392709);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(dor24));*/

        btPlace.setText("GAME OVER");
        btPlace.setEnabled(false);
        isGameOver=true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
       //We apply custom map style so only country borders are visible
        //got it from here : https://mapstyle.withgoogle.com
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        drawUI();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if(!isGameOver)
                {curPoint=point;
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(curPoint));
                }
            }
        });
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
