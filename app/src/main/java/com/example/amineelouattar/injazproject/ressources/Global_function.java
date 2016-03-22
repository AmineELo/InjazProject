package com.example.amineelouattar.injazproject.ressources;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
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
import com.example.amineelouattar.injazproject.MarkerCluster;
import com.example.amineelouattar.injazproject.informationtrajet.InformationChemin;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Ayoub on 20/03/2016.
 */
public class Global_function implements DirectionCallback
{
    // Declare a variable for the cluster manager.
    private ClusterManager<MarkerCluster> mClusterManager;

    //Declare an arraylist of markercluster item
    private ArrayList<MarkerCluster> arrayList;

    public Context context;
    private String url;
    private GoogleMap googleMap;

    //Constructor of Glbal_function with url
    public Global_function(Context context,String url,GoogleMap googleMap)
    {
        this.context = context;
        this.url = url;
        this.googleMap = googleMap;
        googleMap.clear();
        addMarkers(googleMap);
    }

    //Constructor of Glbal_function without url
    public Global_function(Context context,GoogleMap googleMap)
    {
        this.context = context;
        this.googleMap = googleMap;
        googleMap.clear();
        addMarkers(googleMap);
    }

    //Send request sans envoie data
    public void sendrequest()
    {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(GlobalVars.URL+url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        parserresponse(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(context, "detect error connection " + error, Toast.LENGTH_LONG).show();
                    }
                }

        );
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    //Parseur of your response (on appelle de sendrequest())
    public void parserresponse(String response)
    {
        arrayList = new ArrayList<MarkerCluster>();
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("resultat");
            String value  = jsonArray.getJSONObject(0).getString("statut").toString();
            if(value.toString().equals("false"))
            {
                arrayList.add(new MarkerCluster(0, 0, ""));//Ca marche mais il faut voir autre facon
                setUpClusterer();//Ca marche mais il faut voir autre facon

                //Declar an alertedialogue to inform the user that there are no restaurants at asking criterion
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Remarque");
                builder.setMessage("Pas de restaurant au critère demander");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.setCancelable(true);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
            else
            {
                double lat, lng;
                String nom;
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    nom = jsonArray.getJSONObject(i).getString("nom_restaurant");
                    lat = jsonArray.getJSONObject(i).getDouble("latitude");
                    lng = jsonArray.getJSONObject(i).getDouble("longitude");
                    arrayList.add(new MarkerCluster(lat, lng, nom));
                }
                setUpClusterer();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(context,"detect error parser "+e,Toast.LENGTH_LONG).show();
        }

    }


    //Set up your Cluster Manager
    private void setUpClusterer()
    {
        // Initialize the manager with the context and the map
        mClusterManager = new ClusterManager<MarkerCluster>(context, googleMap);
        mClusterManager.setRenderer(new MarkerCluster.CustomRendred(context, googleMap, mClusterManager));
        // Point the map's listeners at the listeners implemented by the cluster manager
        googleMap.setOnCameraChangeListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);

        //Clear map
        googleMap.clear();

        //Add a position of user
        googleMap.addMarker(new MarkerOptions().position(GlobalVars.userposition.get(0).getPosition()).title("Moi"));

        //Add a userforsimulation if exists
        if(GlobalVars.arraymarkers.size() != 0)
        {
            googleMap.addMarker(new MarkerOptions().position(GlobalVars.arraymarkers.get(0).getPosition()).title("Simulation"));
        }





        // Add cluster items (markers) to the cluster manager.
        addItems(arrayList);
        mClusterManager.cluster();

