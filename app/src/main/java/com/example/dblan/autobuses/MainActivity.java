package com.example.dblan.autobuses;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements View.OnClickListener {
    private SQLiteDatabase db;
    public String tvCon;
    private Button btn_start, btn_stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicialitzem els botons.
        btn_start = (Button) findViewById(R.id.buttonentrar);
        btn_stop = (Button) findViewById(R.id.buttonSalir);



        //Obrim la BBDD interna en mode escritura.
        BD usdbh =
                new BD(this, "DBAutobuses", null, 1);

        db = usdbh.getWritableDatabase();



        if(runtime_permissionss()){
            enableButtons();
        }



    }


    /**
     * Metode que inicia els botons.
     */
    private void enableButtons() {
        Button btnSubmit = (Button) findViewById(R.id.buttonentrar);
        Button btnStop = (Button) findViewById(R.id.buttonSalir);
        btnSubmit.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }


    /**
     *onClick per a quan es pulsa 1 dels 2 botons.
     * Si es pulsa start es fara la comprovació de l'usuari i contrasenya per a comprobar que son
     * correctes i llavors iniciar el servei que recull les coordenades del dispositiu.
     * @param v
     */
    @Override
    public void onClick(View v){

        if (v.getId() == R.id.buttonentrar) {
            EditText etMatricula = (EditText) findViewById(R.id.etMatricula);
            EditText etContrasenya = (EditText) findViewById(R.id.etContrasenya);

            String matricula;
            String contrasenya;


            matricula = etMatricula.getText().toString();
            contrasenya = etContrasenya.getText().toString();



            //Condició per comprobar que l'usuari no deixa cap camp necessari buit.
            if ( (!matricula.equals("")) && (!contrasenya.equals("")) ) {

                boolean permis = comprovacio(matricula, contrasenya);
                if(permis){
                    Intent i = new Intent(getApplicationContext(), GPS_servei.class);
                    i.putExtra("matricula", matricula);
                    startService(i);

                }else{
                    //Toast per mostrar missatge indicant que les dades són incorrectes.
                    Toast.makeText(MainActivity.this, "Usuari i/o contrasenya incorrectes.",
                            Toast.LENGTH_LONG).show();
                }


            }else{
                //Toast per mostrar missatge indicant que no es poden deixar camps buits.
                Toast.makeText(MainActivity.this, "No es poden deixar camps buits.",
                        Toast.LENGTH_LONG).show();
            }


        }else if(v.getId() == R.id.buttonSalir){
            //S'atura el servei.
            Intent i = new Intent(getApplicationContext(), GPS_servei.class);
            stopService(i);
        }
    }


    /**
     * Metoque que realitza la comprobació de l'usuari i contrasenya introduits.
     * Si els 2 camps on correctes retornara true si no retornara false.
     * @param matricula
     * @param contrasenya
     * @return
     */
    public boolean comprovacio(String matricula, String contrasenya){
        boolean comprovar = false;

        if (db != null) {
            String[] args = new String[]{matricula, contrasenya};
            Cursor c = db.rawQuery("SELECT * FROM autobuses WHERE ? LIKE matricula AND ? LIKE contrasenya", args);
            if (c.moveToFirst()) {
                comprovar = true;
            } else {
                comprovar = false;
            }
        }

        return comprovar;
    }





    /********** METODES PER EL SERVEI GPS **************/

    /**
     * Metode que comproba la versió ANDROID de l'aplicació, si es la 23 o petita es tindra permis, si no
     * no es tindra permis.
     * @return
     */
    private boolean runtime_permissionss(){

        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return true;
        }
        return false;
    }


    /**
     * Metode que comprova els permisos, si son 100 s'activen els botons, si no es crida al metode que comproba la
     * versió ANDROID.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == getPackageManager().PERMISSION_GRANTED){
                enableButtons();
            }else{
                runtime_permissionss();
            }
        }
    }


    //Clase que no cal utilitzar, només era un prova.
    /*static class Coordenada{
        private String matricula;
        private double latitud;
        private double longitud;
        private String hora;

        public Coordenada(String matricula, double latitud, double longitud, String hora) {
            this.matricula = matricula;
            this.latitud = latitud;
            this.longitud = longitud;
            this.hora = hora;
        }

        public String getMatricula() {
            return matricula;
        }

        public void setMatricula(String matricula) {
            this.matricula = matricula;
        }

        public double getLatitud() {
            return latitud;
        }

        public void setLatitud(double latitud) {
            this.latitud = latitud;
        }

        public double getLongitud() {
            return longitud;
        }

        public void setLongitud(double longitud) {
            this.longitud = longitud;
        }

        public String getHora() {
            return hora;
        }

        public void setHora(String hora) {
            this.hora = hora;
        }
    }*/
}
