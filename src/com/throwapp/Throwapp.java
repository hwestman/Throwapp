package com.throwapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

//main class and index activity
public class Throwapp extends Activity implements OnClickListener
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
       /* super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
    
        //Capture our button from layout
        
        Button throwDeviceButton = (Button)findViewById(R.id.throwDeviceButton);
        Button localHSButton = (Button)findViewById(R.id.localHSButton);
        Button globalHSButton = (Button)findViewById(R.id.globalHSButton);
    
        //Register the onClick listener with the implementation above
        throwDeviceButton.setOnClickListener(this);
        localHSButton.setOnClickListener(this);
        globalHSButton.setOnClickListener(this);
        */
        
    }
      @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
    // Listeners for main menubuttons
        public void onClick(View v) {
            
        switch(v.getId()){

            case R.id.throwDeviceButton:
                Intent TDBL = new Intent(this, Throwdevice.class);
                startActivity(TDBL);
                
            break;
                
            case R.id.localHSButton:
                Intent LHSB = new Intent(this, Localhighscore.class);
                startActivity(LHSB); 
            break;
                
            case R.id.globalHSButton:
                Intent GHSB = new Intent(this, Globalhighscore.class);
                startActivity(GHSB); 
            break;
            }
        };

  
}