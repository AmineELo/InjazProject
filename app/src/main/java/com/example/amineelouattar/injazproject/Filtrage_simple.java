package com.example.amineelouattar.injazproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.amineelouattar.injazproject.ressources.GlobalVars;
import com.example.amineelouattar.injazproject.ressources.Global_function;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.util.ArrayList;

public class Filtrage_simple extends AppCompatActivity implements AdapterView.OnItemSelectedListener,OnMapReadyCallback
{
    private GoogleMap googleMap;

    private Spinner type_filtrage,sous_choix_filtrage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtrage_simple);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        //Spinner
        type_filtrage = (Spinner)findViewById(R.id.type_filtrage);
        sous_choix_filtrage = (Spinner)findViewById(R.id.choix_filtrage);

        //Adapter par defaut
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Filtrage, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        type_filtrage.setAdapter(adapter);

        // set on item click listner on type_filtrage
        type_filtrage.setOnItemSelectedListener(this);

        // set on item click listner on choix_filtrage
        sous_choix_filtrage.setOnItemSelectedListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        //Define the type of spinner clicked
        Spinner spinner = (Spinner) parent;

        //Adapter
        ArrayAdapter<CharSequence> sous_choix = null;


        if(spinner.getId() == R.id.type_filtrage)
        {
            /*--------------Type_filtrage ------------------*/
            //Choix (Distance,Prix,Plats,Vile,Cuisine)
            String choix_user = parent.getItemAtPosition(position).toString();

            switch (choix_user)
            {
                case "Cuisine":
                    sous_choix = ArrayAdapter.createFromResource(this, R.array.Cuisine, android.R.layout.simple_spinner_item);
                    break;

                case "Distance":
                    sous_choix = ArrayAdapter.createFromResource(this, R.array.Distance, android.R.layout.simple_spinner_item);
                    break;

                case "Prix":
                    sous_choix = ArrayAdapter.createFromResource(this, R.array.Distance, android.R.layout.simple_spinner_item);
                    break;

                case "Ville":
                    sous_choix = ArrayAdapter.createFromResource(this, R.array.Ville, android.R.layout.simple_spinner_item);
                    break;
            }
            sous_choix_filtrage.setAdapter(sous_choix);
        }

        else if(spinner.getId() == R.id.choix_filtrage)
        {
            String sous_choix_user;
            Global_function global_function = null;
            switch (type_filtrage.getSelectedItem().toString())
            {
                case "Cuisine":
                    sous_choix_user = parent.getItemAtPosition(position).toString();
                    String cuisine = sous_choix_user.toString();

                    global_function = new Global_function(this,"restau_filtrages.php?cuisine="+cuisine,googleMap);
                    global_function.sendrequest();
                    break;

                case "Distance":
                    sous_choix_user = parent.getItemAtPosition(position).toString();

                    int hlp_distance = sous_choix_user.indexOf("K",0);
                    int distance = Integer.parseInt(sous_choix_user.substring(0,hlp_distance).toString());

                    global_function = new Global_function(this,"restau_filtrages.php?distance="+distance,googleMap);
                    global_function.sendrequest();
                    break;

                case "Prix":
                    sous_choix_user = parent.getItemAtPosition(position).toString();
                    break;

                case "Ville":
                    sous_choix_user = parent.getItemAtPosition(position).toString();
                    String ville = sous_choix_user.toString();

                    global_function = new Global_function(this,"restau_filtrages.php?ville="+ville,googleMap);
                    global_function.sendrequest();
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
         MainActivity mainActivity = new MainActivity();
         mainActivity.mapSetUp(googleMap);
    }

}
