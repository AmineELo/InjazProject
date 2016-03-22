package com.example.amineelouattar.injazproject.informationtrajet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.amineelouattar.injazproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InformationChemin extends AppCompatActivity
{

    //Déclaration du classe infochemin
    InfoChemin infochemin;

    //Déclaration du latitude depart,arrive et longitude depart arrive
    double latitudedepart,longitudedepart;
    double latitudearrive,longitudearrive;


    //Arraylist des infochemins
    ArrayList<InfoChemin> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_chemin);


        //Arraylist déclaration
        arrayList = new ArrayList<InfoChemin>();

        //Recycler View
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //Button de test
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InformationCheminAdapter informationCheminAdapter = new InformationCheminAdapter(arrayList);
                recyclerView.setAdapter(informationCheminAdapter);
            }
        });

        //Récupérer l'intent
        Intent intent = getIntent();
        String depart = intent.getStringExtra("depart");
        String arrive = intent.getStringExtra("arrive");


            int firstlatlng = depart.indexOf(",", 0);

            String latlngdep = depart.substring(0,firstlatlng).toString();
            String latlngarr = depart.substring(firstlatlng+1,depart.length()).toString();



            latitudedepart = Double.valueOf(latlngdep.toString());
            longitudedepart = Double.valueOf(latlngarr.toString());


        
            int lastlatlng = arrive.indexOf(",",0);

            latlngdep = arrive.substring(0,lastlatlng).toString();
            latlngarr = arrive.substring(lastlatlng+1,arrive.length()).toString();

            latitudearrive = Double.valueOf(latlngdep.toString());
            longitudearrive =Double.valueOf(latlngarr.toString());

            //Récupération chemin
            Chemin(latitudedepart, longitudedepart, latitudearrive, longitudearrive);



    }

    //Récupérer le chemin entre 2 points en donnant lat et lng de chacun d'eux
    public void Chemin(double latd,double lngd,double lata,double lnga)
    {
        String url = "http://maps.googleapis.com/maps/api/directions/json?origin="+latd+","+lngd+"&destination="+lata+","+lnga+"&sensor=false&mode=DRIVING&language=fr";
        sendrequest(url);
    }

    private void sendrequest(String url)
    {
        //Send request  //Connecter au serveur pour envoyer le latlng depart et arrive dont le but de récupérer le chemin
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        parseurinfochemin(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //Au cas d'un erreur au niveau de connexion ou un erreur quelconque
                        Toast.makeText(InformationChemin.this, "Erreur détécte  " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        Volley.newRequestQueue(this).add(stringRequest); //Ajouter votre request dans le new.... pour l'execuction
        //Fin Send Request
    }

    public void parseurinfochemin(String objectresponse)
    {
        JSONObject object;
        //Recu data sour format Json
        try {
            //Notre Object Json recu
            object = new JSONObject(objectresponse);

            //Crétion de notre Array routes
            JSONArray routes = object.getJSONArray("routes");

            //Création de notre array legs
            JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");

            //Création de notre array steps
            JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");

            //Récupérer l'adresse de départ
            String adressedepart = legs.getJSONObject(0).getString("start_address");

            //Récupérer l'adresse d'arrivé
            String adressearrive = legs.getJSONObject(0).getString("end_address");

            //Récupérer la distance entre le départ et l'arrivé
            String distance = legs.getJSONObject(0).getJSONObject("distance").get("value").toString();

            //Récupérer le temps à faire entre le départ et l'arrivé
            String dure = legs.getJSONObject(0).getJSONObject("duration").get("text").toString();

            String infoiteneraire = "";

            for (int i = 0; i < steps.length(); i++)
            {
                //Html.fromHtml pour éliminer les instructions html comme <b> etc.
                Spanned chemin = Html.fromHtml(steps.getJSONObject(i).get("html_instructions").toString());

                //Récupérer le chemin
                infoiteneraire += chemin.toString()+"\n";

            }
            infochemin = new InfoChemin( adressedepart, adressearrive, distance, dure, infoiteneraire);
            arrayList.add(infochemin);



        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


}
