package com.example.dblan.autobuses;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



import android.util.Log;



import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


@SuppressWarnings("MissingPermission")
public class GPS_servei extends Service {
    private LocationListener listener;
    private LocationManager locationManager;
    private String dataActual;
    private  String matricula;
    public double latitud;
    public double longitud;

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

                //Iniciem la clase interna que realitza el servei.
                ConexionWeb con = new ConexionWeb();
                //Obtenim la latitud i la longitud
                latitud = location.getLatitude();
                longitud = location.getLongitude();

                //Recollim la data actual.
                DateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date today = Calendar.getInstance().getTime();
                dataActual = data.format(today);


                //Toast per veure si esta recollint les dades.
                Toast.makeText(GPS_servei.this, "latitud: " + location.getLatitude() + " logitud: " + location.getLongitude(),
                        Toast.LENGTH_LONG).show();
                //Executem la connexió.
                con.execute();
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
    public int onStartCommand(Intent intent, int flags, int startId) {

        matricula = intent.getStringExtra("matricula");

        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            locationManager.removeUpdates(listener);
        }
    }


    /**
     * Clase interna que realitza els insert into a la BBD externa.
     */
    private class ConexionWeb extends AsyncTask<Void, Void, Boolean> {

        public ConexionWeb() {

        }

        /**
         * Metode que realitza la connexió en segon pla.
         * @param params
         * @return
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            //Utilitzarem aques boolean per saber si s'ha realitzat la inserció a la BBDD.
            boolean resul;
            //Iniciem HttpCLient.
            HttpClient httpClient = new DefaultHttpClient();
            //Iniciem un HttpPost amb la nostra IP.
            HttpPost post = new HttpPost("http://192.168.120.156:8080/WebClientRest/webresources/generic");
            post.setHeader("content-type", "application/json");
            try {
                //Creem un jSon
                JSONObject ubicacio = new JSONObject();
                //Posem al objecte jSon la informació que volem insertar a la BBDD.
                ubicacio.put("matricula",matricula);
                ubicacio.put("latitud", latitud);
                ubicacio.put("longitud", longitud);
                ubicacio.put("hora", dataActual);

                //Convertim el Json a String.
                StringEntity entity = new StringEntity(ubicacio.toString());
                post.setEntity(entity);

                resul = true;
            } catch (Exception e) {
                //Excepció per si falla
                Log.e("ServicioRest", "Error!", e);
                resul = false;
            }
            return resul;
        }

        /**
         * Metode que ens indica mitjançant un toast si s'ha pogut realitzar la inserció de dades.
         * @param result
         */
        protected void onPostExecute(Boolean result) {

            if (result) {
                Toast.makeText(GPS_servei.this, "Inserció realitzada.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(GPS_servei.this, "No s'ha pogut realitzar l'inserció", Toast.LENGTH_LONG).show();
            }
        }


    }

}
