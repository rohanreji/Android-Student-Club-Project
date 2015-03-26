package com.sensomate.travelr;

/**
 * Created by rohan on 16/3/15.
 */
public class Notes {
    //private variables
    int _id;
    String _name;
    double _latitude;
    double _longitude;
    String _lac;

    // Empty constructor
    public Notes(){
    }
    // constructor
    public Notes(int id, String name, double latitudes,double longitudes,String lac){
        this._id = id;
        this._name = name;
        this._latitude = latitudes;
        this._longitude=longitudes;
        this._lac=lac;

    }
    // constructor
    public Notes(String name, double latitudes,double longitudes,String lac){
        this._name = name;
        this._latitude = latitudes;
        this._longitude=longitudes;
        this._lac=lac;

    }
    // getting ID
    public int getID(){
        return this._id;
    }
    // setting id
    public void setID(int id){
        this._id = id;
    }
    // getting name
    public String getName(){
        return this._name;
    }
    // setting name
    public void setName(String name){
        this._name = name;
    }
    // getting phone number
    public double getLatitude(){
        return this._latitude;
    }
    // setting phone number
    public void setLatitude(double latitudes){
        this._latitude =latitudes;
    }
    public double getLongitude(){
        return this._longitude;
    }
    public void setLongitude(double longitudes){
        this._longitude =longitudes;
    }
    public String getLac(){
        return this._lac;
    }
    public void setLac(String lac){
        this._lac =lac;
    }

}