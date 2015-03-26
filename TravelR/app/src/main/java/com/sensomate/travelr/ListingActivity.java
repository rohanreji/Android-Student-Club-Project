package com.sensomate.travelr;

import android.app.Activity;
import android.content.res.Resources;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by rohan on 16/3/15.
 */

public class ListingActivity extends Activity {
    String cityName;
    ListView list;
    CardAdapter adapter;
    public  ArrayList<CardInfo> CustomListViewValuesArr = new ArrayList<CardInfo>();
    public  ListingActivity CustomListView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing_activity);
        CustomListView = this;


        DatabaseHandler db = new DatabaseHandler(this);
        ArrayList<CardInfo> cards= new ArrayList<CardInfo>();
        Log.d("Reading: ", "Reading all contacts..");
        List<Notes> contacts = db.getAllContacts();
        db.close();

        for (Notes cn : contacts) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<android.location.Address> addresses;
            try {
                addresses = geocoder.getFromLocation(cn.getLatitude(), cn.getLongitude(), 1);
                cityName = addresses.get(0).getAddressLine(0);


                cards.add(new CardInfo(cityName, cn.getName(), cn.getLac()));

                Log.e("jj",cn.getLac()+" ");

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            CustomListViewValuesArr=cards;

        }



        Resources res =getResources();
        list= (ListView)findViewById( R.id.list );  // List defined in XML ( See Below )

        /**************** Create Custom Adapter *********/
        adapter=new CardAdapter( CustomListView, CustomListViewValuesArr,res );
        list.setAdapter( adapter );

    }
}
