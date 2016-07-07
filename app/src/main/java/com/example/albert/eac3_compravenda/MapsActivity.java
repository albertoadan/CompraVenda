package com.example.albert.eac3_compravenda;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double lat=0;
    private double lon=0;
    private Intent i;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        /**
         *Fragment de mapa mapFragment que afegim al layout. Primer localitzem la id del mapa i despres li afegim el mapa amb getMapAsync
         */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        b  = getIntent().getExtras();
        lat = b.getDouble("lat");
        lon = b.getDouble("lon");
    }


    /**
     *Metode que s´executa quan el mapa ja està correctament carregat.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Marker mk;


        LatLng sydney = new LatLng(lat, lon);
        float zoom = (float) 13.0;
        mk = mMap.addMarker(new MarkerOptions().position(sydney).title("Fes click al marcador per aceptar"));

        /**
         * Apropem el mapa a la zona on es el marcador amb el metode newLatLngZoom i afegim un listener per sapiguer quan s´ha clickat
         * el marcador i aixi finalitzar l´activitat. Despres fem que del marcador es mostri l´informació amb el metode showInfoWindow
         *
         */
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoom));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                          @Override
                                          public boolean onMarkerClick(Marker marker) {
                                              b.putDouble("lat", lat);
                                              b.putDouble("lon", lon);
                                              i = getIntent().putExtras(b);
                                              setResult(RESULT_OK, i);
                                              finish();
                                              return true;
                                          }
                                      }
        );
        mk.showInfoWindow();
    }
}
