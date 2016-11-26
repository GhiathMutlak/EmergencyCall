package com.emergencycall;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


public class Map extends Activity implements OnMapReadyCallback {


    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();

    //ArrayList<HashMap<String, String>> locationsList;
    ArrayList<LatLng> locationsList;



    //url to get all location list
    private static String retrieveLocationURL = "http://192.168.1.2/Retrive_data.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_message = "message";
    private static final String TAG_location = "location";

    private static final String TAG_username = "username";
    private static final String TAG_latitude = "latitude";
    private static final String TAG_longitude = "longitude";
    private static final String TAG_time= "time";
    private static final String TAG_date = "date";
    private static final String TAG_address = "address";

    // location JSONArray
    JSONArray locations = null;



    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationsList = new ArrayList<LatLng>();//<<<<<<<<<<<<<<<<<<<---------------------------------------------------------


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady( GoogleMap map ) {

        new RetrieveLocation().execute();

       while(locationsList.size()<=0)
           ;
        LatLng  coordinate;
        PolylineOptions polylineOptions=new PolylineOptions();
        String latitude;
        String longitude;
       // LatLng latLng;

        map.setMyLocationEnabled(true);
        LatLng Homs = new LatLng(34.7215243, 36.7139401);
        map.moveCamera( CameraUpdateFactory.newLatLngZoom( Homs, 13));
        Log.d("size   ----------  size", String.valueOf(locationsList.size()));
      //  Iterator iterator=locationsList.iterator();

        for(int i=0;i<locationsList.size();i++){
      // while(iterator.hasNext()) {
        //   object=iterator.next();

       coordinate=  locationsList.get(i);

        //   latitude=coordinate.get(TAG_latitude);
         //   longitude=coordinate.get(TAG_longitude);

            latitude = String.valueOf(coordinate.latitude);


            Log.d("success","oooooooooooooooooooooooooooooooooooooooo"+latitude);
         //   latLng=new LatLng(Double.parseDouble( latitude ), Double.parseDouble( longitude ));
            polylineOptions.geodesic(true).add(coordinate);
          //  map.addMarker(new MarkerOptions()
                //    .title("Homs")
                 //   .snippet("The most beautiful city in Syria")
                 //   .position(coordinate));
         //   map.addPolyline(polylineOptions);
            if(i==0){
            map.addMarker(new MarkerOptions()
                    .title("Start Path")
                    .snippet(" ")
                    .position(coordinate));
            map.addPolyline(polylineOptions);}
            if(i==locationsList.size()-1){
                map.addMarker(new MarkerOptions()
                        .title("End Path")
                        .snippet(" ")
                        .position(coordinate));
                map.addPolyline(polylineOptions);}

        }


      /*  LatLng Homs = new LatLng( 34.7158177, 36.7106063 );

        map.setMyLocationEnabled( true );
        map.setMapType( GoogleMap.MAP_TYPE_SATELLITE );

        map.moveCamera( CameraUpdateFactory.newLatLngZoom( Homs, 13));

        map.addMarker( new MarkerOptions()
                .title("Homs")
                .snippet("The most beautiful city in Syria")
                .position( Homs ) );

*/
        /*
        map.moveCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(-18.142, 178.431), 2));

        // Polylines are useful for marking paths and routes on the map.
        map.addPolyline(new PolylineOptions().geodesic(true)
                .add(new LatLng(-33.866, 151.195))  // Sydney
                .add(new LatLng(-18.142, 178.431))  // Fiji
                .add(new LatLng(21.291, -157.821))  // Hawaii
                .add(new LatLng(37.423, -122.091))  // Mountain View
        );*/

    }






    /**
     * Background Async Task to Create new product
     * */
    class RetrieveLocation extends AsyncTask<String, String, String> {

        int success;
        String Username;
        String latitude;
        String longitude;
        String time;
        String date;
        String address;



        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        /*
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getBaseContext());
            pDialog.setMessage("Please Wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
                           */




        protected String doInBackground(String... args) {

          ///  String user = "a";
            String user=" ";
            date = "May 18, 2015";///<<<<<----------------------------------------------------------

            DataHandler handler = new DataHandler(getBaseContext());
            handler.open();
            Cursor cursor = handler.retrieveLogin();

            if ( cursor.moveToFirst() )
                 user = cursor.getString(0);



            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add( new BasicNameValuePair( "username", user ) );
            params.add( new BasicNameValuePair( "date", date ) );


            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(retrieveLocationURL, "POST", params );

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {

                success = json.getInt(TAG_SUCCESS);

                if ( success == 1 ) {


                    Log.d(" success", "Check success");


                    locations = json.getJSONArray(TAG_location);
                   // HashMap<String, String> hashMap = new HashMap<String, String>();
                    // looping through All courses
                    LatLng latLng;

                    for (int i = 0; i < locations.length(); i++)//course JSONArray
                    {
                        JSONObject c = locations.getJSONObject(i); // read first

                        // Storing each json item in variable
                      //  Username = c.getString(TAG_username);
                        latitude = c.getString(TAG_latitude);
                        longitude = c.getString(TAG_longitude);
                       // date = c.getString(TAG_date);
                      //  time = c.getString(TAG_time);
                      //  address = c.getString(TAG_address);



                        latLng = new LatLng(Double.parseDouble( latitude ), Double.parseDouble( longitude ));



                        // adding HashList to ArrayList


                       locationsList.add( latLng );
                      //  Log.d("lat", latitude   + "  "+hashMap.get(TAG_latitude));

                    }
for (int i=0;i<locationsList.size();i++){

    Log.d("lat", String.valueOf(locationsList.get(i).latitude));

}

                } else {
                    // failed to create product
                    Log.d(" NOT success", "Check NOT success map map map map map");
                   // Intent i = new Intent( Map.this,LocationActivity.class );
                    //i.putExtra( "username", user );
                  //  startActivity(i);

                    // closing this screen
                   // finish();

                }

            } catch ( JSONException e ) {
                e.printStackTrace();
            }

            return null;
        }


        /**
         * After completing background task Dismiss the progress dialog
         * **/

       /* protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
            pDialog.dismiss();
            if(success==0){
                Toast.makeText(getApplicationContext(), "Invalid values", Toast.LENGTH_LONG).show();
            }

        }*/


    }




}
