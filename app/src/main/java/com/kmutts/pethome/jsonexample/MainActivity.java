package com.kmutts.pethome.jsonexample;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends Activity {

	private ListView jsonListview;
	private ArrayList<String> exData;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_main);

		jsonListview = (ListView) findViewById(R.id.json_listview);

		exData = new ArrayList<String>();
		//exData.add("S1");
		//exData.add("S2");
		//exData.add("S3");
		//exData.add("S4");
		//exData.add("S5");

		new AsyncTask<Void,Void,Void>(){

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setCancelable(false);
				progressDialog.setMessage("Downloading ...");
				progressDialog.show();
			}

			@Override
			protected Void doInBackground(Void... params) {
				try {
					URL url = new URL("http://pethome.kmutts.com/post_json.php");

					URLConnection urlConnection = url.openConnection();

					HttpURLConnection httpURLConnection = (HttpURLConnection)urlConnection;
					httpURLConnection.setAllowUserInteraction(false);
					httpURLConnection.setInstanceFollowRedirects(true);
					httpURLConnection.setRequestMethod("GET");
					httpURLConnection.connect();

					InputStream inputStream = null;

					if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK)
						inputStream = httpURLConnection.getInputStream();

					BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"),8);

					StringBuilder stringBuilder = new StringBuilder();
					String line = null;

					while ((line=reader.readLine()) != null){
						stringBuilder.append(line + "\n");
					}
					inputStream.close();
					Log.d("JSON Result",stringBuilder.toString());

					JSONObject jsonObject = new JSONObject(stringBuilder.toString());
					JSONArray exArray = jsonObject.getJSONArray("result");

					for(int i=0;i<exArray.length();i++){
						JSONObject jsonObj = exArray.getJSONObject(i);
						exData.add(jsonObj.getString("postname"));
					}

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return null;
			}


			@Override
			protected void onPostExecute(Void aVoid) {
				super.onPostExecute(aVoid);

				ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,android.R.id.text1,exData);
				jsonListview.setAdapter(myAdapter);

				progressDialog.dismiss();
			}
		}.execute();


		/*Button btPost = (Button) findViewById(R.id.btPost);
		btPost.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,PostActivity.class);
				startActivity(intent);
			}
		});*/
	}
	public void OpenPost(View view){
		startActivity(new Intent(this,PostActivity.class));
	}

}
