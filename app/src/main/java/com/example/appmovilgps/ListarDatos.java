package com.example.appmovilgps;

import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;

import com.example.appmovilgps.baseDatos.MiBaseDatos;

public class ListarDatos extends AppCompatActivity {

    private TextView mostrarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_datos);

        mostrarText = (TextView) findViewById(R.id.mostrarDatos);

        listadoGeo();
    }

    // Se crea metodo para listar los datos. Que nos permite mostrar las coordenadas clickeadas.
    private void listadoGeo() {
        //Se instancia mi base de datos
        MiBaseDatos admin = new MiBaseDatos(this, "geoMaps", null, 1);
        SQLiteDatabase BaseD = admin.getReadableDatabase();

        Cursor cursor = BaseD.rawQuery("select * from Geolocalizacion", null);

        if (cursor.moveToFirst()) {
            StringBuilder dataBuilder = new StringBuilder();

            do {
                double lat = cursor.getDouble(0);
                double lon = cursor.getDouble(1);

                mostrarText.setPadding(80, 400, 0, 0);

                dataBuilder.append("\n");
                dataBuilder.append("LATITUD: ").append(lon).append("\n");
                dataBuilder.append("LONGITUD: ").append(lat).append("\n");
                dataBuilder.append("\n");
            } while (cursor.moveToNext());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mostrarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    mostrarText.setText(dataBuilder.toString());
                }
            });
        } else {
            mostrarText.setPadding(60, 350, 60, 0);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mostrarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    mostrarText.setText("No existen datos");
                }
            });
        }

        cursor.close();
        BaseD.close();
    }

    public void Volver(View view) {
        Intent volver = new Intent(this, MainActivity.class);
        startActivity(volver);
    }
}