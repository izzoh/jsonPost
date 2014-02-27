package com.example.jsonpost;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener {
	public static final int TIMEOUT_MILLISEC = 50000;
	Button send;
	Button receive;
	TextView results;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		send = (Button) findViewById(R.id.send);
		receive = (Button) findViewById(R.id.receive);
		results = (TextView) findViewById(R.id.results);
		send.setOnClickListener(this);
		receive.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.send:
			sendToJSON();
			break;
		case R.id.receive:
			receiveJSON();
			break;
		}
		
	}

	private void receiveJSON() {
		// TODO Auto-generated method stub
		try{
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
			HttpParams p = new BasicHttpParams();
			p.setParameter("user", "1");
			//initiate an HttpClient
			HttpClient httpclient = new DefaultHttpClient(p);
			String url = "http://10.0.2.2:8080/fav/"+
			"webservice.php?user=1&format=json";
			HttpPost httppost = new HttpPost(url);
			try {
				Log.i(getClass().getSimpleName(), "send task - start");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("user","1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				String responseBody = httpclient.execute(httppost,responseHandler);
				JSONObject json = new JSONObject(responseBody);
				JSONArray jArray = json.getJSONArray("posts");
				ArrayList<HashMap<String,String>> mylist = new ArrayList<HashMap<String,String>>();
				for (int i = 0; i<jArray.length();i++){
					HashMap<String, String> map = new HashMap<String, String>();
					JSONObject e = jArray.getJSONObject(i);
					String s = e.getString("post");
					JSONObject jObject = new JSONObject(s);
					
					map.put("idusers", jObject.getString("idusers"));
					map.put("UserName", jObject.getString("UserName"));
					map.put("FullName", jObject.getString("FullName"));
					mylist.add(map);
					results.setTag(mylist);
					
				}
				Toast.makeText(this, responseBody, Toast.LENGTH_LONG).show();
			}catch (ClientProtocolException e){
				e.printStackTrace();
			}catch (IOException e){
				e.printStackTrace();
			}
		}catch (Throwable t){
			Toast.makeText(this, "Request failed: "+ t.toString(), Toast.LENGTH_LONG).show();
		}
	}

	private void sendToJSON() {
		// TODO Auto-generated method stub
		try {
	        JSONObject json = new JSONObject();
	        json.put("UserName", "test2");
	        json.put("FullName", "1234567");
	        HttpParams httpParams = new BasicHttpParams();
	        HttpConnectionParams.setConnectionTimeout(httpParams,
	                TIMEOUT_MILLISEC);
	        HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
	        HttpClient client = new DefaultHttpClient(httpParams);
	        //
	        //String url = "http://10.0.2.2:8080/sample1/webservice2.php?" + 
	        //             "json={\"UserName\":1,\"FullName\":2}";
	        String url = "http://10.0.2.2:8080/sample1/webservice2.php";

	        HttpPost request = new HttpPost(url);
	        request.setEntity(new ByteArrayEntity(json.toString().getBytes(
	                "UTF8")));
	        request.setHeader("json", json.toString());
	        HttpResponse response = client.execute(request);
	        HttpEntity entity = response.getEntity();
	        // If the response does not enclose an entity, there is no need
	        if (entity != null) {
	            InputStream instream = entity.getContent();

	            String result = RestClient.convertStreamToString(instream);
	            Log.i("Read from server", result);
	            Toast.makeText(this,  result,
	                    Toast.LENGTH_LONG).show();
	        }
	    } catch (Throwable t) {
	        Toast.makeText(this, "Request failed: " + t.toString(),
	                Toast.LENGTH_LONG).show();
	    }
		
	}

}
