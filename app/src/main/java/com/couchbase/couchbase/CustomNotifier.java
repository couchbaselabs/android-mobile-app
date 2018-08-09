package com.couchbase.couchbase;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;

public class CustomNotifier {
    private NotificationMonitor notificationMonitor;
    private FileHelper customNotification;
    private SharedPreferences sharedPreferences;
    private FileHelper fileHelper = new FileHelper();


    public CustomNotifier(NotificationMonitor notificationMonitor){
        this.notificationMonitor = notificationMonitor;
        this.customNotification = new FileHelper();
        this.sharedPreferences = notificationMonitor.getSharedPreferences("CustomNotifications", Context.MODE_PRIVATE);
    }

    public void updatePastNotStatus(Cluster cluster){
        customNotification.writeToFile(notificationMonitor,"CustomNotifications", "" , notificationMonitor.MODE_PRIVATE);
        for(Node node : cluster.getClusterNodes()){
            String status = node.getIp() + node.getPt() + ",";
            for(String notificationStatus : getNotificationStatus(node)){
                status += notificationStatus + ",";
            }
            customNotification.writeToFile(notificationMonitor,"CustomNotifications", status + "\n", notificationMonitor.MODE_APPEND);
        }
    }
    public void checkChanges(Cluster cluster){
        ArrayList<ArrayList> serverStatus = customNotification.readToFile(notificationMonitor,"CustomNotifications", ",");
        Log.d("server", serverStatus + "");
        for(Node node : cluster.getClusterNodes()){
            for(ArrayList<String> server : serverStatus){
                if(server.get(0).equals(node.getIp() + node.getPt())){
                    for(int i = 0; i < getNotificationStatus(node).size(); i ++){
                        if(getNotificationStatus(node).get(i).equals("1") && server.get(i + 1).equals("0")){
                            sendNotification(i, node.getIp(), node.getPt());
                        }
                    }
                }
            }
        }
        updatePastNotStatus(cluster);
    }

    public ArrayList<String> getNotificationStatus(Node node){
        ArrayList<String> notificationStatus = new ArrayList<>();
        String cpuStatus = (node.getCpuRate() > sharedPreferences.getInt("CPU", 50)) ? "1" : "0";
        notificationStatus.add(cpuStatus);
        String ramStatus = (node.getRamRate() > sharedPreferences.getInt("RAM", 50)) ? "1" : "0";
        notificationStatus.add(ramStatus);
        String diskStatus = (node.getDiskUsed() > sharedPreferences.getInt("Storage", 50)) ? "1" : "0";
        notificationStatus.add(diskStatus);
        String statusStatus = (node.getStatus() != "healthy") ? "1" : "0";
        notificationStatus.add(statusStatus);
        return notificationStatus;
    }

    public void sendNotification(int i, String ip, String pt){
        NotificationManager notificationManager = new NotificationManager(notificationMonitor, "/pools/default");
        switch (i){
            case 0:
                if(!fileHelper.findKey(notificationMonitor.getApplicationContext(), "CustomFilters", "CPU")){
                    notificationManager.customNotification("CPU has exceeded " + sharedPreferences.getInt("CPU", 50) + "%", ip, pt);
                }
                break;
            case 1:
                if(!fileHelper.findKey(notificationMonitor.getApplicationContext(), "CustomFilters", "memory")){
                    notificationManager.customNotification("RAM has exceeded " + sharedPreferences.getInt("RAM", 50) + "%", ip, pt);
                }
                break;
            case 2:
                if(!fileHelper.findKey(notificationMonitor.getApplicationContext(), "CustomFilters", "storage")){
                    notificationManager.customNotification("Disk usage has exceeded " + sharedPreferences.getInt("Storage", 50) + " MB", ip, pt);
                }
                break;
            case 3:
                if(!fileHelper.findKey(notificationMonitor.getApplicationContext(), "CustomFilters", "status")){
                    notificationManager.customNotification("Status is not healthy", ip, pt);
                }
        }
    }
}
