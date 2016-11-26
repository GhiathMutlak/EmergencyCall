package com.emergencycall;


import com.emergencycall.R;

import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreenActivity extends Activity {

	private Thread splashThread;
	private final int splashtime =3000;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		//no title bar
		requestWindowFeature( Window.FEATURE_NO_TITLE );
		
		//full screen 
		getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN, 0 );
		
		setContentView(R.layout.activity_splash_screen);
		
        splashThread = new Thread(){
			
			public  void run(){

				DataHandler handler = new DataHandler(getBaseContext());
				handler.open();
				Cursor c = handler.retrieveLogin();



				try{
					synchronized (this) {
						wait(splashtime);						
					}					
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				
				finally {

					finish();

					Intent i = new Intent();


						if ( c.getCount() == 0 ) {
							i.setClass( getApplicationContext(), MainActivity.class );
							startActivity(i);
						}
						else {
							i.setClass( getApplicationContext(), LocationActivity.class );
							startActivity(i);
						}

					handler.close();

				}				
			}			
		};
		
		splashThread.start();		
	}

	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}*/

}
