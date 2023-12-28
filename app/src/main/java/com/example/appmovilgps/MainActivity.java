package com.example.appmovilgps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appmovilgps.baseDatos.MiBaseDatos;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener{

    //Se crean los atributos de tipo private
    private EditText txtLatitud, txtLongitud;
    private GoogleMap miMapa;
    private Button  btnBorrar;
    private MiBaseDatos bbdd;
    private SQLiteDatabase Database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bbdd = new MiBaseDatos(getApplicationContext(), "geoMaps", null, 1);
        Database = bbdd.getWritableDatabase();

        txtLatitud = (EditText) findViewById(R.id.txtLatitud);
        txtLongitud = (EditText) findViewById(R.id.txtLongitud);
        btnBorrar = (Button) findViewById(R.id.btnBorrar);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        //Se crea un Listener para el boton borrar, que elimina los datos guardados.
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean borrarDatos = eliminarDatos();
                if (borrarDatos) {
                    Toast.makeText(MainActivity.this, "Datos borrados", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "No hay datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        miMapa = googleMap;
        this.miMapa.setOnMapClickListener(this);
        this.miMapa.setOnMapLongClickListener(this);

        LatLng chile = new LatLng(-33.1625731,-70.7874117);
        miMapa.addMarker(new MarkerOptions().position(chile).title("Chile"));
        miMapa.moveCamera(CameraUpdateFactory.newLatLng(chile));
    }

    //AL PINCHAR EN EL MAPA, SE GUARDA AUTOMATICAMENTE LA UBICACION
    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        txtLatitud.setText(""+latLng.latitude);
        txtLongitud.setText(""+latLng.longitude);

        guardarDatos(latLng.latitude, latLng.longitude);

        miMapa.clear();
        LatLng chile = new LatLng(latLng.latitude,latLng.longitude);
        miMapa.addMarker(new MarkerOptions().position(chile).title(""));
        miMapa.moveCamera(CameraUpdateFactory.newLatLng(chile));
    }

    //UTILIZAR EN CELULAR, CLICK SOSTENIDO Y AGRANDAR MAPA
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        txtLatitud.setText(""+latLng.latitude);
        txtLongitud.setText(""+latLng.longitude);

        guardarDatos(latLng.latitude, latLng.longitude);

        miMapa.clear();
        LatLng chile = new LatLng(latLng.latitude,latLng.longitude);
        miMapa.addMarker(new MarkerOptions().position(chile).title(""));
        miMapa.moveCamera(CameraUpdateFactory.newLatLng(chile));
    }

    //METODO GUARDAR DATOS
    private void guardarDatos(double latit, double longit) {
        try {
            ContentValues values = new ContentValues();
            values.put("latit", latit);
            values.put("longit", longit);
            long result = Database.insertOrThrow("Geolocalizacion", null, values);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No se guardaron los datos", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean eliminarDatos() {
        try {
            int eliminado = Database.delete("Geolocalizacion", null, null);
            if (eliminado > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    //Boton mostrar nos lleva al activity LISTAR DATOS
    public void mostrarListarDatos(View view){
        Intent read = new Intent(this, ListarDatos.class);
        startActivity(read);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Database != null && Database.isOpen()) {
            Database.close();
        }
    }
}