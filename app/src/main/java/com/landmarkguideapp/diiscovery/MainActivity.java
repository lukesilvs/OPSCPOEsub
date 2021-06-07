package com.landmarkguideapp.diiscovery;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {



    //incorporate spinner?
    Spinner spType;
    boolean isPermissionGranted;
    FloatingActionButton fab;
    private FusedLocationProviderClient mLocationClient;
    GoogleMap mGoogleMap;
    private int GPS_REQUEST_CODE=9001;

    private double lat;
    private double lng;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fab=findViewById(R.id.fab);
        spType= findViewById(R.id.sp_type);

        checkMyPermission();

        initMap();

        //Initialise array of place type
        String[] placeTypeList= {"atm","bank","hospital","movie_theater","restaurant"};
        //initialise array of place name
        String[] placeNameList={"ATM","Bank","Hospital","Movie Theater", "Restaurant"};
        //5:49 in yt video

        //set adapter on spinner
        spType.setAdapter(new ArrayAdapter<>(MainActivity.this
                , android.R.layout.simple_spinner_dropdown_item,placeNameList));

        mLocationClient= new FusedLocationProviderClient(this);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();

                int i= spType.getSelectedItemPosition();
                //initialise URL
                String url= "https://maps.googleapis.com/maps/api/place/nearbysearch/json"+"?location="+"38.8951"+","+"-77.0364"+//might throw error
                        "&radius=5000"+
                        "&types="+placeTypeList[i]+
                        "&sensor=true"+
                        "&key=" + getResources().getString(R.string.google_map_key);

                //execute place task method to download json data
                //new PlaceTask().execute(url);


            }
        });










    }

    private void initMap() {
        if(isPermissionGranted){
            if(isGPSenable()){
                SupportMapFragment supportMapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
                supportMapFragment.getMapAsync(this);
            }

        }
    }

    private boolean isGPSenable(){
        LocationManager locationManager= (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean providerEnable= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(providerEnable){
            return true;
        }else{

            //alert box will pop up and redirect to settings app to enable GPS manually

           /* AlertDialog alertDialog=new AlertDialog.Builder(this)
                    .setTitle("GPS Permission")
                    .setMessage("GPS perimssions are required for this App to work. Please enable GPS permissions")
                    .setPositiveButton("Yes",((dialogInterface, i) -> {

                        Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, GPS_REQUEST_CODE);

                    }))
                    .setCancelable(false)
                    .show();*/
            //start activity for result is depreciated!!

        }

        return false;
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(){
        mLocationClient.getLastLocation().addOnCompleteListener(task -> {

            if(task.isSuccessful()){
                Location location= task.getResult();
                goToLocation(location.getLatitude(),location.getLongitude());
                lat= location.getLatitude();
                lng= location.getLongitude();
            }
        });
    }

    private void goToLocation(double latitude, double longitude) {
        LatLng LatLng= new LatLng(latitude,longitude);

        CameraUpdate cameraUpdate= CameraUpdateFactory.newLatLngZoom(LatLng,18);
        mGoogleMap.moveCamera(cameraUpdate);
        // mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void checkMyPermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                Toast.makeText(MainActivity.this,"Permission has been granted", Toast.LENGTH_SHORT).show();
                isPermissionGranted=true;

            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent=new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri= Uri.fromParts("package",getPackageName(),"");
                intent.setData(uri);
                startActivity(intent);

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }




    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mGoogleMap=googleMap;
        mGoogleMap.setMyLocationEnabled(true);
    }


    private class PlaceTask extends AsyncTask<String, Integer, String> {
        //async task depreciated
        @Override
        protected String doInBackground(String... strings) {
            String data=null;
            try {
                data= downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }
        @Override
        protected void onPostExecute(String s){
            //execute parser task
            new ParserTask().execute(s);

        }
    }

    private String downloadUrl(String string) throws IOException {
        //initialize url
        URL url =new URL(string);

        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
        //connect connection
        connection.connect();
        //initialize input stream
        InputStream stream= connection.getInputStream();
        //initilaize buffer reader
        BufferedReader reader =new BufferedReader(new InputStreamReader(stream));
        //initialise string builder
        StringBuilder builder = new StringBuilder();
        //initialize string variable
        String line="";
        //use while loop
        while((line=reader.readLine())!=null){
            builder.append(line);
        }
        //get append data
        String data= builder.toString();
        //close reader
        reader.close();

        return data;


    }


    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //create json parser class
            JsonParser jsonParser= new JsonParser();

            List<HashMap<String,String>> mapList=null;
            JSONObject object= null;

            mapList=jsonParser.parseResult(object);

            try {
                object= new JSONObject(strings[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            mGoogleMap.clear();

            for (int i=9; i<hashMaps.size();i++){

                HashMap<String,String> hashMapList= hashMaps.get(i);

                double lat1= Double.parseDouble(hashMapList.get("lat"));

                double lng1= Double.parseDouble(hashMapList.get("lng"));

                String namee= hashMapList.get("name");

                LatLng latLng= new LatLng(lat1,lng1);

                MarkerOptions options= new MarkerOptions();

                options.position(latLng);

                options.title(namee);

                mGoogleMap.addMarker(options);






            }
        }
    }
}