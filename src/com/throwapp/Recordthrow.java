package com.throwapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hallvardwestman
 */
public class Recordthrow extends Activity {
    
    
    /*
     * Class-Global variables
     */
    Accelerationhandler AH;
    SensorManager SM; 
    
    int SBSIZE = 50;
    
    int WEIGHTLESS = 5;
    int RATE = 10;
    int [] calibrationBuffer = new int[SBSIZE];
    int[] startBuffer = new int[SBSIZE];
    int weightlessDuration = 0;
    
    int GRAVITY_EARTH = 9;
    
    boolean RunThreads = false;
    
    Thread calibrateDeviceThread,messurePowerThread,messureHangtimeThread;
    ImageView image;
    
    @Override
    public synchronized void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.recordthrow);
        image = (ImageView) findViewById(R.id.test_image);
        
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        AH = new Accelerationhandler(SM);
        //float tmpNext = 0.0F;
        
        
        
        Log.d("StartListening","starting listening");
        RunThreads = true;
        calibrateDevice();
        
        /*
         * cancelbutton while throwing, TODO: refactor code
         */
        
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        
                         RunThreads = false;    
                         
                    }
                  });
        
        
      
    }
    /*
     * keeps orientation standar-view
     */
           
    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    
            
    /*
     * sums up a bufferarray
     */
    
    int bufferCalc(int[] buffer){
        
        int bufferSize = buffer.length;
        int totalValue=0;
        for(int i = 0; i<bufferSize;i++){
            totalValue += buffer[i];
        }
        
        
        Log.d("RecordThrow","returning bufferCalc() "+totalValue);
        return totalValue;
    }
    
    
    synchronized void calibrateDevice(){
        calibrateDeviceThread = new Thread(){
            
            
            int calibrationVector=0;
            
            
        
        public void run(){
            /*
              * Waiting for sensor-reading
              */
            
            
            
            int calibrationPosition = 0;
            int calibrationCheck = 0;
            
             while(calibrationCheck < 8 || calibrationCheck > 10 && RunThreads){
                 
                    Log.d("RecordThrow","Waiting for sensor-reading");
                    try {
                        this.sleep(10);
                        
                        calibrationVector = (int) AH.returnAcceleration();
                        
                        if(calibrationPosition == SBSIZE-1){
                            calibrationPosition = 0;
                        }
                        
                        calibrationBuffer[calibrationPosition] = calibrationVector;
                        calibrationPosition++;
                        
                        
                        calibrationCheck = bufferCalc(calibrationBuffer)/SBSIZE;
                        Log.d("RecordThrow","calibrationCheck = "+calibrationCheck);
                        
                    } 
                    catch (InterruptedException ex) {
                    Log.d("RecordThrow","calibrateDevice()");
                    }          
             }
             calibrateDeviceHandler.sendMessage(Message.obtain(calibrateDeviceHandler));
             
             this.interrupt();
        }
        };
        Log.d("RecordThrow","starting initiateBuffer");
          calibrateDeviceThread.start();
    } 
    
    
    
    synchronized void messurePower(){
        messurePowerThread = new Thread() {

          public void run() {
         
             startBuffer = calibrationBuffer;
              
              
             int totalFilterVector = 9;
             int startBufferPosition = 0;
             int[] startFilterBuffer = new int[5];
          
             int startFilterPosition =0;
             int curAccel;
             
             while(totalFilterVector > WEIGHTLESS && RunThreads){
                 Log.d("RecordThrow","Waiting for weightless state");
                try {
                    this.sleep(RATE);
                    curAccel = (int) AH.returnAcceleration();
                    
                    Log.d("RecordThrow","Current vector is = "+curAccel);
                    /*
                     * wrapping startBuffer - array 
                     */
                    if(startBufferPosition==SBSIZE-1){
                        startBufferPosition = 0;
                        Log.d("RecordThrow","Wrapping startbufferarray");
                    }
                    
                        //filling startbuffer
                            startBuffer[startBufferPosition] = curAccel;
                            startBufferPosition++;
                        
                        /*
                         * FILTERING THAT USER HAS THROWN
                         */
                        startFilterBuffer[startFilterPosition] = curAccel;
                        startFilterPosition++;
                        
                        
                        /*wrapping filterarray
                         * 
                         */
                        if(startFilterPosition==4){
                            startFilterPosition = 0;
                            Log.d("RecordThrow","Resetting filterarray");
                        }
                        
                        //setting up filter to check for weightless
                        
                        totalFilterVector = bufferCalc(startFilterBuffer);
                       Log.d("RecordThrow","buffervector = "+totalFilterVector);
                        
                        
               } 
                catch (InterruptedException ex) {
                    Log.d("RecordThrow","couldnt sleep and set buffervalue");
                }       
             }
             
             /*
              * device is weightless, calling function to record
              */
             initiateBufferHandler.sendMessage(Message.obtain(initiateBufferHandler));
             this.interrupt();
          }
    };
          Log.d("RecordThrow","starting initiateBuffer");
          messurePowerThread.start();
        
    }
      
      /*
       * Messuring hangtime
       */
    synchronized void messureHangtime(){
        messureHangtimeThread = new Thread() {

          public void run() {
              
        
              
                 
             int curAccel = 0;
             int i = 0;
             long bootTime = SystemClock.elapsedRealtime(); 
             int[] endFilterBuffer = new int[5];
             int endBufferPosition=0;
             int totalFilterVector = 0;
             weightlessDuration = 0;
             /*
              * reading duration of which device is weightless
              */
             while(totalFilterVector < GRAVITY_EARTH && RunThreads){
                 Log.d("RecordThrow","messuring time device is weightless");
                    try {
                        this.sleep(RATE);
                        
                        curAccel = (int) AH.returnAcceleration();
                        endFilterBuffer[endBufferPosition] = curAccel;
                        endBufferPosition++;
                 Log.d("RecordThrow","vector in weightless 'state' ="+curAccel);
                        weightlessDuration++;
                    } 
                    catch (InterruptedException ex) {
                    }
                    /*
                     * filter that assures current state is weightless
                     */
                    
                    if(endBufferPosition==4){
                            endBufferPosition = 0;
                            
                            Log.d("RecordThrow","Resetting filterarray");
                        }
                    
                    totalFilterVector = bufferCalc(endFilterBuffer);
                  Log.d("RecordThrow","totalbuffervector = "+totalFilterVector);
             }
             
             messureHangtimeHandler.sendMessage(Message.obtain(messureHangtimeHandler));
             long bootTime2 = SystemClock.elapsedRealtime();
             int weightlessDuration2 = ((int) bootTime2 - (int) bootTime);
           Log.d("RecordThrow","systemclock weightless= "+weightlessDuration2);
           this.interrupt();
          }
    };
        
          Log.d("RecordThrow","starting messureHangtime");
          messureHangtimeThread.start();
        
    }
    
    
    private Handler calibrateDeviceHandler = new Handler() {

      @Override
      public void handleMessage(Message msg) { 
          
          if(RunThreads){
        image.setImageResource(R.drawable.ready);
        messurePower();
          }
          else{
              summarize();
          }
       
              
          }
     };
    
      private Handler initiateBufferHandler = new Handler() {

      @Override
      public void handleMessage(Message msg) { 
          
          
          
             //Log.d("vector = ",msg.what+"");
        //image = (ImageView) findViewById(R.id.test_image);
          
        if (RunThreads){
        image.setImageResource(R.drawable.weightless);
        messureHangtime();
        }
        else{ 
           summarize();
              
          }
      }
   };
    
       /*
     * Handler for recieving result from threads
     */
             
      private Handler messureHangtimeHandler = new Handler() {

      @Override
      public void handleMessage(Message msg) { 
         
          
          
             //Log.d("vector = ",msg.what+"");
       
        summarize();
        
      }
           
     };
      
      /*
       * setting weightless-image
       */
    
      
      
    
    /*
     * algorithm for calculation of power-score
     */
            
    int powerScoreCalc(int[] power){
        
        int totalScore=0;
        int k = 0;
        for(int i = 0; i<SBSIZE;i++){
                if(power[i] > GRAVITY_EARTH) {
                        totalScore += power[i];
                        k++;
                } 
        }
       
        
        
        Log.d("RecordThrow","number of power readings in startBuffer "+k);
        /*TODO: number of readings not above 0, should 
         * calculate reading for each vector above normal gravity
         * 
         */
        
         //cant take credit for gravity (-GRAVITY_EARTH), thats just wrong
        if(k!=0){
        return totalScore/k - GRAVITY_EARTH;
        }
        else{
            return totalScore;
        }
        }
    
    /*
     * algorithm for calculation of hangtime-score
     */
    float hangtimeScoreCalc(int hangtime){
        float i = hangtime;
        i/=100;
        return i;
    }
    
    /*
     * algorithm for calculation of total-score
     */
    int totalScoreCalc(int power,int hangtime){
        
        
        Log.d("LOGGING in CALC = "," "+hangtime);
        return power*power+power*hangtime;
    }
    
    
    public void summarize(){
        
        if (RunThreads){
          int power = powerScoreCalc(startBuffer);
             float hangtime = hangtimeScoreCalc(weightlessDuration);
             
             float tmphangtime = hangtime * 100;
             
             int inthangtime = (int)tmphangtime;
             Log.d("LOGGING = * = 100"," "+inthangtime);
             int total = totalScoreCalc(power,inthangtime);
            
             
              /*
         * returning result to activity
         */
              
          Intent returnIntent = new Intent();
          returnIntent.putExtra("Power",power);
          returnIntent.putExtra("Hangtime",hangtime);
          returnIntent.putExtra("Total",total);
          setResult(RESULT_OK,returnIntent);        
          finish();
        }
        else{
            setResult(RESULT_CANCELED);        
            finish();
        }
        
    }
    
    @Override
    public void onResume(){
        super.onResume();
        AH.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        AH.stopListening();
        
        Log.d("RecordThrow","stoppedListening");
        /*
         boolean cal = calibrateDeviceThread.isAlive();
         Log.d("RecordThrow","calibrateDeviceThread "+ cal);
         boolean mesPow = messurePowerThread.isAlive();
         Log.d("RecordThrow","messurePowerThread "+ mesPow);
         boolean mesHang = messureHangtimeThread.isAlive();
         Log.d("RecordThrow","messureHangtimeThread "+ mesHang);
        */
        
        
     }
     @Override
    public void onPause() {
        super.onPause();
        RunThreads = false;
        //summarize();
        
     }
}