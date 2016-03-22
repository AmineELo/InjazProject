package com.example.amineelouattar.injazproject.informationtrajet;

/**
 * Created by Ayoub on 22/03/2016.
 */
public class InfoChemin
{
    String depart,arrive;
    String distance,duree;
    String chemin;

    public InfoChemin(String depart, String arrive, String distance, String duree, String chemin)
    {
        this.depart = depart;
        this.arrive = arrive;
        this.distance = distance;
        this.duree = duree;
        this.chemin = chemin;
    }

    public String toString()
    {
        return depart+" "+arrive+" "+distance+" "+duree+" "+chemin;
    }
}
