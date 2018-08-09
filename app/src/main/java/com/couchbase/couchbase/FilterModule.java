package com.couchbase.couchbase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class FilterModule extends Fragment{
    private View filterModuleView;
    private Switch ns_cluster;
    private Switch ns_orchestrator;
    private Switch ns_memcached;
    private Switch ns_rebalancer;
    private Switch auto_failover;
    private Switch ns_vbucket_mover;
    private Switch mb_master;
    private Switch ns_node_disco;
    private Switch menelaus_sup;
    private Switch memcached_config_mgr;
    private Switch ns_storage_conf;
    private Switch menelaus_web_alerts_srv;
    private Switch ns_config;
    private Switch menelaus_web;

    private Switch filter_all;
    private FileHelper fileHelper = new FileHelper();
    private boolean toast;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        filterModuleView = inflater.inflate(R.layout.filter_module, container, false);

        ns_cluster = filterModuleView.findViewById(R.id.ns_cluster_switch);
        ns_orchestrator = filterModuleView.findViewById(R.id.ns_orchestrator_switch);
        ns_memcached = filterModuleView.findViewById(R.id.ns_memcached_switch);
        ns_rebalancer = filterModuleView.findViewById(R.id.ns_rebalancer_switch);
        auto_failover = filterModuleView.findViewById(R.id.auto_failover_switch);
        ns_vbucket_mover = filterModuleView.findViewById(R.id.ns_vbucket_mover_switch);
        mb_master = filterModuleView.findViewById(R.id.mb_master_switch);
        ns_node_disco= filterModuleView.findViewById(R.id.ns_node_disco_switch);
        menelaus_sup = filterModuleView.findViewById(R.id.menelaus_sup_switch);
        memcached_config_mgr = filterModuleView.findViewById(R.id.memcached_config_mgr_switch);
        ns_storage_conf = filterModuleView.findViewById(R.id.ns_storage_conf_switch);
        menelaus_web_alerts_srv = filterModuleView.findViewById(R.id.menelaus_web_alerts_srv_switch);
        ns_config = filterModuleView.findViewById(R.id.ns_config_switch);
        menelaus_web = filterModuleView.findViewById(R.id.menelaus_web_switch);

        filter_all = filterModuleView.findViewById(R.id.filter_all_modules_switch);

        final Switch[] moduleSwitches = {ns_cluster, ns_orchestrator, ns_memcached, ns_rebalancer, auto_failover, ns_vbucket_mover, mb_master, ns_node_disco,
                menelaus_sup, memcached_config_mgr, ns_storage_conf, menelaus_web_alerts_srv, ns_config, menelaus_web};

        final String[] moduleStrings = {"ns_cluster", "ns_orchestrator", "ns_memcached", "ns_rebalancer", "auto_failover", "ns_vbucket_mover", "mb_master",
        "ns_node_disco", "menelaus_sup", "memcached_config_mgr", "ns_storage_conf", "menelaus_web_alerts_srv", "ns_config", "menelaus_web"};

        toast = true;
        int checked = 0;
        for(int i = 0; i < moduleStrings.length; i++){
            if(fileHelper.findKey(getContext(), "ModuleFilters", moduleStrings[i])){
                checked += 1;
            }
            if(checked == moduleStrings.length){
                filter_all.setOnCheckedChangeListener(null);
                filter_all.setChecked(true);
            }
        }

        filter_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                toast = false;
                for(int i = 0; i < moduleSwitches.length; i++){
                    if(on){
                        if(!moduleSwitches[i].isChecked()){
                            moduleSwitches[i].setChecked(true);
                        }
                    }
                    else{
                        if(moduleSwitches[i].isChecked()){
                            moduleSwitches[i].setChecked(false);
                        }
                    }
                }
                if(on){
                    Toast.makeText(getContext(), "Filtered all listed modules", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Removed filteres on all listed modules", Toast.LENGTH_SHORT).show();
                }
                toast = true;
                fileHelper.logFile(getContext(), "ModuleFilters", "log123");
            }
        });

        for(int i = 0; i < moduleSwitches.length; i++){
            final int k = i;
            if(fileHelper.findKey(getContext(), "ModuleFilters", moduleStrings[i])){
                moduleSwitches[i].setOnClickListener(null);
                moduleSwitches[i].setChecked(true);
            }
            moduleSwitches[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                    if(on){
                        fileHelper.writeToFile(getContext(), "ModuleFilters", moduleStrings[k] + "\n", getContext().MODE_APPEND);
                        if(toast){
                            Toast.makeText(getContext(), "Filtered " + moduleStrings[k], Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        fileHelper.removeKey(getContext(), "ModuleFilters", moduleStrings[k]);
                        if(toast){
                            Toast.makeText(getContext(), "Removed " + moduleStrings[k] + " filter", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        return filterModuleView;
    }

}