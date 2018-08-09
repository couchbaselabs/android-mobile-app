package com.couchbase.couchbase;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import org.json.JSONArray;

import java.util.ArrayList;

public class NotificationMonitor extends Service implements ConnectionResponse{
    private final int INTERVAL = 1000 * 10;
    private FileHelper fileHelper = new FileHelper();
    private SharedPreferences sharedPreferences;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences("ClusterName", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("Cluster", null) == null){
            stopSelf();
        }
        startPoolsMonitor();
        startLogsMonitor();

        Notification notification = new Notification.Builder(this)
                .setContentTitle("")
                .setTicker("")
                .setContentText("").build();
        startForeground(1, notification);
        return START_REDELIVER_INTENT;
    }

    Handler poolsMonitor = new Handler();
    Runnable poolsMonitorTask = new Runnable()
    {
        @Override
        public void run() {
            if(sharedPreferences.getString("Cluster", null) == null){
                stopSelf();
            }
            else{
                ConnectionTask monitor = new ConnectionTask(NotificationMonitor.this, "/pools/default", "GET");
                ClusterFinder clusterFinder = new ClusterFinder();
                monitor.execute(clusterFinder.findCluster(getApplicationContext()));
                poolsMonitor.postDelayed(poolsMonitorTask, INTERVAL);
            }
        }
    };

    Handler logsMonitor = new Handler();
    Runnable logsMonitorTask = new Runnable()
    {
        @Override
        public void run() {
            if(sharedPreferences.getString("Cluster", null) == null){
                stopSelf();
            }
            else{
                ConnectionTask monitor = new ConnectionTask(NotificationMonitor.this, "/logs", "GET");
                ClusterFinder clusterFinder = new ClusterFinder();
                monitor.execute(clusterFinder.findCluster(getApplicationContext()));
                logsMonitor.postDelayed(logsMonitorTask, INTERVAL);
            }
        }
    };

    public void startPoolsMonitor()
    {
        poolsMonitorTask.run();
    }

    public void stopPoolsMonitor()
    {
        poolsMonitor.removeCallbacks(poolsMonitorTask);
    }

    public void startLogsMonitor(){ logsMonitorTask.run(); }

    public void stopLogsMonitor() { logsMonitor.removeCallbacks(logsMonitorTask); }

    @Override
    public void getResponse(ConnectionData result, String extension) {
        SharedPreferences firstPref = this.getSharedPreferences("InitiateService", MODE_PRIVATE);
        JsonHelper JsonReader = new JsonHelper(result.getResult());
        if(result.getResult() != null) {
            if(firstPref.getBoolean("key", true) && extension == "/logs") {
                JSONArray logList = JsonReader.getTargetArray("list");
                Log lastLog = new Log(logList, logList.length() - 1);
                fileHelper.writeToFile(getApplicationContext(), "LastLog", lastLog.getNode() + lastLog.getTime() + lastLog.getEvent(), MODE_PRIVATE);
                SharedPreferences idCounter = getSharedPreferences("Counter", Context.MODE_PRIVATE);
                idCounter.edit().putInt("CounterVal", 0).commit();
                NotificationManager notificationManager = new NotificationManager(this, extension);
                notificationManager.logNotifier(result.getResult());
                firstPref.edit().putBoolean("key", false).commit();
            } else if (extension == "/logs") {
                NotificationManager notificationManager = new NotificationManager(this, extension);
                notificationManager.logNotifier(result.getResult());
            } else if (extension == "/pools/default") {
                CustomNotifier customNotifier = new CustomNotifier(this);
                customNotifier.checkChanges(new Cluster(result));
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
