package com.throwapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;



public class Globalhighscore extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.globalhighscore);
        WebView webview = new WebView(this);
        setContentView(webview);
        webview.loadUrl("http://mariusengen.com/throwapp/getHighscore.php");
    
        
    }

    
    //Code from Simon MCcullen, slightly adjusted for my xml

  public void DownloadRSS(String URL)
    {   
        
        setContentView(R.layout.globalhighscore);
        
        //TODO: takes exception instantly
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            Document doc = null;
            DocumentBuilderFactory dbf = 
            DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            
            try {
                db = dbf.newDocumentBuilder();
                doc = db.parse(in);
            } catch (ParserConfigurationException e) {
                
                Toast.makeText(getBaseContext(),"error 2 try", 
                        Toast.LENGTH_LONG).show();
                
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SAXException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getBaseContext(),"error 3 try", 
                        Toast.LENGTH_LONG).show();
            }        
            
            doc.getDocumentElement().normalize(); 
            
            //---retrieve all the <item> nodes---
            NodeList itemNodes = doc.getElementsByTagName("user"); 
            
            String strTitle = "";
            String[] RSSTitles = new String[itemNodes.getLength()];
            
            
            for (int i = 0; i < itemNodes.getLength(); i++) { 
                Node itemNode = itemNodes.item(i); 
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) 
                {            
                  
                    //TODO: PARSE FROM http://dev.westman.no/projects/ThrowApp//globalhighscore.xml
                    
                } 
                
                
            }
            
          
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace(); 
            Toast.makeText(getBaseContext(),"error 1 try", 
                        Toast.LENGTH_LONG).show();
        }
    }
    
    
    private InputStream OpenHttpConnection(String urlString) 
    throws IOException
    {
        InputStream in = null;
        int response = -1;
               
        URL url = new URL(urlString); 
        URLConnection conn = url.openConnection();
                 
        if (!(conn instanceof HttpURLConnection))                     
            throw new IOException("Not an HTTP connection");
        
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect(); 

            response = httpConn.getResponseCode();                 
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();                                 
            }                     
        }
        catch (Exception ex)
        {
            throw new IOException("Error connecting");            
        }
        return in;     
    }
}