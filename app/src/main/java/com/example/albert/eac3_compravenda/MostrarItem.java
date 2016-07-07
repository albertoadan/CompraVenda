package com.example.albert.eac3_compravenda;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;


public class MostrarItem extends AppCompatActivity  {

    private GoogleMap mMap;
    TextView tv ;
    ImageView im;
    private String foto, titol_preu, desc;
    private Bundle b;
    private Intent i;
    private double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        /**
         *A traves del intent obtenim en un bundle les dades del objecte a mostrar
         */
        b = getIntent().getExtras();
        foto = b.getString("foto");
        lat = b.getDouble("lat");
        lon = b.getDouble("lon");
        titol_preu = b.getString("titol_preu");
        desc = b.getString("desc");

        setSupportActionBar(toolbar);

        /**
         *Canviem el titol de la toolbar i fem que hi aparegui el boto de tornar enrera
         */
        this.setTitle(titol_preu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        im = (ImageView) findViewById(R.id.show_image);

        tv = (TextView) findViewById(R.id.text_mostrar);
        tv.setText(desc);

        /**
         *Fem apareixer el mapa com un fragment del layout de l´activity. Apropem el mapa al lloc on està ubicat l´objecte fent servir les
         * dades lat , long de l´objecte i amb el metode moveCamera
         */
        mMap = ((MapFragment)getFragmentManager().findFragmentById(
                R.id.mapaShowFragment)).getMap();
        LatLng sydney = new LatLng(lat, lon);
        float zoom = (float) 13.0;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoom));


        String aux = foto.replace("file:/", "");
        File arxiu = new File (aux);
        if(arxiu.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(arxiu.getAbsolutePath());
            im.setImageBitmap(myBitmap);

            /**
             * Part Lliure, per mostrar la foto he afegit una animació desenvolupada a l´arxiu fer_venir.xml de la carpeta anim
             * amb el qual la foto fa com si vingues de lluny i rotant.
             * Primer declarem una nova animació i hi carreguem l´arxiu xml amb les dades de l´animació
             * i despres li diem al imageView que comenci l´animació asignada
             */
            Animation an = AnimationUtils.loadAnimation(this, R.anim.fer_venir);
            im.startAnimation(an);
        }

        /**
         *El boto flotant te afegit el listener per que quan es pitji l´eliminem de la base de dades i mostrem un avis per
         * pantalla de que s´ha eliminat l´objecte al haver fet la compra
         * donem com a Ok el resultat de l´activitat i la finalitzem per tornar a la principal.
         */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseDeDades bd = new BaseDeDades(MostrarItem.this);
                bd.obre();
                try {
                    bd.eliminarItem(foto);
                    Toast.makeText(MostrarItem.this, "Item comprat i eliminat de la base de dades", Toast.LENGTH_SHORT).show();
                    bd.tanca();
                    setResult(RESULT_OK);
                    finish();
                }catch (SQLiteException e){
                    String error = e.toString();
                }

                bd.tanca();

            }
        });
    }


    /**
     *Metode per sapiguer quin boto de la action bar s´ha clickat, si s´ha clickat el boto de tornar enrere, home,
     * aleshores fem un result_canceled ja que no ha finalitzat l´activitat correctament
     *
     * @param item objecte de la action bar que s´ha declarat a l´arxiu menu_mostrar_item.xml
     * @return torna el valor boolea de si clickat el boto o no.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
