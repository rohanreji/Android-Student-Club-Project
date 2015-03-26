package com.sensomate.travelr;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.plus.PlusShare;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends ActionBarActivity {

    // Google Map
    private GoogleMap googleMap;
    private ArrayList<MarkerOptions> markerArray = new ArrayList<MarkerOptions>();
    private ArrayList<Double> latitudeArray = new ArrayList<Double>();
    private ArrayList<Double> longitudeArray = new ArrayList<Double>();
    private ArrayList<String> noteArray = new ArrayList<String>();
    private ArrayList<Marker> mmarkerArray = new ArrayList<Marker>();
    private ArrayList<Notes> notesArray = new ArrayList<Notes>();
    boolean alert1=false;

    GPSTracker gps;
    double latitude;
    double longitude;
    double lati;
    double longi;
    int marker_count,marker_counter;
    EditText e;
    String cityName,countryName,stateName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_go = (Button) findViewById(R.id.btn_find);

        // Defining button click event listener for the find button
        View.OnClickListener findClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting reference to EditText to get the user input location
                EditText etLocation = (EditText) findViewById(R.id.location);

                // Getting user input location
                String location = etLocation.getText().toString();

                if(location!=null && !location.equals("")){
                    new GeocoderTask().execute(location);
                }
            }
        };

        // Setting button click event listener for the find button
        btn_go.setOnClickListener(findClickListener);
        DatabaseHandler db = new DatabaseHandler(this);

        List<Notes> contacts = db.getAllContacts();
        db.close();
        marker_counter=0;
        markerArray = new ArrayList<MarkerOptions>();
        latitudeArray = new ArrayList<Double>();
        longitudeArray = new ArrayList<Double>();
        noteArray = new ArrayList<String>();
        mmarkerArray = new ArrayList<Marker>();
        notesArray = new ArrayList<Notes>();
        for (Notes cn : contacts) {
            String log1 = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Latitude: " + cn.getLatitude()+ " ,Longitude: " + cn.getLongitude();
            Log.d("Name: ", log1);
            notesArray.add(cn);
            latitudeArray.add(cn.getLatitude());
            longitudeArray.add(cn.getLongitude());
            noteArray.add(cn.getName());
            MarkerOptions newMarkerOptions=new MarkerOptions().position(new LatLng(cn.getLatitude(), cn.getLongitude())).title("check-in")
                    .snippet(cn.getName());
            markerArray.add(newMarkerOptions);
            marker_counter++;
        }

        try {
            // Loading map
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
            else{
                gps = new GPSTracker(this);

                if(gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    Log.e("Latitude: ", latitude + "");
                    Log.e("Longitude: ", longitude + "");

                }
                else
                {
                    latitude=28.6139;
                    longitude=77.2090;
                }
                CameraPosition cameraPosition = new CameraPosition.Builder().target(
                        new LatLng(latitude, longitude)).zoom(17).build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                mmarkerArray = new ArrayList<Marker>();
                for(MarkerOptions m:markerArray){
                    mmarkerArray.add(googleMap.addMarker(m));

                }
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        for(int i=0;i<mmarkerArray.size();i++)
                        {
                            if(mmarkerArray.get(i).equals(marker))
                            {
                                final int u=i;
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<android.location.Address> addresses;
                                try {
                                    addresses = geocoder.getFromLocation(latitudeArray.get(i), longitudeArray.get(i), 1);
                                    cityName = addresses.get(0).getAddressLine(0);
                                    stateName = addresses.get(0).getAddressLine(1);
                                    countryName = addresses.get(0).getAddressLine(2);

                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
// Setting Dialog Title
                                alertDialog.setTitle(cityName);
// Setting Dialog Message
                                alertDialog.setMessage(noteArray.get(i));
// Setting Positive "Yes" Button
                                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int which) {
// Write your code here to invoke YES event
                                    }
                                });
// Setting Negative "NO" Button
                                alertDialog.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
// Write your code here to invoke NO event
                                        DatabaseHandler db = new DatabaseHandler(MainActivity.this);

                                        db.deleteContact(notesArray.get(u));
                                        mmarkerArray.get(u).setVisible(false);
                                        List<Notes> contacts = db.getAllContacts();
                                        db.close();
                                        marker_counter=0;
//                                        markerArray = new ArrayList<MarkerOptions>();
//                                        latitudeArray = new ArrayList<Double>();
//                                        longitudeArray = new ArrayList<Double>();
//                                        noteArray = new ArrayList<String>();
//                                        notesArray = new ArrayList<Notes>();
//
//                                        for (Notes cn : contacts) {
//                                            String log1 = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Latitude: " + cn.getLatitude()+ " ,Longitude: " + cn.getLongitude();
//                                            Log.d("Name: ", log1);
//                                            notesArray.add(cn);
//                                            latitudeArray.add(cn.getLatitude());
//                                            longitudeArray.add(cn.getLongitude());
//                                            noteArray.add(cn.getName());
//                                            MarkerOptions newMarkerOptions=new MarkerOptions().position(new LatLng(cn.getLatitude(), cn.getLongitude())).title("check-in")
//                                                    .snippet(cn.getName());
//                                            markerArray.add(newMarkerOptions);
//                                            marker_counter++;
//                                        }
                                        //initilizeMap();

                                        dialog.cancel();
                                    }
                                });
// Showing Alert Message
                                alertDialog.show();
                            }
