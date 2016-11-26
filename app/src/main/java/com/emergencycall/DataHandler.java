package com.emergencycall;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DataHandler {
	
	
			public static final String USER_NAME = "username";
			public static final String LATITUDE = "latitude";
			public static final String LONGITUDE = "longitude";
			public static final String TIME = "time";
			public static final String DATE = "date";

			public static final String INTERARRIVAL = "interarrival";

			public static final String LOCATION = "location";
			public static final String SETTING = "setting";
			public static final String LOGIN = "login";

			public static final String DATABASE_NAME = "emergency";
			public static final int DATABASE_VERSION = 1;

			public static final String CREATE_LOCATION = "create table location (username text not null, latitude text, longitude text" +
					",time text, date text) ";
			public static final String CREATE_LOGIN = "create table login ( username text not null  )";
			public static final String CREATE_SETTING = "create table setting (interarrival integer)";

			DatabaseHelper dbHelper;
			Context context;
			SQLiteDatabase db;




			public DataHandler(Context context) {
				super();
				this.context = context;
				dbHelper = new DatabaseHelper(context);
			}



			private static class DatabaseHelper extends SQLiteOpenHelper {


				public  DatabaseHelper(Context context) {
					super(context ,DATABASE_NAME ,null ,DATABASE_VERSION);
				}

				@Override
				public void onCreate(SQLiteDatabase db) {
					// TODO Auto-generated method stub

					try {

					db.execSQL( CREATE_LOCATION );
					db.execSQL( CREATE_SETTING );
					db.execSQL( CREATE_LOGIN );

					}
					catch (SQLException e) {
						e.printStackTrace();
					}
				}


				@Override
				public void onUpgrade( SQLiteDatabase db, int arg1, int arg2 ) {
					// TODO Auto-generated method stub

					 db.execSQL( "DROP TABLE IF EXISTS location" );
					 db.execSQL( "DROP TABLE IF EXISTS setting" );
					 db.execSQL( "DROP TABLE IF EXISTS login" );
					 onCreate( db );

				}




			}

				public DataHandler open() {

					db = dbHelper.getWritableDatabase();
					return this;
				}

				public void close() {

					this.dbHelper.close();

				}


				public long insertLocation( String username , String latitude,String longtiude,String date ,String time ) {

					ContentValues content = new ContentValues();
					content.put( USER_NAME, username );
					content.put( LATITUDE, latitude );
					content.put( LONGITUDE, longtiude );
					content.put( DATE, date );
					content.put(TIME, time);
					return db.insertOrThrow( LOCATION, null, content );

				}


				public long insertSetting( String interarrival ) {

				ContentValues content = new ContentValues();
				content.put( INTERARRIVAL, interarrival );

				return db.insertOrThrow( SETTING, null, content );

			}


			public long insertLogin( String username ) {

				ContentValues content = new ContentValues();
				content.put( USER_NAME , username );

				return db.insertOrThrow( LOGIN , null, content );

			}

			public Cursor retrieveLocation() {

					return db.query(LOCATION, new String[]{USER_NAME, LATITUDE, LONGITUDE, DATE, TIME}, null, null, null, null, null, null);

			}

			public Cursor retrieveSetting() {

						return db.query( SETTING, new String [] { INTERARRIVAL } ,null , null, null, null, null, null );

			}

			public Cursor retrieveLogin() {

				return db.query( LOGIN, new String [] { USER_NAME } ,null , null, null, null, null, null );

			}

			////    Delete row from database
			public boolean deleteLocationRow( String time  )  {

					//String name,String latitude,
					//return db.delete(LOCATION, USER_NAME+"='"+ name+"' and "+LATITUDE+"='"+latitude+"' and "+TIME+"='"+time+"'", null) > 0;

					return db.delete( LOCATION, TIME + "='" + time + "'", null ) > 0;
			}

			public int logout() {

				  return db.delete( LOGIN, null, null );

			}


}
