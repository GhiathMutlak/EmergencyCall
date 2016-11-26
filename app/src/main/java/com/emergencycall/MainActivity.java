package com.emergencycall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


public class MainActivity extends Activity {

	// Progress Dialog
	private ProgressDialog pDialog;

	JSONParser jsonParser = new JSONParser();

	EditText username;
	EditText password;
	TextView check;
	Button login;
	Button register;


	//private static String addLocationURL = "http://amrghiath.890m.com/create_product.php"-------?id=57&x=uu;
	private static String checkLoginURL = "http://192.168.1.2/Check_Login.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		// Edit Text
		username = (EditText) findViewById(R.id.editText1);
		password = (EditText) findViewById(R.id.editText2);

		//  Buttons
		login = (Button) findViewById(R.id.start);
		register = (Button) findViewById(R.id.end);

		//textview
		check = (TextView) findViewById(R.id.textView1);


		register.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				if (isNetworkAvailable() && isOnline()) {

					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://192.168.1.2/Login/register.php"));
					startActivity(browserIntent);

				} else
					Toast.makeText(getApplicationContext(), "No Connection", Toast.LENGTH_LONG).show();

			}
		});


		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {


				String user = username.getText().toString();
				String pass = password.getText().toString();

				hideKeyboard();


				//Log.d(" NOT networkConnection","networkConnection");

				if (isNetworkAvailable() && isOnline()) {

					if (pass.equals("") && user.equals(""))
						Toast.makeText(getApplicationContext(), "Please Enter all fields", Toast.LENGTH_LONG).show();

					else if (pass.equals(""))
						Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_LONG).show();

					else if (user.equals(""))
						Toast.makeText(getApplicationContext(), "Please Enter Username", Toast.LENGTH_LONG).show();

					else
						new CheckLogin().execute();

				} else
					Toast.makeText(getApplicationContext(), "No connection", Toast.LENGTH_LONG).show();

			}
		});

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.emergencycall/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Main Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://com.emergencycall/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}


	/**
	 * Background Async Task to Create new product
	 */
	class CheckLogin extends AsyncTask<String, String, String> {

		int success;


		/**
		 * Before starting background thread Show Progress Dialog
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}


		protected String doInBackground(String... args) {

			String user = username.getText().toString();
			String pass = password.getText().toString();

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("username", user));
			params.add(new BasicNameValuePair("password", pass));


			// getting JSON Object
			JSONObject json = jsonParser.makeHttpRequest(checkLoginURL, "POST", params);

			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {
				success = json.getInt(TAG_SUCCESS);

				if (success == 1) {


					Log.d(" success", "Check success");


					DataHandler handler = new DataHandler(getBaseContext());

					handler.open();
					handler.insertLogin(user);
					handler.close();

					Intent i = new Intent(MainActivity.this, LocationActivity.class);
					//i.putExtra( "username", user );
					startActivity(i);

					// closing this screen
					finish();

					// closing this screen
				} else {
					// failed to create product
					Log.d(" NOT success", "Check NOT success");

				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 **/

		protected void onPostExecute(String file_url) {
			// dismiss the dialog once done
			pDialog.dismiss();
			if (success == 0) {
				Toast.makeText(getApplicationContext(), "Invalid values", Toast.LENGTH_LONG).show();
			}

		}


	}


	private boolean isNetworkAvailable() {

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}


	private boolean isOnline() {

		Runtime runtime = Runtime.getRuntime();

		try {

			Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
			int exitValue = ipProcess.waitFor();

			return (exitValue == 0);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return false;

	}


	public void hideKeyboard() {

		InputMethodManager mgr = (InputMethodManager) getSystemService(MainActivity.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(username.getWindowToken(), 0);

	}
			
	
			/*
				@Override
				public boolean onCreateOptionsMenu(Menu menu) {
					// Inflate the menu; this adds items to the action bar if it is present.
					getMenuInflater().inflate(R.menu.main, menu);
					return true;
				}
			*/

}
