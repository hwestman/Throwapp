package com.throwapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class Throwdevice extends Activity implements OnClickListener{
    //Buttons that is beeing enabled and disabled
            
     Button throwButton;
     Button stopButton;
     Button saveButton;
     Button shareToFacebook;
     Button submitHighscoreButton;
     Button logoutFb;
     
     ImageView completedThrowImage;
     TextView tutorial_head;
     ImageView tutorial_cycle;
     TextView tutorial_tips;
     
   
     String FILENAME = "ThrowApp_data";
     Intent fbhandler;
     
     private Context CTX;
     
     private Throwndbadapter mDbHelper;
     private String resultVar;
     private boolean connected = false;
     
     
     public static SharedPreferences mPrefs;
     
      @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
  @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         //setContentView(R.layout.throwdevice);
        setContentView(R.layout.throwdevice);
        
        
       CTX = Throwdevice.this;
       
        
        //creating buttons
        throwButton = (Button) findViewById(R.id.throwButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        shareToFacebook = (Button) findViewById(R.id.shareToFacebook);
        completedThrowImage = (ImageView) findViewById(R.id.completedThrowImage);
        submitHighscoreButton = (Button) findViewById(R.id.submitHighscore);
        logoutFb = (Button) findViewById(R.id.logoutFb);
        
        
        //attaching listeners
        throwButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        logoutFb.setOnClickListener(this);
        
        
       
        
      
    }
    
   /*
    * Listener for the acivitybuttons, starts and stops recording
    */
    public void onClick(View v) {
            switch(v.getId()){
                case R.id.throwButton:
                    Intent recordThrow = new Intent(this, Recordthrow.class);
                    startActivityForResult(recordThrow,0);

                break;
                case R.id.saveButton:
                    saveThrow();
                break;
                case R.id.logoutFb:
                    
                    //LOGOUT FROM FACEBOOK!
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("access_token", null);
                    editor.commit();
                break;
                        
               
            }
    }
    
    private boolean checkValidScore(int power,float hangtime,int total){
        
        if(power>1 && hangtime > 0.01) 
            return true;
        else
            return false;
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        

        mPrefs = getPreferences(MODE_PRIVATE);
        String access_token = mPrefs.getString("access_token", null);
        Log.d("throwdevice","acces_token when setting visibility is ."+access_token);
         if(access_token != ""){
             logoutFb.setVisibility(0);
         }
        
         Log.d("ThrowDevice","running onactivityresult");
        
        tutorial_head = (TextView) findViewById(R.id.tutorial_head);
        tutorial_cycle = (ImageView) findViewById(R.id.tutorial_cycle);
        tutorial_tips = (TextView) findViewById(R.id.tutorial_tips);
        
        
        
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                
                
                final int power = data.getIntExtra("Power",0);
                final float hangtime = data.getFloatExtra("Hangtime",0);
                final int total = data.getIntExtra("Total",0);
                
                
                
                if(checkValidScore(power,hangtime,total)){
                
                shareToFacebook.setVisibility(0);
                saveButton.setVisibility(0);
                submitHighscoreButton.setVisibility(0);
                completedThrowImage.setVisibility(0);
                tutorial_head.setVisibility(8);
                tutorial_cycle.setVisibility(8);
                
                
                
               
                
                
                throwButton.setText(R.string.throwAgain);
                
                
                
                resultVar = "Power: " + power + " Hangtime : "+ hangtime + " Total : "+total;
                
                
                //TODO ugly this is 
                tutorial_tips.setText(resultVar);
                
                
                submitHighscoreButton.setVisibility(0);
                
                
                /*
                 * Sharing to facebook button
                 */
                shareToFacebook.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        
                        
                        boolean gotInterwebz = Functions.haveInternet(CTX);
                        if(gotInterwebz) {
                        
                       fbhandler = new Intent(Throwdevice.this,Fbhandler.class);
                        
                        Log.d("throwdevice","just clicked");
                        
                        Bundle b = new Bundle();
                        
                        b.putInt("power", power);
                        b.putFloat("hangtime", hangtime);
                        b.putInt("total", total);
                        b.putString("type", "shareToFb");
                        
                        fbhandler.putExtras(b);
                        
                    startActivityForResult(fbhandler,1);
                    
                        }
                        else {
                        Toast.makeText(getBaseContext(),R.string.fbErrorConnectToast, 
                                Toast.LENGTH_SHORT).show();
                    }
                     
                   }
                  });
                
                submitHighscoreButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        
                        boolean gotInterwebz = Functions.haveInternet(CTX);
                        if(gotInterwebz) {
                        fbhandler = new Intent(Throwdevice.this,Fbhandler.class);
                        Log.d("throwdevice","just clicked submithighscore");
                        
                        Bundle b = new Bundle();
                        
                        b.putInt("power", power);
                        b.putFloat("hangtime", hangtime);
                        b.putInt("total", total);
                        b.putString("type", "submitHs");

                        fbhandler.putExtras(b);
                    startActivityForResult(fbhandler,1);
                        
                        }
                        else {
                        Toast.makeText(getBaseContext(),R.string.fbErrorConnectToast, 
                                Toast.LENGTH_SHORT).show();
                    }
                            
                        } 
                        
                        
                    
                });
                    
                
                
                
            }else{
                tutorial_tips.setText(R.string.deviceSpin);
                }
            }
                
            else if (resultCode == RESULT_CANCELED) 
                
                tutorial_tips.setText(R.string.canceledThrow);
            
            
            }
        
    }
            
        
    private void saveThrow(){
        mDbHelper = new Throwndbadapter(this);
        mDbHelper.open();
        
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.saveResultAlert);
        
        
        // Set an EditText view to get user input 
            final EditText input = new EditText(this);
            alert.setView(input);
                    
            alert.setPositiveButton(R.string.okButton, new DialogInterface.OnClickListener() {
                    
            public void onClick(DialogInterface dialog, int whichButton) {
            String value = input.getText().toString();
                    
            mDbHelper.createThrow(value,resultVar);
                            
                   //confirms save to user
                   Toast.makeText(getBaseContext(),R.string.localSaveToast, 
                   Toast.LENGTH_SHORT).show();
                        
                            
                      }
             });

                    alert.setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
                      public void onClick(DialogInterface dialog, int whichButton) {
                            //confirms that it has not been saved
                          Toast.makeText(getBaseContext(),R.string.localSaveCanceledToast, 
                            Toast.LENGTH_SHORT).show();
                      }
                    });

                    alert.show();
    }
    
     
}