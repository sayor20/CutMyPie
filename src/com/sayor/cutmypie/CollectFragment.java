package com.sayor.cutmypie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import javax.xml.datatype.Duration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sayor.cutmypie.DonateFragment.PostDetails;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class CollectFragment extends Fragment {
	
	JSONParser jsonParser=new JSONParser();
	  double[] lat;
	double[] lon;
	ProgressDialog pDialog;

	HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String dataJsonStr = null;
	
	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {				       
		
		new getDetails().execute();
	
		//Inflate the layout for this fragment     		
		View root=inflater.inflate(R.layout.activity_collect, container, false);
		
		return root;
			      
  }
	
	class getDetails extends AsyncTask<Void, Void, String[]> implements OnMapReadyCallback{
    	
  	  boolean failure = false;
  	  String lat,lon,city;
  	  List<FoodData> inpList;
  	  
  	          @Override
  	          protected void onPreExecute() {
  	              super.onPreExecute();
  	              pDialog = new ProgressDialog(getActivity());
  	              pDialog.setMessage("Getting details...");
  	              pDialog.setIndeterminate(false);
  	              pDialog.setCancelable(true);
  	              pDialog.show();
  	          }

  	          
		protected String[] doInBackground(Void... params) {
			
	            final String BASE_URL="http://cutmypie.com/webservices/service.php";
	     
	             Uri builtUri= Uri.parse(BASE_URL);
	                                
	             URL url = null;
	            
				try {
					url = new URL(builtUri.toString());
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	             try {
					urlConnection = (HttpURLConnection) url.openConnection();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	             try {
					urlConnection.setRequestMethod("GET");
				} catch (ProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	             try {
					urlConnection.connect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	             // Read the input stream into a String
	             InputStream inputStream = null;
				try {
					inputStream = urlConnection.getInputStream();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	             StringBuffer buffer = new StringBuffer();
	             if (inputStream == null) {
	            	 dataJsonStr = null;
	             }
	             reader = new BufferedReader(new InputStreamReader(inputStream));

	             String line;
	             String[] resu = new String[3];
	             try {
					while ((line = reader.readLine()) != null) {
					     buffer.append(line + "\n");
					 }
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	             if (buffer.length() == 0) {
	                 // Stream was empty.  No point in parsing.
	                 return null;
	             }
	             dataJsonStr = buffer.toString();
	            
				 Type type = new TypeToken<List<FoodData>>(){}.getType();
				 inpList = new Gson().fromJson(dataJsonStr, type);
				return resu;

			  
           

		}
		
		protected void onPostExecute(String[] output) {
			
			            pDialog.dismiss();		            
			          MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
			  		  mapFragment.getMapAsync(this);		
			            
		   }

		public void onMapReady(GoogleMap mmap) {
			
			 mmap.addMarker(new MarkerOptions()
             .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps))
             .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
             .position(new LatLng(38.2813, 120.9045)));   		 
			
			for(FoodData inp:inpList){   		
    		  mmap.addMarker(new MarkerOptions()
             .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps))
             .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
             .position(new LatLng(inp.getLat(), inp.getLon())));
     		}
	}
}
}
