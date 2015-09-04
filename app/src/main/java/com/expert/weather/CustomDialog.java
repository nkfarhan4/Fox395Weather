package com.expert.weather;

/**
 * Created by krishnakumar on 04-09-2015.
 */

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;


/**
 * Created by Android on 21-04-2015.
 */
public class CustomDialog extends Dialog implements
        android.view.View.OnClickListener {
    CustomDialogInterface customDialogInterface;
    public Activity c;
    public Dialog d;
    ToggleButton toggle;
    public TextView topButton, bottomButton;


    public CustomDialog(Activity a,int theme) {
        super(a,theme);


        // TODO Auto-generated constructor stub
        this.c = a;
    }


    public void setResponse(CustomDialogInterface customDialogInterface){
        this.customDialogInterface=customDialogInterface;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_alert_dialog);

        toggle = (ToggleButton)findViewById(R.id.toggle);
        topButton = (TextView) findViewById(R.id.topButton);
     //   bottomButton = (TextView) findViewById(R.id.bottomButton);


        topButton.setText("dwdwddwd");
      //  bottomButton.setText(msgText2);


        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    customDialogInterface.tempCelsius();
                }else{
                    customDialogInterface.tempFarehn();
                }
            }
        });



        topButton.setOnClickListener(this);
    //    bottomButton.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.topButton:
                customDialogInterface.topButton();
                break;
         /*   case R.id.bottomButton:
                customDialogInterface.bottomButton();
                break;*/
            default:
                break;
        }
        dismiss();
    }


    public interface CustomDialogInterface {


        public void topButton();


        public void tempFarehn();

        public void tempCelsius();

        public void bottomButton();






    }
}