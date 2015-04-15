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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DonateFragment extends Fragment implements OnClickListener {

	ProgressDialog pDialog;
	String FoodDesc,TimetoExp,FeedCap,Address;
	Button b1;

	HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String addressJsonStr = null;

	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {				       
	
		//Inflate the layout for this fragment
		
		View root=inflater.inflate(R.layout.activity_donate, container, false);
		b1 = (Button)root.findViewById(R.id.button1);
		
		b1.setOnClickListener(b1click);
		return root;
			      
  }
	
	final OnClickListener b1click = new OnClickListener() {
        public void onClick(final View v) {
        	
        	
        	FoodDesc = ((EditText)getView().findViewById(R.id.editText1)).getText().toString();       	
        	TimetoExp = ((EditText)getView().findViewById(R.id.editText2)).getText().toString();
        	FeedCap = ((EditText)getView().findViewById(R.id.editText3)).getText().toString();
        	Address = ((EditText)getView().findViewById(R.id.editText4)).getText().toString();
//        	
        	new PostDetails().execute(Address,FoodDesc,TimetoExp,FeedCap);
        }
    };
	

    
    class PostDetails extends AsyncTask<String, String[], String[]>{
    	
    	  boolean failure = false;
    	  String lat,lon,city;
    	  
    	          @Override
    	          protected void onPreExecute() {
    	              super.onPreExecute();
    	              pDialog = new ProgressDialog(getActivity());
    	              pDialog.setMessage("Posting details...");
    	              pDialog.setIndeterminate(false);
    	              pDialog.setCancelable(true);
    	              pDialog.show();
    	          }

    	          
    	          private String[] getCoordDataFromJson(String AddressJsonStr)
    	                  throws JSONException {
    	        	  
    	        	  String res[]=new String[3];
    	        	  
    	              JSONObject AddressJson = new JSONObject(AddressJsonStr);
    	              JSONObject AddrComp = AddressJson.getJSONArray("results").getJSONObject(0);
    	              JSONObject results = AddrComp.getJSONArray("address_components").getJSONObject(2);
    	              city = results.getString("long_name");

    	              lat = AddrComp.getJSONObject("geometry").getJSONObject("location").getString("lat");
    	              lon = AddrComp.getJSONObject("geometry").getJSONObject("location").getString("lng");

    	              res[0]=city;
    	              res[1]=lat;
    	              res[2]=lon;
    	              
    	              return res;
    	          }
    	          
    	
		@Override
		protected String[] doInBackground(String... params) {


             final String BASE_URL="https://maps.googleapis.com/maps/api/geocode/json?";
             ApplicationInfo ai = null;
			try {
				ai = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             Bundle bundle = ai.metaData;
             String myApiKey = bundle.getString("com.google.android.maps.v2.API_KEY");
             final String APIKEY_PARAM=myApiKey;

             Uri builtUri= Uri.parse(BASE_URL).buildUpon()
                                 .appendQueryParameter("address",params[0])
                                 .appendQueryParameter("key",APIKEY_PARAM)
                                 .build();
             
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
            	 addressJsonStr = null;
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
             addressJsonStr = buffer.toString();

             try {
			
            	  resu= getCoordDataFromJson(addressJsonStr) ;
				
            	  
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
             
             List<NameValuePair> para = new ArrayList<NameValuePair>();
             para.add(new BasicNameValuePair("desc", params[1]));
             para.add(new BasicNameValuePair("time", params[2]));
             para.add(new BasicNameValuePair("feed", params[3]));
             para.add(new BasicNameValuePair("lat", resu[1]));
             para.add(new BasicNameValuePair("lon", resu[2]));

             JSONObject json = JSONParser.makeHttpRequest("http://cutmypie.com/webservices/insert.php", "POST", para);

             int success = 0;
			try {
				success = json.getInt("success");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
              if (success == 1) {
            	   Log.d("Database insert Successful!", json.toString());
              }
              else
              {
            	  Log.d("Database insert Failed!", json.toString());
              }

			return resu;    

		}
		
		protected void onPostExecute(String[] output) {
			
			            pDialog.dismiss();
			            
			            if (output != null){
			                Toast.makeText(getActivity(), "data inserted successfully", Toast.LENGTH_LONG).show();
			            }
			        }

    }



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
}