        //On itemcluster Click listner
        OnItemclick(mClusterManager);
    }

    //On cluster Manager Item Click
    private void OnItemclick(ClusterManager<MarkerCluster> clusterItemClusterManager)
    {

        clusterItemClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MarkerCluster>()
        {
            @Override
            public boolean onClusterItemClick(final MarkerCluster markerCluster)
            {
                if (GlobalVars.arraymarkers.size() <=1)
                {
                    GlobalVars.departarrive.add(GlobalVars.arraymarkers.get(0).getPosition().latitude+","+GlobalVars.arraymarkers.get(0).getPosition().longitude);
                    GlobalVars.departarrive.add(markerCluster.getPosition().latitude+","+markerCluster.getPosition().longitude);

                    //Settup your Cluster Manager
                    setUpClusterer();

                    // Use the Builder class for convenient dialog construction
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Your Choice")

                            //Choice for More Information
                            .setPositiveButton("More Information", new DialogInterface.OnClickListener()
                            {

                                public void onClick(DialogInterface dialog, int id)
                                {
                                    String depart = GlobalVars.departarrive.get(0);
                                    String arrive = GlobalVars.departarrive.get(1);

                                    Intent i = new Intent(context,InformationChemin.class);
                                    i.putExtra("depart",depart);
                                    i.putExtra("arrive",arrive);
                                    //Toast.makeText(context,"Longueur du tableau est "+arrayList.size(),Toast.LENGTH_LONG).show();
                                    context.startActivity(i);

                                }
                            })
                            //---------- Fin Choice More information Code

                           // Choice Rooting To
                            .setNegativeButton("Rooting to", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    routing(GlobalVars.arraymarkers.get(0).getPosition(), markerCluster.getPosition());
                                }
                            });
                            //---------- Fin Chocie Rooting to

                    // Create the AlertDialog object and return it
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    return true;
                }
                else  if(GlobalVars.userposition.size() !=0 && GlobalVars.arraymarkers.size() ==0)
                {
                    GlobalVars.departarrive.add(GlobalVars.userposition.get(0).getPosition().latitude+","+GlobalVars.userposition.get(0).getPosition().longitude);
                    GlobalVars.departarrive.add(markerCluster.getPosition().latitude+","+markerCluster.getPosition().longitude);

                    //Settup your Cluster Manager
                    setUpClusterer();

                    // Use the Builder class for convenient dialog construction
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Your Choice")

                            //Choice for More Information
                            .setPositiveButton("More Information", new DialogInterface.OnClickListener()
                            {

                                public void onClick(DialogInterface dialog, int id)
                                {
                                    String depart = GlobalVars.departarrive.get(0);
                                    String arrive = GlobalVars.departarrive.get(1);
                                    Toast.makeText(context,"Départ est "+depart+" Arrivé est "+arrive,Toast.LENGTH_LONG).show();

                                    Intent i = new Intent(context,InformationChemin.class);
                                    i.putExtra("depart",depart);
                                    i.putExtra("arrive",arrive);
                                    context.startActivity(i);

                                }
                            })
                                    //---------- Fin Choice More information Code

                                    // Choice Rooting To
                            .setNegativeButton("Rooting to", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    routing(GlobalVars.userposition.get(0).getPosition(), markerCluster.getPosition());
                                }
                            });
                    //---------- Fin Chocie Rooting to

                    // Create the AlertDialog object and return it
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    return true;
                }
                else
                {
                    Toast.makeText(context, "Cliquer sur map pour ajouter un marqueur ", Toast.LENGTH_LONG).show();
                    return false;
                }

            }
        });
    }
    //Add a items to Cluster Manager
    private void addItems(ArrayList<MarkerCluster> arrayList)
    {
        // Add (array.size()) cluster items in close proximity
        mClusterManager.addItems(arrayList);
    }

    //Add marker to simulate with it
    public void addMarkers(final GoogleMap googleMap)
    {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {

            @Override
            public void onMapClick(LatLng latLng)
            {
                GlobalVars.arraymarkers.clear();
                googleMap.clear();
                setUpClusterer();
                MarkerOptions markerOptions = new MarkerOptions().title("forsimulation").position(latLng);
                googleMap.addMarker(markerOptions);
                GlobalVars.arraymarkers.add(markerOptions);
            }
        });
    }

    //Fonction fait le routage
    public void routing(LatLng origin,LatLng destination)
    {
        GoogleDirection.withServerKey("AIzaSyDWaR27Fz7PjA3Vpt4G3G7Le2LbeAnK-FY")
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction)
    {
        if (direction.isOK())
        {
            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
            googleMap.addPolyline(DirectionConverter.createPolyline(context, directionPositionList, 5, Color.RED));
        }
    }

    @Override
    public void onDirectionFailure(Throwable throwable)
    {
        Toast.makeText(context,"Routing non passer ", Toast.LENGTH_LONG).show();
    }
}
