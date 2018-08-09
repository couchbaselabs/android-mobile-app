package com.couchbase.couchbase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class LoginHome extends Fragment{
    private View loginView;
    private LinearLayout loginLayout;
    private Button addCluster;
    private Button clusterList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        loginView = inflater.inflate(R.layout.login_fragment_manager, container, false);
        loginLayout = loginView.findViewById(R.id.login_layout);

        addCluster = loginView.findViewById(R.id.add_cluster);
        clusterList = loginView.findViewById(R.id.cluster_list);

        addCluster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.login_frame, new ClusterAdder()).commit();
            }
        });


        clusterList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.login_frame, new ClusterList()).commit();
            }
        });

        return loginView;
    }
}
