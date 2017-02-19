package com.example.dblan.autobuses;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BD extends SQLiteOpenHelper {

    //String[] sentenciasSQL = new String [6];
    String crearTabla1 = "CREATE TABLE autobuses (matricula TEXT PRIMARY KEY, contrasenya TEXT)";
    String crearTabla2 = "CREATE TABLE rutas (matricula TEXT, latitud NUMBER, longitud NUMBER, data TEXT)";
    String crearTabla3 = "CREATE TABLE jornada (matricula TEXT, hora_inici TEXT, hora_fi TEXT)";



    public BD(Context contexto, String nombre, CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(crearTabla1);
        db.execSQL(crearTabla2);
        db.execSQL(crearTabla3);

        db.execSQL("INSERT INTO autobuses VALUES ('1111A', '1234')");
        db.execSQL("INSERT INTO autobuses VALUES ('2222B', '4321')");
        db.execSQL("INSERT INTO autobuses VALUES ('3333C', '1423')");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAnterior,
                          int versionNueva) {

    }


}