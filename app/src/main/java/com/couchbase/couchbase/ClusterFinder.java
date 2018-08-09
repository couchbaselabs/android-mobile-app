package com.couchbase.couchbase;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class ClusterFinder {

    public ArrayList<String> findCluster(Context context){
        FileHelper finder = new FileHelper();
        SharedPreferences sharedPreferences = context.getSharedPreferences("ClusterName", 0);
        FileHelper fileHelper = new FileHelper();
        ArrayList<String> prefInfo = fileHelper.readLine(sharedPreferences.getString("Cluster", ""), "/");
        ArrayList<ArrayList> clusterList = finder.readToFile( context,"ClusterList", ",");
        for(ArrayList<String> cluster : clusterList){
            if(cluster.get(0).equals(prefInfo.get(0))){
                return cluster;
            }
        }
        return null;
    }
}
