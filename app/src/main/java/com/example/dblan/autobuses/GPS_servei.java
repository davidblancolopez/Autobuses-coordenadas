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
    private  String matricula;

    private SQLiteDatabase db;

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
                DateFormat data = new SimpleDateFormat("HH:mm:ss");
                Date today = Calendar.getInstance().getTime();
                dataActual = data.format(today);


                MainActivity.Coordenada cordenadaActual;

                cordenadaActual = new MainActivity.Coordenada( matricula, location.getLatitude(), location.getLongitude(), dataActual);

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





    /*private class ConexionWebService extends AsyncTask<Void, Void, Boolean> {

        public ConexionWebService() {

        }*/

        /**
         * Metodo que realiza toda la tarea relacionada con el servicio POST en segundo plano.
         * @param params
         * @return
         */
        /*@Override
        protected Boolean doInBackground(Void... params) {
            //Boolean utilizado para saber si se ha insertado o no la ubicacion
            boolean resul;
            //Inicializamos el tipo HttpClient
            HttpClient httpClient = new DefaultHttpClient();
            //Creamos un HttpPost con la IP de nuestro WebService para realizar los Insert Intos
            HttpPost post = new HttpPost("http://192.168.1.46:8080/WebClientRest/webresources/mapas");
            post.setHeader("content-type", "application/json");
            try {
                //Creamos un objeto JSON
                JSONObject ubicacion = new JSONObject();
                //Introducimos el objeto JSON los atributos que queremos que tenga
                ubicacion.put("matricula",matricula);
                ubicacion.put("latitud", latitud);
                ubicacion.put("longitud", longitut);
                ubicacion.put("data", date);
                //Creamos un tipo StringEntity para convertir el JSON a String
                StringEntity entity = new StringEntity(ubicacion.toString());
                post.setEntity(entity);
                //Creamos un HttpResponse para ejecutar la sentencia POST
                HttpResponse resp = httpClient.execute(post);
                resul = true;
            } catch (Exception e) {
                Log.e("ServicioRest", "Error!", e);
                resul = false;
            }
            return resul;
        }*/

        /**
         * Metodo que se realiza despues de ejecutarse el metodo onBackground para decirnos basicamente
         * Si se ha realizado o no el Insert Into
         * @param result
         */
        /*protected void onPostExecute(Boolean result) {

            if (result) {
                Toast.makeText(GeoLocalizacion.this, "Insertado OK", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(GeoLocalizacion.this, "No insertado", Toast.LENGTH_SHORT).show();
            }
        }


    }*/

}
