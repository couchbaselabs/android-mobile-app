package com.couchbase.couchbase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

public class ClusterMonitor {
    private ConnectionResponse process;
    private Context context;
    public ClusterMonitor(ConnectionResponse process, Context context){
        this.process = process;
        this.context = context;

    }

    Handler poolsMonitor = new Handler();
    Runnable poolsMonitorTask = new Runnable()
    {
        @Override
        public void run() {
            SharedPreferences sharedPreferences = context.getSharedPreferences("DelayTime", Context.MODE_PRIVATE);
            int interval = 1000 * sharedPreferences.getInt("Delay", 10);
            ConnectionTask monitor = new ConnectionTask(process, "/pools/default", "GET");
            ClusterFinder clusterFinder = new ClusterFinder();
            monitor.execute(clusterFinder.findCluster(context));
            poolsMonitor.postDelayed(poolsMonitorTask, interval);
        }
    };

    Handler logsMonitor = new Handler();
    Runnable logsMonitorTask = new Runnable()
    {
        @Override
        public void run() {
            SharedPreferences sharedPreferences = context.getSharedPreferences("DelayTime", Context.MODE_PRIVATE);
            int interval = 1000 * sharedPreferences.getInt("Delay", 10);
            ConnectionTask monitor = new ConnectionTask(process, "/logs", "GET");
            ClusterFinder clusterFinder = new ClusterFinder();
            monitor.execute(clusterFinder.findCluster(context));
            logsMonitor.postDelayed(logsMonitorTask, interval);
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

}
