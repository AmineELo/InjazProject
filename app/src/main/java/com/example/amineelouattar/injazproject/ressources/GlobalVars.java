package com.example.amineelouattar.injazproject.ressources;

import com.example.amineelouattar.injazproject.MainActivity;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by AmineElouattar on 3/9/16.
 */
public class GlobalVars
{
    public static final String URL = "http://192.168.42.227/geolocalisation/";

    public static LatLng myLocation = null;

    //Declare an arraylist of markersmap
    public static ArrayList<MarkerOptions> arraymarkers = new ArrayList<MarkerOptions>();
    public static ArrayList<String> departarrive = new ArrayList<String>();
    public static ArrayList<MarkerOptions> userposition = new ArrayList<MarkerOptions>();

}
