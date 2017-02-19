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


        btn_start = (Button) findViewById(R.id.buttonentrar);
        btn_stop = (Button) findViewById(R.id.buttonSalir);



        //Abrimos la base de datos 'DBUsuarios' en modo escritura
        BD usdbh =
                new BD(this, "DBAutobuses", null, 1);

        db = usdbh.getWritableDatabase();



        if(runtime_permissionss()){
            enableButtons();
        }




        //Si hemos abierto correctamente la base de datos
        //if(db != null)
        //{
        //Cerramos la base de datos
        //    db.close();
        //}



    }


    private void enableButtons() {
        Button btnSubmit = (Button) findViewById(R.id.buttonentrar);
        Button btnStop = (Button) findViewById(R.id.buttonSalir);
        btnSubmit.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }






    /**
     * Boton de enviar y el menu de seleccion de FECHA.
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
            Intent i = new Intent(getApplicationContext(), GPS_servei.class);
            stopService(i);
        }
    }



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

    private boolean runtime_permissionss(){

        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);

            return true;
        }
        return false;
    }


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
}
