package com.expert.weather;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by Krishna on 10-05-2015.
 */
public class CustomDialogBoxRate extends Dialog {

    TextView txtRate,txtLater;
    public Activity act;
    public CustomDialogBoxRate(Activity context) {
        super(context);
        this.act = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_rate);

        txtRate = (TextView)findViewById(R.id.txtRate);
        txtLater = (TextView)findViewById(R.id.txtLater);


        txtLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        txtRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();

                final String appPackageName4 = "com.expert.weather"; // getPackageName() from Context or Activity object
                try {
                    act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName4)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName4)));
                }
            }
        });
    }

    //end of main class
}
