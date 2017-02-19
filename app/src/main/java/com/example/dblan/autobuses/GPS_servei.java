package com.example.dblan.autobuses;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("MissingPermission")
public class GPS_servei extends Service {
    private LocationListener listener;
    private LocationManager locationManager;
    private String dataActual;



    public GPS_servei() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent i = new Intent("location_update");

                //Recollim la data actual.
                DateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date today = Calendar.getInstance().getTime();
                dataActual = data.format(today);




                //Toast per veure si esta recollint les dades.
                Toast.makeText(GPS_servei.this, "latitud: " + location.getLatitude() + " logitud: " + location.getLongitude(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };


        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            locationManager.removeUpdates(listener);
        }
    }
}
