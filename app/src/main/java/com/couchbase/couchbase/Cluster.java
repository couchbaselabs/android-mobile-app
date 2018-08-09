package com.couchbase.couchbase;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Cluster {
    private ArrayList<Node> clusterNodes;
    private int dataNodeCount = 0;
    private int indexNodeCount = 0;
    private int searchNodeCount = 0;
    private int queryNodeCount = 0;
    private int eventingNodeCount = 0;
    private int analyticNodeCount = 0;
    private int serviceCount[] = {dataNodeCount, indexNodeCount, searchNodeCount, queryNodeCount, eventingNodeCount, analyticNodeCount};
    public Cluster(ConnectionData data){
        initializeNodes(data);
        updateServiceAmt();
    }

    public void initializeNodes(ConnectionData data){
        JsonHelper JsonReader = new JsonHelper(data.getResult());
        JSONArray nodeArray = JsonReader.getTargetArray("nodes");
        Log.d("nodes", nodeArray + "");
        clusterNodes = new ArrayList<>();
        for(int i = 0; i < nodeArray.length(); i++){
            try{
                Node node = new Node((JSONObject) nodeArray.get(i));
                clusterNodes.add(node);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void updateClusterFile(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("ClusterName", 0);
        FileHelper updater = new FileHelper();
        ArrayList<String> prefInfo = updater.readLine(sharedPreferences.getString("Cluster", ""), "/");
        ArrayList<ArrayList> clusterList = updater.readToFile( context,"ClusterList", ",");
        updater.writeToFile(context, "ClusterList", "", context.MODE_PRIVATE);
        for(int i = 0; i < clusterList.size(); i++){
            ArrayList<String> clusterInfo = clusterList.get(i);
            String append = "";
            if(!clusterInfo.get(0).equals(prefInfo.get(0))){
                for(String info : clusterInfo){
                    append += info;
                    append += ",";
                }
                updater.writeToFile(context, "ClusterList", append + "\n", context.MODE_APPEND);
            }
            else{
                append += prefInfo.get(0) + ",";
                for(Node node : clusterNodes){
                    append += node.getIp() + "/" + node.getPt() + "/" + prefInfo.get(1) + "/" + prefInfo.get(2) + "/" + ",";
                }
                updater.writeToFile(context, "ClusterList", append + "\n", context.MODE_APPEND);
            }
        }
    }

    public void updateServiceAmt(){
        for(Node node : clusterNodes){
            if(node.getServices().contains("kv")){
                dataNodeCount += 1;
                serviceCount[0] = dataNodeCount;
            }
            if (node.getServices().contains("index")){
                indexNodeCount += 1;
                serviceCount[1] = indexNodeCount;
            }
            if(node.getServices().contains("fts")){
                searchNodeCount += 1;
                serviceCount[2] = searchNodeCount;
            }
            if (node.getServices().contains("n1ql")){
                queryNodeCount += 1;
                serviceCount[3] = queryNodeCount;
            }
            if(node.getServices().contains("eventing")){
                eventingNodeCount += 1;
                serviceCount[4] = eventingNodeCount;
            }
            if (node.getServices().contains("cbas")){
                analyticNodeCount += 1;
                serviceCount[5] = analyticNodeCount;
            }
        }
    }

    public ArrayList<Node> getClusterNodes() {
        return clusterNodes;
    }

    public int[] getServiceCount() {
        return serviceCount;
    }
}