//Toast.makeText(getApplicationContext(), s[i+1],Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onMapClick(LatLng point) {
                    // TODO Auto-generated method stub

                        lati=point.latitude;
                        longi=point.longitude;
                        if(haveNetworkConnection()) {
                            Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                            List<android.location.Address> addresses;
                            try {
                                addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                                cityName = addresses.get(0).getAddressLine(0);
                                stateName = addresses.get(0).getAddressLine(1);
                                countryName = addresses.get(0).getAddressLine(2);

                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                        AlertDialog.Builder alert=new AlertDialog.Builder(MainActivity.this);
                        alert.setTitle("Add note");
                        e = new EditText(MainActivity.this);
                        Button b= new Button(MainActivity.this);


//                        e.setSingleLine(false);
//                        e.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
//                        e.setHeight(500);
                        b.setText("share on g+");
                        ColorDrawable colorDrawable = new ColorDrawable(Color.RED);
                        b.setBackground(colorDrawable);
                        b.setTextColor(Color.WHITE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent shareIntent = new PlusShare.Builder(MainActivity.this)
                                        .setType("text/plain")
                                        .setText(e.getText() + "\n@"+cityName+ "\n-via TravelR")
                                .getIntent();
                                startActivityForResult(shareIntent, 0);

                            }
                        });
                        LinearLayout l=new LinearLayout(MainActivity.this);

                        l.setOrientation(LinearLayout.VERTICAL);
                        l.addView(e);
                        l.addView(b);

                        alert.setView(l);


                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                addnote();
                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
// Canceled.
                            }
                        });
                        alert.show();
                    }
                });


            }
        }
    }

    public void addnote(){

        String s=e.getText().toString().trim();

        Log.d("Insert: ", "Inserting ..");
        Context context = this;
        Geocoder gcd = new Geocoder(context, Locale.getDefault());

        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        List<android.location.Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lati, longi, 1);
            cityName = addresses.get(0).getAddressLine(0);
            stateName = addresses.get(0).getAddressLine(1);
            countryName = addresses.get(0).getAddressLine(2);

        } catch (IOException e1) {
            e1.printStackTrace();
        }


        DatabaseHandler db = new DatabaseHandler(this);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat formats = DateFormat.getDateInstance();


        db.addNote(new Notes(s, lati,longi,df.format(c.getTime())));

        Log.d("Reading: ", "Reading all contacts..");
        List<Notes> contacts = db.getAllContacts();
        marker_counter=0;
        for (Notes cn : contacts) {
            String log1 = "Id: "+cn.getID()+" ,Name: " + cn.getName() + " ,Latitude: " + cn.getLatitude()+ " ,Longitude: " + cn.getLac();
            Log.d("Name: ", log1);

            marker_counter++;
        }
        Toast.makeText(getApplicationContext(),marker_counter+" markers",Toast.LENGTH_SHORT).show();
        //adding marker there
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lati,longi)).title("check-in")
                .snippet(e.getText().toString());
        mmarkerArray.add(googleMap.addMarker(marker));
        latitudeArray.add(lati);
        longitudeArray.add(longi);
        noteArray.add(e.getText().toString());
        markerArray.add(marker);

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
//        TextView t=(TextView)findViewById(R.id.tracker);
//        if(isMyServiceRunning())
//            t.setText("stop tracking");
//        else
//            t.setText("start tracking");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
           Intent i = new Intent(MainActivity.this, ListingActivity.class);
           startActivity(i);
        }
        if(id==R.id.find) {
            checkin();
        }
        if(id==R.id.tracker) {
            trackme();
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    public void checkin()
    {
        GPSTracker gps = new GPSTracker(MainActivity.this);
        if(gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.d("1 "+latitude,"fwef");
            final DatabaseHandler db = new DatabaseHandler(this);
            int y=0;
            List<Notes> contacts = db.getAllContacts();
            for (Notes cn : contacts) {
                final Notes me=cn;
                double km=distance(cn.getLatitude(),cn.getLongitude(),latitude,longitude);
                if(km<0.1){
                    new AlertDialog.Builder(this)
                            .setTitle("Your check-in")
                            .setMessage(cn.getName())
                            .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })

                            .show();
                    y=1;
                }
            }
            if(y==0)
            {
                Toast.makeText(getApplicationContext(),"No Checkin", Toast.LENGTH_LONG).show();
                y=0;
            }

        }
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
    public void trackme()
    {
        if(!isMyServiceRunning()){
            Intent i= new Intent(getApplicationContext(), MyService.class);
// potentially add data to the intent
            i.putExtra("KEY1", "Value to be used by the service");
            this.startService(i);
            finish();
        }
        else
        {
            stopService(new Intent(MainActivity.this, MyService.class));
        }
    }
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private class GeocoderTask extends AsyncTask<String, Void, List<android.location.Address>> {

        @Override
        protected List<android.location.Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            if(haveNetworkConnection()) {
                Geocoder geocoder = new Geocoder(getBaseContext());
                List<android.location.Address> addresses = null;

                try {
                    // Getting a maximum of 3 Address that matches the input text
                    addresses = geocoder.getFromLocationName(locationName[0], 3);
                    Log.e("jk","jk");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return addresses;
            }
            else
                return null;
        }

        @Override
        protected void onPostExecute(List<android.location.Address> addresses) {

            if(addresses==null || addresses.size()==0){
                Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
            }

            // Clears all the existing markers on the map
         //   googleMap.clear();

            // Adding Markers on Google Map for each matching address
            for(int i=0;i<addresses.size();i++){

                android.location.Address address = (android.location.Address) addresses.get(i);

                // Creating an instance of GeoPoint, to display in Google Map
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                String addressText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getCountryName());


                if(i==0)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }
    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}