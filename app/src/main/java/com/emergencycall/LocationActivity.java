package com.emergencycall;



import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.text.DateFormat;



import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class LocationActivity extends Activity {

	// Progress Dialog
			private ProgressDialog pDialog;

			JSONParser jsonParser = new JSONParser();	
			
		    Handler serverHandler = new Handler();
		    Timer serverTimer = new Timer();
	        TimerTask toServerTimerTask;

		    Handler SQLiteHandler = new Handler();
		    Timer SQLiteTimer = new Timer();
		    TimerTask toSQLiteTimerTask;

		    boolean server = false;
		    boolean sqlite = false;
		    
			//Intent userIntent;
			String username; 
			
			static double lati;
			static double longi;
			
			static double lati2;
			static double longi2;
			
			//static Location location;

			TextView text1;
			TextView text2;
			TextView text3;
			TextView text4;
			
			Button start;
			Button end;
			Button map;

			private static String addLocationURL = "http://192.168.1.2/Add_location.php";
	        private static String retrieveLocationURL = "http://192.168.1.2/Retrive_data.php";
			private static final String TAG_SUCCESS = "success";
			
			
			
	
			@Override
			protected void onCreate(Bundle savedInstanceState) {
				
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_location);
		 
			//userIntent = getIntent();
			//username = userIntent.getStringExtra("username").toString();


			DataHandler handler = new DataHandler(getBaseContext());
			handler.open();
			Cursor c = handler.retrieveLogin();

				if ( c.moveToFirst() )
					username = c.getString(0);

				
			LocationManager service = ( LocationManager ) getSystemService( LOCATION_SERVICE );
		   		    
		         if( ! service.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
		        	
		        	Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
		        	startActivity( intent );
		        		
		        }
		         
		    text1 = (TextView)findViewById(R.id.textView1);
		    text2 = (TextView)findViewById(R.id.textView2);
		    text3 = (TextView)findViewById(R.id.textView3);
		    text4 = (TextView)findViewById(R.id.textView4);
		        
		    start = (Button)findViewById(R.id.start);
		    end = (Button)findViewById(R.id.end);
			map = (Button) findViewById(R.id.map);
	        
		    end.setEnabled(false);
	     
	        LocationListener listener = new  LocationListener() {
				
				@Override
				public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProviderEnabled(String arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProviderDisabled(String arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLocationChanged(Location location) {
					
					set(location);
					
				}
				
				private void set(Location location){
				
					lati = location.getLatitude();
					longi = location.getLongitude();
					
					String la = location.getLatitude()+"";
					text1.setText("Latitude  : "+la);
					
					String lo = location.getLongitude()+"";
					text2.setText("Longitude : "+lo);
					
					
				}
			};
			
			service.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, listener );
	        service.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, listener );


	        start.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

					lati2 = lati;
					longi2 = longi;

					text3.setText("Start point saved");

					start.setEnabled(false);
					end.setEnabled(true);

					start();

				}
			});
	        


	        end.setOnClickListener(new OnClickListener() {
	       
				@Override
				public void onClick(View arg0) {
					
					try {
					
						if ( isNetworkAvailable() ) {
							
							double distance = Math.sqrt( Math.pow( (lati2-lati), 2) + Math.pow( (longi2-longi), 2) );
							
							DecimalFormat df = new DecimalFormat("#.##");
						    df.setRoundingMode( RoundingMode.CEILING );
						    
							text3.setText("Distance in meters  "+ df.format(distance) );
							text4.setText( getAddress(lati2,longi2) );
						
						    }
						 
						start.setEnabled(true);
						end.setEnabled(false);
					
						   if ( server ) {
						      toServerTimerTask.cancel();
						      server = false;
						   }
						   
						   if( sqlite ) {
					           toSQLiteTimerTask.cancel();
					           sqlite = false;
						   }



						   /*
						DataHandler handler = new DataHandler( getBaseContext() );
						handler.open();
						Cursor c = handler.retrieveLocation();
						String getName = "";
						String getEmail = "";
						
						   if (c.moveToFirst()) {
							   
							   do {
								   
								  getName = c.getString(0);
								  getEmail = c.getString(4);
								  Toast.makeText(getBaseContext(), "name:"+getName + "email"+getEmail, Toast.LENGTH_LONG).show();
										  
							   }while(c.moveToNext());
							   
						   }
						   
						handler.close();
						*/
					
						
					} catch (IOException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						Log.d(" cance ","eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
					}
				}
			});

			map.setOnClickListener( new OnClickListener() {

					@Override
					public void onClick(View view) {
						if( isNetworkAvailable() && isOnline() ) {

                    new RetrieveLocation().execute();
						}
						else
							Toast.makeText( getApplicationContext(), "No connection", Toast.LENGTH_LONG).show();

								}
							}
				);
		
		
		
			}






			
			
			
	
			private void start () {		
				
				DataHandler handler = new DataHandler( getBaseContext() );
				handler.open();
				Cursor cursor = handler.retrieveLocation() ;
				int SQLiteRowCount = cursor.getCount() ;
				handler.close();
				
				
					if ( isNetworkAvailable() ) {
						
						if ( SQLiteRowCount == 0 ) 
							saveToServer();
						else 
							sendFromSQLite();
						
					}
					
					else
						saveToDevice();
					   // Toast.makeText(getApplicationContext(), "saveToDevice();  ", Toast.LENGTH_LONG).show();
						
						
				
			}
	
			
			
			
			
			private void saveToDevice () {		
				
				toSQLiteTimerTask = new TimerTask() {       
			        @Override
			        public void run() {
			        	
			            SQLiteHandler.post( new Runnable() {
			           
			            	public void run() {       
			               
			                	try {
			                   
			                		  if ( !isNetworkAvailable() ) {
			                            
			                        	  String [] vars = getVars();
			                        	  DataHandler handler = new DataHandler(getBaseContext());
			                        	
			                        	//  Toast.makeText(getApplicationContext(), "Insert to sqlite", Toast.LENGTH_LONG).show();
			                        	  			                       
			                        	  handler.open();
			                        	  handler.insertLocation(vars[0], vars[1], vars[2], vars[3], vars[4]);
			                        	  handler.close();
			                      	
			                          } 
			                		  
			                          else {
			                        	  
			                        	  toSQLiteTimerTask.cancel();
			                        	//  Toast.makeText(getApplicationContext(), "Start() in saveToDevice ", Toast.LENGTH_LONG).show();
			                         	  start();  
			                         	  
			                          }
			                        	              
			                    } catch (Exception e) {
			                    	Log.d("yes","noooooooooooooooooo");
			                    }
			                }
			            });
			        }
			    };
			 
			    SQLiteTimer.schedule(toSQLiteTimerTask, 0, 10000);
			    sqlite = true;
				
			}
	
	
			private void saveToServer() {
				
				toServerTimerTask = new TimerTask() {       
			        @Override
			        public void run() {
			        	
			            serverHandler.post(new Runnable() {
			                public void run() {       
			                    try {
			                    	
			                          if ( isNetworkAvailable() ) 
			                        	  new AddNewLocation().execute();
			                         
			                          else {
			                        	  toServerTimerTask.cancel();
			                        	  //Toast.makeText(getApplicationContext(), "start()   ", Toast.LENGTH_LONG).show();
			                        	  start();  
			                          }
			                        	              
			                    } catch (Exception e) {
			                    	Log.d("yes","noooooooooooooooooo");
			                    }
			                }
			            });
			        }
			    };
			 
			    serverTimer.schedule( toServerTimerTask, 0, 10000);
			    server = true;
			  
				
			}
	
			
			
		    private void sendFromSQLite() {
		    	
		    //	Toast.makeText(getApplicationContext(), "sendFromSQLite ", Toast.LENGTH_LONG).show();	
		    	DataHandler handler = new DataHandler(getBaseContext());
				handler.open();
				Cursor c = handler.retrieveLocation();
				int SQLiteRowCount  = c.getCount() ;
				
				String username = "";
				String latitude = "";
				String longitude = "";
				String date = "";
				String time = "";
				String address="";
			
				//handler.deleteRow("a");
				//int i=0;
			
			
				   if ( c.moveToFirst() ) {
					   
					   do {
						   	  
						  // should be in  if
						  username = c.getString(0);
						  latitude = c.getString(1);
						  longitude = c.getString(2);
						  date = c.getString(3);
						  time = c.getString(4);
						  String vars[] = { username, latitude, longitude, date, time };
					   
						   try {
							   
							address = getAddress( Double.parseDouble( latitude ), Double.parseDouble( longitude ) );
							address = removeChar(address,'\'');
						
							if( isNetworkAvailable() && SQLiteRowCount!=0 ) {
							 
								new AddNewLocation( vars, address ).execute();
								//---------------------------------------delete-----------<<<<<<<<<<<<<<<<<<<
								handler.deleteLocationRow(time);
								SQLiteRowCount--;
							    //Toast.makeText(getBaseContext(), "name:  "+ username+ "  time"+time  +SQLiteRowCount, Toast.LENGTH_LONG).show();
							
							   }
							
							else if( !isNetworkAvailable() ) {
							      
								//Toast.makeText(getBaseContext(), "statr()in sendFromSQLite" , Toast.LENGTH_LONG).show();
								start();
								
							}
							
							   
						   }	 
						 catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						 catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						   
						    
						  // i++;
						   
						 //Toast.makeText(getBaseContext(), "name:"+ username+ "time  "+time, Toast.LENGTH_LONG).show();
								  
					   } while( c.moveToNext() );
					   
				   }
				   
				   
				handler.close();
				
				     
						//Toast.makeText(getBaseContext(), "statr()in last" , Toast.LENGTH_LONG).show();
						start();
						
				
			}
	
	
	
		 	private boolean isNetworkAvailable() {
		 		
			    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( MainActivity.CONNECTIVITY_SERVICE );
			    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
			    
			    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
		
		 	}



			private  boolean isOnline () {

				 Runtime runtime = Runtime.getRuntime();

				try {

					Process ipProcess = runtime.exec( "/system/bin/ping -c 1 8.8.8.8" );
					int exitValue = ipProcess.waitFor();

					return ( exitValue == 0 );
				}
				catch ( IOException e ) {
					e.printStackTrace();
				}
				catch ( InterruptedException e ) {
					e.printStackTrace();
				}

				return false;

			}



			private String getAddress( double latitude , double longitude ) throws IOException {

				Geocoder geocoder;
		    	List<Address> addresses;
		    	geocoder = new Geocoder(this, Locale.getDefault());
		    	addresses = geocoder.getFromLocation(latitude, longitude, 1);
		
		    	String address = addresses.get(0).getAddressLine(0);
		    	String city = addresses.get(0).getAddressLine(1);
		    	String country = addresses.get(0).getAddressLine(2);
		    	String all_add = country+" "+city+" "+address;
		    	return all_add;
		    	
		    }



			private String [] getVars() {
				
				String latitude;
				String longitude;
				String date;
				String time;
								 
				String [] vars = new String [5] ;
				
				latitude = String.valueOf(lati);
				longitude = String.valueOf(longi);
				 
			    DateFormat dateInstance = SimpleDateFormat.getDateInstance();
			    date = dateInstance.format( Calendar.getInstance().getTime() );
			    
			    DateFormat timeInstance = SimpleDateFormat.getTimeInstance();
			    time = timeInstance.format( Calendar.getInstance().getTime() );
			    
			   
				vars = new String [] { username , latitude , longitude , date , time };			
					
				return vars;
				
			}





	 		private  String removeChar( String s, char c ) {
				
		        StringBuffer buffer = new StringBuffer( s.length() );
		        buffer.setLength( s.length() );
		        int current = 0;
		        
			        for ( int i = 0; i < s.length(); i++) {
			        	
			            char cur = s.charAt(i);
			            
			            	if( cur != c ) 
			            		buffer.setCharAt( current++, cur );
			            
			        }
			        
		        
			        return buffer.toString();
		    }
			
			
			
			/**
			 * Background Async Task to Create new product
			 * */
			
			class AddNewLocation extends AsyncTask<String, String, String> {
				
				int success;
				String [] vars;
				String address;
				
				
				public  AddNewLocation() {
					
					this.vars = getVars();
					
					try {
						
						this.address = getAddress( lati, longi );
						this.address = removeChar( this.address, '\'' );
						
					    } catch ( NumberFormatException e1 ) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
					    } catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
					    }
				}
				
				
				public  AddNewLocation( String [] vars, String address ) {
					
					this.vars = vars;
					this.address = address;
				
				}
				
				
				/**
				 * Before starting background thread Show Progress Dialog
				 * */
				@Override
			/*	protected void onPreExecute() {
					super.onPreExecute();
					pDialog = new ProgressDialog(LocationActivity.this);
					pDialog.setMessage("save location...");
					pDialog.setIndeterminate(false);
					pDialog.setCancelable(true);
					pDialog.show();
				}
				*/
		
				
				
				/**
				 * Adding new location
				 * */
				protected String doInBackground( String... args ) {
								
					
					// Building Parameters
					
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					
					params.add( new BasicNameValuePair( "username", vars[0] ) );
					params.add( new BasicNameValuePair( "latitude", vars[1] ) );
					params.add( new BasicNameValuePair( "longitude", vars[2] ) );
					params.add( new BasicNameValuePair( "date", vars[3] ) );
					params.add( new BasicNameValuePair( "time", vars[4] ) );
					params.add( new BasicNameValuePair( "address", address ) );
					
					//Log.d("time date", address+ vars[4]+vars[3]);
		
					
		
					// getting JSON Object
					JSONObject json = jsonParser.makeHttpRequest( addLocationURL, "POST", params );
					
					// check log cat for response
					Log.d( "Create Response", json.toString() );
		
					// check for success tag
					try {
						
						 success = json.getInt( TAG_SUCCESS );
		
						if ( success == 1 ) {
							 
							// successfully created product
							//check.setText("add success");
							Log.d(" success","add row success");
							// successfully created product
							
						//	Intent i = new Intent(MainActivity.this, LocationActivity.class);
						//	i.putExtra("username", user);
						//	startActivity(i);
		
							// closing this screen
						//	finish();
			
							// closing this screen
						} else {
							// failed to create product
							Log.d(" NOT success","row NOT add success");				
							
						}
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
		
					return null;
				}
				
		
				/**
				 * After completing background task Dismiss the progress dialog
				 * **/
			/*	protected void onPostExecute(String file_url) {
					// dismiss the dialog once done
					pDialog.dismiss();
				if(success==0)
					{
					Toast.makeText(getApplicationContext(), "Not add row", Toast.LENGTH_LONG).show();
					}
					
				}*/
				
				
			    
			}
	

			public void onBackPressed (  ) {

				moveTaskToBack(true);

			}


	

			@Override
			public boolean onCreateOptionsMenu( Menu menu ) {

				// Inflate the menu; this adds items to the action bar if it is present.
				getMenuInflater().inflate(R.menu.location, menu);
				return true;

			}


			@Override
			public boolean onOptionsItemSelected(MenuItem item) {

					switch ( item.getItemId() ) {

						case R.id.exit:
							this.finish();
							return true;

						case R.id.action_settings: {Intent i = new Intent();
							i.setClass( this.getBaseContext() ,Setting.class);
							startActivity( i ); ;return true;}


						case R.id.about: ;{Intent i = new Intent();
							i.setClass( this.getBaseContext() ,aboutActivity.class);
							startActivity( i ); ;return true;}

						case R.id.logout: ;
							logout();
							return true;

						default:
							return super.onOptionsItemSelected( item );
					}


			}


			public void logout () {

				DataHandler handler = new DataHandler( getBaseContext() );
				handler.open();
				handler.logout();
				handler.close();

				Intent i = new Intent();
				i.setClass( this.getBaseContext() , MainActivity.class );
				startActivity( i );

				finish();

			}




	class RetrieveLocation extends AsyncTask<String, String, String> {

		int success;
		String username;
		String latitude;
		String longitude;
		String time;
		String date;
		String address;

		private static final String TAG_SUCCESS = "success";
		private static final String TAG_message = "message";
		private static final String TAG_location = "location";

		private static final String TAG_username = "username";
		private static final String TAG_latitude = "latitude";
		private static final String TAG_longitude = "longitude";
		JSONArray locations = null;
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


			username="a";
			date = "May 18, 2015";///<<<<<----------------------------------------------------------

			DataHandler handler = new DataHandler(getBaseContext());
			handler.open();
			Cursor cursor = handler.retrieveLogin();

			if ( cursor.moveToFirst() )
				username = cursor.getString(0);



			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add( new BasicNameValuePair( "username", username ) );
			params.add( new BasicNameValuePair( "date", date ) );


			// getting JSON Object
			JSONObject json = jsonParser.makeHttpRequest(retrieveLocationURL, "POST", params );

			// check log cat fro response
			Log.d("Create Response", json.toString());

			// check for success tag
			try {

				success = json.getInt(TAG_SUCCESS);

				if ( success == 1 ) {


					Log.d(" success", "Check success Go To MAP Activity");
				Intent i = new Intent();
					i.setClass( LocationActivity.this, Map.class );
					startActivity(i);

				} else {
					// failed to create product
					Log.d(" NOT success", "Check NOT success map map map map map");


				}

			} catch ( JSONException e ) {
				e.printStackTrace();
			}

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/

       protected void onPostExecute(String file_url) {
            // dismiss the dialog once done
           // pDialog.dismiss();
            if(success==0){
               Toast.makeText(getApplicationContext(), "Not Found Path ", Toast.LENGTH_LONG).show();
            }

        }


	}

}