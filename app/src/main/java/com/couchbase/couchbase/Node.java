package com.couchbase.couchbase;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Node {
    private JSONObject jsonObject;
    private String hostname;
    private ArrayList<String> services;
    private String status;
    private double cpuRate;
    private double ramRate;
    private double swapRate;
    private long diskUsed;
    private String ip;
    private String pt;
    private String membership;
    private String otp;
    private String version;
    private String os;
    private int cpuCount;
    private long uptime;
    public Node(JSONObject jsonObject){
        initializeNode(jsonObject);
    }

    public void initializeNode(JSONObject jsonObject){
        JsonHelper nodeReader = new JsonHelper(jsonObject);
        JsonHelper systemStatsReader = new JsonHelper(nodeReader.getTargetObject("systemStats"));
        JsonHelper interestingStatsReader = new JsonHelper(nodeReader.getTargetObject("interestingStats"));
        this.jsonObject = jsonObject;
        this.hostname = nodeReader.getTargetString("hostname");
        this.services = new ArrayList<>();
        this.status = nodeReader.getTargetString("status");
        this.cpuRate = systemStatsReader.getTargetDouble("cpu_utilization_rate");
        this.ramRate = (1 - ((double) systemStatsReader.getTargetLong("mem_free") / (double) systemStatsReader.getTargetLong("mem_total"))) * 100;
        this.swapRate = (1 - ((double) systemStatsReader.getTargetLong("swap_used") / (double) systemStatsReader.getTargetLong("swap_total"))) * 100;
        this.diskUsed = (interestingStatsReader.getTargetLong("couch_views_actual_disk_size") + interestingStatsReader.getTargetLong("couch_docs_actual_disk_size"))/1000000;
        for(int i = 0; i < nodeReader.getTargetArray("services").length(); i++){
            try{ services.add((String) nodeReader.getTargetArray("services").get(i)); }
            catch (Exception e){ e.printStackTrace(); }
        }
        String hostname = nodeReader.getTargetString("hostname");
        this.ip = hostname.substring(0, hostname.indexOf(":"));
        this.pt = hostname.substring(hostname.indexOf(":") + 1);
        this.membership = nodeReader.getTargetString("clusterMembership");
        this.otp = nodeReader.getTargetString("otpNode");
        this.uptime = nodeReader.getTargetLong("uptime") / 60;
        this.os = nodeReader.getTargetString("os");
        this.version = nodeReader.getTargetString("version");
        this.cpuCount = nodeReader.getTargetInt("cpuCount");
    }

    public ArrayList<String> getServices() {
        return services;
    }

    public String getMembership() {
        return membership;
    }

    public String getOtp() {
        return otp;
    }

    public long getUptime() {
        return uptime;
    }

    public String getIp() {
        return ip;
    }

    public String getPt() {
        return pt;
    }

    public double getCpuRate() {
        return cpuRate;
    }

    public double getRamRate() {
        return ramRate;
    }

    public double getSwapRate() {
        return swapRate;
    }

    public int getCpuCount() {
        return cpuCount;
    }

    public String getVersion() {
        return version;
    }

    public String getOs() {
        return os;
    }

    public long getDiskUsed() {
        return diskUsed;
    }

    public String getStatus() {
        return status;
    }

    public String getHostname() {
        return hostname;
    }
}
