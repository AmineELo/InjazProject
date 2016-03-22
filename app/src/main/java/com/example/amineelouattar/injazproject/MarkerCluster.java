package com.example.amineelouattar.injazproject;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;


/**
 * Created by Ayoub on 18/03/2016.
 */
public class MarkerCluster implements ClusterItem
{
    private final LatLng mposition;
    private String title;
    public static final class CustomRendred extends DefaultClusterRenderer<MarkerCluster>
    {
        public CustomRendred(Context context, GoogleMap map, ClusterManager<MarkerCluster> clusterManager)
        {
            super(context, map, clusterManager);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<MarkerCluster> cluster)
        {
            //start clustering if at least 1 items overlap
            return cluster.getSize() > 2;
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerCluster item, MarkerOptions markerOptions)
        {
            markerOptions.title(item.getTitle());
            super.onBeforeClusterItemRendered(item,markerOptions);
        }
    }

    public MarkerCluster(double lat,double lng,String title)
    {
       mposition = new LatLng(lat,lng);
       this.title = title;
    }

    @Override
    public LatLng getPosition()
    {
        return mposition;
    }

    public String getTitle()
    {
        return title;
    }

}
