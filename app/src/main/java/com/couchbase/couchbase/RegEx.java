package com.couchbase.couchbase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RegEx extends Fragment {
    private View regExView;
    private View regExDialogView;

    private LinearLayout regExLayout;

    private EditText dialogRegExName;
    private EditText dialogRegExContent;

    private RadioGroup dialogRadio;
    private RadioButton dialogIncludeRegEx;
    private RadioButton dialogExcludeRegEx;

    private Button addRegEx;

    private FileHelper fileHelper = new FileHelper();
    private LayoutHelper layoutHelper;

    ArrayList<ArrayList> regExIncludeList;
    ArrayList<ArrayList> regExExcludeList;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        regExView = inflater.inflate(R.layout.reg_ex, container, false);
        regExDialogView = inflater.inflate(R.layout.reg_ex_dialog, container, false);


        regExLayout = regExView.findViewById(R.id.reg_ex_layout);
        addRegEx = regExView.findViewById(R.id.add_reg_ex);

        dialogRadio = regExDialogView.findViewById(R.id.dialog_reg_ex_radio);
        dialogRegExName = regExDialogView.findViewById(R.id.dialog_reg_ex_name);
        dialogRegExContent = regExDialogView.findViewById(R.id.dialog_reg_ex_content);
        dialogIncludeRegEx = regExDialogView.findViewById(R.id.dialog_include);
        dialogExcludeRegEx = regExDialogView.findViewById(R.id.dialog_exclude);

        layoutHelper = new LayoutHelper(getContext());

        buildLayout(inflater, container);

        addRegEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setTitle("Add RegEx");
                dialogBuilder.setCancelable(false);

                if(regExDialogView.getParent()!=null) {
                    ((ViewGroup)regExDialogView.getParent()).removeView(regExDialogView);
                }
                dialogBuilder.setView(regExDialogView);

                dialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for(ArrayList<String> regExInclude : regExIncludeList){
                            if(regExInclude.get(0).equals(dialogRegExName.getText().toString())){
                                Toast.makeText(getContext(), "RegEx creation failed.  Name already exists.", Toast.LENGTH_SHORT).show();
                                dialogRegExName.setText("");
                                dialogRegExContent.setText("");
                                dialogRadio.clearCheck();
                                return;
                            }
                            if(dialogIncludeRegEx.isChecked()){
                                if(regExInclude.get(1).equals(dialogRegExContent.getText().toString())){
                                    Toast.makeText(getContext(), "RegEx creation failed.  RegEx already exists.", Toast.LENGTH_SHORT).show();
                                    dialogRegExName.setText("");
                                    dialogRegExContent.setText("");
                                    dialogRadio.clearCheck();
                                    return;
                                }
                            }
                        }

                        ArrayList<ArrayList> regExExcludeList = fileHelper.readToFile(getContext(), "RegExExclude", ",");
                        for(ArrayList<String> regExExclude : regExExcludeList){
                            if(regExExclude.get(0).equals(dialogRegExName.getText().toString())){
                                Toast.makeText(getContext(), "RegEx creation failed.  Name already exists.", Toast.LENGTH_SHORT).show();
                                dialogRegExName.setText("");
                                dialogRegExContent.setText("");
                                dialogRadio.clearCheck();
                                return;
                            }
                            if(dialogExcludeRegEx.isChecked()){
                                if(regExExclude.get(1).equals(dialogRegExContent.getText().toString())){
                                    Toast.makeText(getContext(), "RegEx creation failed.  RegEx already exists.", Toast.LENGTH_SHORT).show();
                                    dialogRegExName.setText("");
                                    dialogRegExContent.setText("");
                                    dialogRadio.clearCheck();
                                    return;
                                }
                            }
                        }
                        if(dialogIncludeRegEx.isChecked()){
                            fileHelper.writeToFile(getContext(), "RegExInclude", dialogRegExName.getText().toString() + "," + dialogRegExContent.getText().toString() + "," + "\n", getContext().MODE_APPEND);
                        }
                        else if(dialogExcludeRegEx.isChecked()){
                            fileHelper.writeToFile(getContext(), "RegExExclude", dialogRegExName.getText().toString() + "," + dialogRegExContent.getText().toString() + "," + "\n", getContext().MODE_APPEND);
                        }
                        else{
                            dialogRegExName.setText("");
                            dialogRegExContent.setText("");
                            dialogRadio.clearCheck();
                            Toast.makeText(getContext(), "RegEx creation failed.  Please choose a type.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialogRegExName.setText("");
                        dialogRegExContent.setText("");
                        dialogRadio.clearCheck();
                        regExLayout.removeAllViews();
                        buildLayout(inflater, container);
                    }
                });

                dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });
        return regExView;
    }

    public void buildLayout(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container){
        regExIncludeList = fileHelper.readToFile(getContext(), "RegExInclude", ",");
        regExExcludeList = fileHelper.readToFile(getContext(), "RegExExclude", ",");
        ArrayList<View> regExListItems = new ArrayList<>();

        for(int i = 0; i < regExIncludeList.size(); i++){
            View regExListItem = inflater.inflate(R.layout.reg_ex_list_item, container, false);
            final TextView itemText = regExListItem.findViewById(R.id.reg_ex_name);
            TextView contentText = regExListItem.findViewById(R.id.reg_ex_content);
            ImageView delete = regExListItem.findViewById(R.id.reg_ex_delete);

            final ArrayList<String> regExInclude = regExIncludeList.get(i);
            itemText.setText("Name: " + regExInclude.get(0));
            contentText.setText("RegEx: " + regExInclude.get(1));

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fileHelper.writeToFile(getContext(), "RegExInclude", "", getContext().MODE_PRIVATE);
                    for(int j = 0; j < regExIncludeList.size(); j++){
                        if(!regExInclude.get(0).equals(regExIncludeList.get(j).get(0))){
                            String append = "";
                            ArrayList<String> regExIncludeAdder = regExIncludeList.get(j);
                            for(String info : regExIncludeAdder){
                                append += info;
                                append += ",";
                            }
                            fileHelper.writeToFile(getContext(), "RegExInclude", append + "\n", getContext().MODE_APPEND);
                        }
                    }
                    regExLayout.removeAllViews();
                    buildLayout(inflater, container);
                }
            });
            regExListItems.add(regExListItem);
        }

        for(int i = 0; i < regExExcludeList.size(); i++){
            View regExListItem = inflater.inflate(R.layout.reg_ex_list_item, container, false);
            TextView itemText = regExListItem.findViewById(R.id.reg_ex_name);
            TextView contentText = regExListItem.findViewById(R.id.reg_ex_content);
            ImageView delete = regExListItem.findViewById(R.id.reg_ex_delete);

            final ArrayList<String> regExExclude = regExExcludeList.get(i);
            itemText.setText("Name: " + regExExclude.get(0));
            contentText.setText("RegEx: " + regExExclude.get(1));

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fileHelper.writeToFile(getContext(), "RegExExclude", "", getContext().MODE_PRIVATE);
                    for(int j = 0; j < regExExcludeList.size(); j++){
                        if(!regExExclude.get(0).equals(regExExcludeList.get(j).get(0))){
                            String append = "";
                            ArrayList<String> regExIncludeAdder = regExExcludeList.get(j);
                            for(String info : regExIncludeAdder){//TODO error regexinclude
                                append += info;
                                append += ",";
                            }//TODO clear radio button check
                            fileHelper.writeToFile(getContext(), "RegExExclude", append + "\n", getContext().MODE_APPEND);
                        }
                    }
                    regExLayout.removeAllViews();
                    buildLayout(inflater, container);
                }
            });

            regExListItems.add(regExListItem);
        }


        for(int i = 0; i < regExListItems.size(); i+=2){
            if(!(i == regExListItems.size() - 1)){
                LinearLayout regExListItemsLayout = new LinearLayout(getContext());
                regExListItemsLayout.addView(regExListItems.get(i));
                regExListItemsLayout.addView(regExListItems.get(i + 1));
                layoutHelper.setLayout(regExListItemsLayout, regExLayout, 0, -1, -2, Gravity.CENTER, 1);
            }
            else{
                LinearLayout regExListItemsLayout = new LinearLayout(getContext());
                regExListItemsLayout.addView(regExListItems.get(i));
                layoutHelper.setLayout(regExListItemsLayout, regExLayout, 0, -1, -2, Gravity.CENTER, 1);
            }
        }
    }
}
