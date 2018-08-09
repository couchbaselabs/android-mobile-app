package com.couchbase.couchbase;

import android.app.NotificationChannel;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationManager {
    private String extension;
    private NotificationMonitor notificationMonitor;
    private ArrayList<Log> logs = new ArrayList<>();
    private FileHelper fileHelper = new FileHelper();


    public NotificationManager(NotificationMonitor notificationMonitor, String extension){
        this.notificationMonitor = notificationMonitor;
        this.extension = extension;
    }

    public void customNotification(String msg, String ip, String pt){
        SharedPreferences idCounter = notificationMonitor.getSharedPreferences("Counter", Context.MODE_PRIVATE);
        createNotificationChannel("Node", "Node", "node");
        buildNotification(ip + ":" + pt, msg,"node", idCounter.getInt("CounterVal", 0));
        idCounter.edit().putInt("CounterVal", idCounter.getInt("CounterVal", 0) + 1).commit();
    }

    public void notificationOrganizer(){
        if(extension == "/logs"){
            for(int i = logs.size() - 1; i >= 0; i--){
                SharedPreferences idCounter = notificationMonitor.getSharedPreferences("Counter", Context.MODE_PRIVATE);
                createNotificationChannel("Logs", "Logs", "logs");
                buildNotification(logs.get(i).getModule() + ": " + logs.get(i).getType(), logs.get(i).getEvent(), "logs", idCounter.getInt("CounterVal", 0));
                idCounter.edit().putInt("CounterVal", idCounter.getInt("CounterVal", 0) + 1).commit();
            }
            logs.clear();
        }
    }

    public void createNotificationChannel(CharSequence channelName, String channelDescription, String CHANNEL_ID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);
            android.app.NotificationManager notificationManager = (android.app.NotificationManager) notificationMonitor.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void buildNotification(String title, String content, String CHANNEL_ID, int notificationID){
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(notificationMonitor.getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(notificationMonitor.getApplicationContext());
        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    public void logNotifier(JSONObject result){
        RegExHelper regExHelper = new RegExHelper(notificationMonitor.getApplicationContext());
        try{
            JsonHelper JsonReader = new JsonHelper(result);
            JSONArray logList = JsonReader.getTargetArray("list");
            for(int i = logList.length() - 1; i >= 0; i--){
                Log log = new Log(logList, i);
                Log lastLog = new Log(logList, logList.length() - 1);
                if (fileHelper.findKeyNoWhitespace(notificationMonitor.getApplicationContext(),"LastLog",log.getNode() + log.getTime() + log.getEvent())){
                    fileHelper.writeToFile(notificationMonitor.getApplicationContext(), "LastLog", lastLog.getNode() + lastLog.getTime() + lastLog.getEvent(), notificationMonitor.getApplicationContext().MODE_PRIVATE);
                    break;
                }
                else if(i != 0) {
                    if (regExHelper.match(log.getEvent(), "RegExInclude")) {
                        logs.add(log);
                    }
                    else if (!fileHelper.findKey(notificationMonitor.getApplicationContext(), "ModuleFilters", log.getModule())) {
                        if (!fileHelper.findKey(notificationMonitor.getApplicationContext(), "TypeFilters", log.getType())) {
                            if(!regExHelper.match(log.getEvent(), "RegExExclude")){
                                logs.add(log);
                            }
                        }
                    }
                }
                else{
                    fileHelper.writeToFile(notificationMonitor.getApplicationContext(), "LastLog", lastLog.getNode() + lastLog.getTime() + lastLog.getEvent(), notificationMonitor.getApplicationContext().MODE_PRIVATE);
                    logs.clear();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        notificationOrganizer();
    }
}
