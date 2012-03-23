package com.throwapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.content.Context;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
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


public class Fbhandler extends Activity {
    
    public static SharedPreferences mPrefs;
    public static boolean connected = false;
    
      private Context c;
      Intent fbhandler;

     
     public static Facebook facebook = new Facebook("200385206700551");
     
     String type = null;
     int power = 0;
     float hangtime = 0;
     int TOTAL = 0;
     
     @Override
    public  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        Log.d("fbHandler","just entered fbhandler");
        
        Bundle b = getIntent().getExtras();
        
        
        power = b.getInt("power", 0);
        hangtime = b.getFloat("hangtime", 0);
        TOTAL = b.getInt("total", 0);
        type = b.getString("type");
       
        connectFB();
       // finish();
      
    }

public void connectFB() {
         /*
         * All the facebook shizzle
         */
        
        
        /*
         * Get existing access_token if any
         */
        
         mPrefs = getPreferences(MODE_PRIVATE);
        Log.d("ThrowDevice","just initiated connectFB ");
        //System.out.println(mPrefs);
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        Log.d("ThrowDevice","access_token string = "+access_token);
        if(access_token != null) {
            facebook.setAccessToken(access_token);
            Log.d("ThrowDevice","set accesstoken ");
        }
        
        
        if(expires != 0) {
            facebook.setAccessExpires(expires);
            
            Log.d("ThrowDevice","set accessexpires ");
        }
        
        /*
         * Only call authorize if the access_token has expired.
         */
        if(!facebook.isSessionValid()) {
            
            Log.d("ThrowDevice","session aint valid so im running that now ");
            facebook.authorize(this, new String[] {"read_stream", "publish_stream"}, new DialogListener() {
                @Override
                public void onComplete(Bundle values) {
                    SharedPreferences.Editor editor = mPrefs.edit();
                    editor.putString("access_token", facebook.getAccessToken());
                    editor.putLong("access_expires", facebook.getAccessExpires());
                    editor.commit();
                    
                    upload();
                    
                }
    
                @Override
                public void onFacebookError(FacebookError error) {
                connected = false;
                Log.d("ThrowDevice","onfacebookerror "+error);
                }
    
                @Override
                public void onError(DialogError e) {
                    connected = false;
                Log.d("ThrowDevice","onerror "+e);
                }
    
                @Override
                public void onCancel() {
                connected = false;
                Log.d("ThrowDevice","onCancel ");
                }
            });
        }
        else{
            upload();
        }
          
    }

 @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("ThrowDevice","running callback ");
        facebook.authorizeCallback(requestCode, resultCode, data);
        
         
    }
 
 
  public void upload(){         
             if(type.equals("shareToFb")){
            
            
            
        
            Log.d("fbHandler","type = shareToFb");
            
            String fbString = "I just threw my Android scoring: " +TOTAL+ " I bet you cant beat me, maddafakkaaaah! Check out the highscores at http://mariusengen.com/throwapp/getHighscore.php" ;
            Bundle fb = new Bundle();
            fb.putString("message", fbString);
                                
                                
            
            new AsyncFB().execute(fb);
            
             Toast.makeText(getBaseContext(),"Posted to Facebook", 
                   Toast.LENGTH_SHORT).show(); 
                                
            Intent returnIntent = new Intent();
            setResult(RESULT_OK,returnIntent);  
        
            finish();
        }
             
             /*
              * SUBMITTING HIGHSCORE 
              */
        else if(type.equals("submitHs")){
        Bundle b = new Bundle();
        try {
         String jarray = facebook.request("me");
                                    
         Log.d("fbHandler", "facebook array looks like: "+jarray);
         JSONObject cock = new JSONObject(jarray);
         String username ="";
         
         try{
            username = cock.getString("username");
         }
         catch(Exception e){
             username = cock.getString("id");
         }
         b.putString("username",username);
                                     
          b.putString("id", cock.getString("id"));
          b.putInt("power", power);
          b.putFloat("hangtime", hangtime);
          b.putInt("total", TOTAL);
           

           } catch (Exception e) {
                                                                // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("Name", "couldnt try submit highscore, got response: " + e);
        }    
            
        
        new AsyncScore().execute(b);
            Toast.makeText(getBaseContext(),"Submitted to highscore", 
                   Toast.LENGTH_SHORT).show(); 
            
        Intent returnIntent = new Intent();
            setResult(RESULT_OK,returnIntent);  
        
            finish();
        
        }
           }    


private class AsyncFB extends AsyncTask<Bundle, Integer, Integer> {
        @Override
        protected Integer doInBackground(Bundle... params) {
            Log.d("LOGGER", "Starting async fb");
            try {
                Log.d("ThrowDevice","im trying in asyncfb");
                // String fbString = params[0];
                Bundle fbk = params[0];
                        // fb.putString("Message", fbString);
                String response = facebook.request("me");
                        response = facebook.request("me/feed", fbk, "POST");
                        Log.d("String", "got response: " + fbk);
                Log.d("Tests", "got response: mo" + response);
                if (response == null || response.equals("") ||  response.equals("false") || response.equals("")) {
                        Log.d("ThrowDevice", "did not happend");
                        
               }else{
                     
                     Log.d("ThrowDevice","response is not null or ''");
                
                }
                
                
            } catch (Exception e) {
                e.printStackTrace();
            }
                /* TODO make this happend right
                                    
                 * 
                 */
            return 0;
        }

    }
private class AsyncScore extends AsyncTask<Bundle, Integer, Integer> {
        @Override
        protected Integer doInBackground(Bundle... params) {
            Log.i("LOGGER", "Starting...");
            try {
                
                // String fbString = params[0];
                Bundle b = params[0];
                String username = b.getString("username");
                String id = b.getString("id");
                int power = b.getInt("power");
                float hangtime = b.getFloat("hangtime");
                int total = b.getInt("total");
                postData(username, id, power, hangtime, total);
                
                
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

    public void postData(String name, String id, int power, float hangtime, int totalscore) {    
        try {
          // Create a new HttpClient and Post Header 
        String url = "http://mariusengen.com/throwapp/addHighscore.php?jorn="+name+"&id="+id+"&fredrik="+power+"&ollis="+hangtime+"&larserik="+totalscore;
         HttpClient httpclient = new DefaultHttpClient();  
         HttpPost httppost = new HttpPost(url);  
         Log.d("url", "got response: " + url);
         
         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();  
         nameValuePairs.add(new BasicNameValuePair("name", "Noon"));  
         nameValuePairs.add(new BasicNameValuePair("level", "0"));  
         httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs)); 
            // Execute HTTP Post Request  
            HttpResponse response = httpclient.execute(httppost);           
            String text = EntityUtils.toString(response.getEntity());          
               Log.d("Response","response = "+text); 
               
               /*TODO make this happen approporiate
               
                * 
                */
               
               
        } catch (ClientProtocolException e) {  
            // TODO Auto-generated catch block 
         e.printStackTrace();
        } catch (IOException e) {  
            // TODO Auto-generated catch block  
         e.printStackTrace();
        }  
    }    

}



}