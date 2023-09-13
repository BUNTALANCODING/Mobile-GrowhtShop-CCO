package com.project.growthshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class alert5 {

    Activity activity;
    AlertDialog alertDialog;

    alert5(Activity myActivity)
    {
        activity = myActivity;
    }

    void startLoding(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.comp_useradd, null));
        builder.setCancelable(false);


        alertDialog = builder.create();
        alertDialog.show();

    }

    public void dismis(){
        alertDialog.dismiss();
    }
}
