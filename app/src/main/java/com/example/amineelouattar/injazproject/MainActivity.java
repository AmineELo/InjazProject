package com.example.amineelouattar.injazproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
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
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private GoogleMap googleMap;
    private GPSTracker gps;
    String type, ville;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    NavigationView navigation;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        initInstances();
        gps = new GPSTracker(MainActivity.this);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
        saveMyPostition();
        mapSetUp(googleMap);
        afficherestau(this, googleMap);

    }

    public void mapSetUp(GoogleMap googleMap)
    {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //this.googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(33.97730789999999,-6.890131999999994))
                .zoom(13)
                .tilt(1)
                .build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    //Enregsitrer notre position en arraylist
    private void saveMyPostition()
    {
        if(gps.canGetLocation())
        {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            GlobalVars.myLocation = new LatLng(33.9781792,-6.8920484000000215);//Mohim ana drt hna lat w lngstatique makhdmtsh bhado lilfo9 latitude ou longitude hit tele matijibshlia dikchi exacte
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(33.97730789999999,-6.890131999999994)).title("Moi");//Tabnssba lhna diro lat ou lng limdéclarihom lfo9 latitude ou longitude
            GlobalVars.userposition.add(markerOptions);
        }
        else
        {
            gps.showSettingsAlert();
        }
    }

    //Affichage Restaurants sur carte
    public void afficherestau(Context context,GoogleMap googleMap)
    {
        Global_function global_function = new Global_function(context,"restau_proches.php",googleMap);
        global_function.sendrequest();
    }

    public void alertView()
    {
        AlertDialog.Builder Dialog = new AlertDialog.Builder(this);
        Dialog.setTitle("Select Option");

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = li.inflate(R.layout.activity_filetring,null);
        Dialog.setView(dialogView);

        Dialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        Toast.makeText(getApplicationContext(), "type : " + type + " | ville : " + ville,Toast.LENGTH_SHORT).show();
                        googleMap.clear();
                        multiFilterRequest();
                    }
                });

        Dialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {

                    }
                });
        Dialog.show();
        Spinner spinnercategory = (Spinner) dialogView.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.Cuisine,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnercategory.setAdapter(adapter);

        spinnercategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            public void onItemSelected(AdapterView<?> parent, View arg1,
                                       int arg2, long arg3) {
                type = parent.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> arg0)
            {
                // TODO Auto-generated method stub
            }
        });


        // ---------------------------------------------------

        Spinner spinnercategory2 = (Spinner) dialogView.findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.Ville, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnercategory2.setAdapter(adapter2);

        spinnercategory2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            public void onItemSelected(AdapterView<?> parent, View arg1, int arg2, long arg3)
            {
                ville = parent.getSelectedItem().toString();
            }

            public void onNothingSelected(AdapterView<?> arg0)
            {
                // TODO Auto-generated method stub
            }
        });

    }

    private void multiFilterRequest()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalVars.URL+"/restaurants/multiFilter.php",
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Global_function global_function = new Global_function(MainActivity.this,googleMap);
                        global_function.parserresponse(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(MainActivity.this,error.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String,String> getParams()
            {
                Map<String,String> params = new HashMap<String, String>();
                params.put("type",type);
                params.put("ville",ville);
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void initInstances()
    {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        drawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.hello_world, R.string.hello_world);
        drawerLayout.setDrawerListener(drawerToggle);

        navigation = (NavigationView) findViewById(R.id.navigation_view);
        navigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.navigation_item_1:
                        //Do some thing here
                        // add navigation drawer item onclick method here
                        break;
                    case R.id.navigation_item_2:
                        //Do some thing here
                        // add navigation drawer item onclick method here
                        startActivity(new Intent(getApplicationContext(), Filtrage_simple.class));
                        finish();
                        break;
                    case R.id.navigation_item_3:
                        //Do some thing here
                        // add navigation drawer item onclick method here
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        alertView();
                        break;
                    case R.id.navigation_item_4:
                        //Do some thing here
                        Toast.makeText(MainActivity.this,"Ajouter un marqueurs sur map ensuite chosir une restaurant pour voir le chemin e d'autre information",Toast.LENGTH_LONG).show();
                        break;
                    case R.id.navigation_item_5:
                        //Do some thing here
                        // add navigation drawer item onclick method here
                        break;
                }
                return false;
            }
        });

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




}
