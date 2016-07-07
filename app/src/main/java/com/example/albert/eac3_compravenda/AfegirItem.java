package com.example.albert.eac3_compravenda;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;


public class AfegirItem extends AppCompatActivity implements LocationListener {

    Uri ident;
    public static final int APP_CAMERA = 0;
    public static final int MAPS_ACTIVITY = 1;
    ImageView im;
    EditText et1, et2, et3;
    BaseDeDades bd;
    Boolean gps = false;
    String titol, preu, desc, imatge;
    Double lat, lon;
    ImageButton ib;
    LocationManager gestorLoc;
    boolean maps_act = false;
    Bitmap bmp;

    /**
     *
     * @param savedInstanceState
     */
    // @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afegir_item);
        et1 = (EditText) findViewById(R.id.editText);
        et2 = (EditText) findViewById(R.id.editText2);
        et3 = (EditText) findViewById(R.id.editText3);



        gestorLoc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        gestorLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);

        Object actionBar = getSupportActionBar();

        android.support.v7.internal.app.WindowDecorActionBar bar = (android.support.v7.internal.app.WindowDecorActionBar) actionBar;

        /**
         * Afegim el titol a l´action bar i tambè el boto de tornar enrera.
         */
        bar.setTitle("Sell item");
        bar.setDisplayHomeAsUpEnabled(true);


        im = (ImageView) findViewById(R.id.imageView);
        ib = (ImageButton) findViewById(R.id.imageButton);


        /**
         * Si hi ha dades grabades per quan es rota la pantalla les treiem per no perdre-les
         */
        if (savedInstanceState != null) {
            ident = savedInstanceState.getParcelable("foto");
            if (ident!=null) {
                tamany_foto(ident);
            }
            maps_act = savedInstanceState.getBoolean("maps");
            if (maps_act== true) {
                ib.setBackgroundColor(Color.GREEN);
            }
            lat = savedInstanceState.getDouble("lat");
            lon = savedInstanceState.getDouble("lon");

        }
        bd = new BaseDeDades(this);




        /**
         * Afegim al imagebutton el listener per sapigur quan es pitja el boto i aixi cridar a l´actvitiy MapsActivity amb la
         // qual mostrem l´ubicació en el mapa per que l´usuari comprovi si es correcte.
         // Si les dades de latitut i longitut encara no les hem obtingut es crida al LocationManager per acabar aconseguint
         // la darrera posició coneguda d´ubicacio. Una vegada obtingut tornem a cridad al metode onClick per que aquesta
         // vegada si que tingui les dades lat, lon correctes i aixi engegar l´activity amb aquestes dades que enviem a traves
         // del bundle en el intent
         */
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lat != null & lon != null) {
                    Intent intent = new Intent(AfegirItem.this, MapsActivity.class);
                    Bundle b = new Bundle();
                    b.putDouble("lat", lat);
                    b.putDouble("lon", lon);
                    intent.putExtras(b);
                    startActivityForResult(intent, MAPS_ACTIVITY);
                } else {
                    Toast.makeText(getApplicationContext(), "Obtenint ubicació, un moment", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                    }
                    gestorLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, AfegirItem.this);
                    Location lm = gestorLoc.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    lat = lm.getLatitude();
                    lon = lm.getLongitude();
                    onClick(v);
                }
            }
        });

    }


    /**
     * Metode per fer la foto del objecte a afegir cridant a l´aplicació de sistema de la cámara amb un intent, despres
     * comprovem si existeix la carpeta Items i si no es crea per guardar-hi totes les fotos.
     * Obtenim un nom únic per a les fotos amb el UUID.randomUUID. Creem l´arxiu amb la foto amb aquest nom únic i
     * l´afegim al intent per que en el resultat de l´activitat sapiga quin nom ha de tenir la foto a guardar.
     * Obtenim la Uri del nom de l´arxiu i començem l´activitat de la càmara
     * * @param view
     */
    public void makePhoto(View view) {

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File carpeta = new File(Environment.getExternalStorageDirectory().toString() + "/Items/");
        if (!carpeta.exists()) {
            carpeta.mkdir();
        }
        String nomUnic = carpeta.getAbsolutePath() + "/" +
                UUID.randomUUID().toString() + "-foto.jpg";
        File foto = new File(nomUnic);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(foto));
        ident = Uri.fromFile(foto);
        startActivityForResult(intent, APP_CAMERA);
    }

    /**
     *Metode que s´executa abans de que la pantalli es roti. Anem ficant en un bundle les dades
     *
     * @param outState Bundle que ens serveix per recuperar dades amb la seva corresponent clau
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (ident!=null) {
            outState.putParcelable("foto", ident);
        }
        outState.putBoolean("maps", maps_act);
        if (lat!=null) {
            outState.putDouble("lat", lat);
            outState.putDouble("lon", lon);
        }
    }

    /**
     *Metode que es fa servir per obtenir les dades guardades abans de rotar el dispositiu. Si hi tenim
     * les dades de la foto , la recuperem per tornar a ficar la foto al imageView i si ja s´ha completat
     * correctament la mapsActivity canviem el fons del boto a verd.
     *
     * @param savedInstanceState Amb aquest objecte i una clau de les dades que volem obtenir ja tindrem els valors
     *                           que necesitem
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("foto")) {
            ident = Uri.parse(String.valueOf(savedInstanceState.getParcelable("foto")));
        }
        if (savedInstanceState.containsKey("maps")) {
            maps_act = savedInstanceState.getBoolean("maps");
            if (maps_act == true ) {
                ib.setBackgroundColor(Color.GREEN);
            }
        }
        if (savedInstanceState.containsKey("lat")) {
            lat = savedInstanceState.getDouble("lat");
            lon = savedInstanceState.getDouble("lon");
        }
    }

    /**
     * Metode per obtenir el resultat de la crida a l´ aplicació càmara i del mapa de google maps
     * Quan cridem a la camara despres cridem al metode tamany_foto que ens modifica el tamany de la foto per evitar
     * problemes de memoria
     *
     * @param requestCode codi per sapiguer quina activitat hem cridat i comprovar que sigui la correcte.
     * @param resultCode  codi que ens diu si l´activitat s´ha finalitzat correctament o no
     * @param data        Totes les dades que hi ha al intent, entre elles, la foto
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APP_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                ContentResolver content = getContentResolver();
                content.notifyChange(ident, null);

                 tamany_foto(ident);

            }
        } else if (requestCode == MAPS_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                ib.setBackgroundColor(Color.GREEN);
                maps_act = true;
                Bundle b = data.getExtras();
                lat = b.getDouble("lat");
                lon = b.getDouble("lon");
            }
        }
    }

    /**
     * Metode que s executa quan canvia l´ubicació del dispositiu, per tant, canviem els valor de les variable lat, lon
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
    }

    /**
     *
     * Metode  que s´executa quan canvia l´estat del GPS
     *
     * @param provider Proveidor, en aquest cas el LocationProvider
     * @param status Codi per sapiguer l´estat del gps
     * @param extras Dades a transferir a traves del bundle segon el canvi d´estat
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String missatge = "";
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                missatge = " GPS status: Out of service";
                gps = false;
                break;
            case LocationProvider.AVAILABLE:
                missatge = "GPS status: Available";
                gps = true;
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                missatge = "GPS status: Temporaly out of service";
                gps = false;
                break;
        }
       // Toast.makeText(this, missatge , Toast.LENGTH_SHORT).show();
    }

    /**
     * Metode que es fa servir quan l ubicació s´activa
     * @param provider
     */
    @Override
    public void onProviderEnabled(String provider) {
        gps = true;
        Toast.makeText(getApplicationContext(), "GPS activat per l’usuari", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        gps = false;
        Toast.makeText(getApplicationContext(), "GPS desactivat per l’usuari", Toast.LENGTH_LONG).show();
    }


    /**
     *Metode que es fa servir al crear el layout per fer apareixer els elements de la action bar
     *
     * @param menu
     * @return Torna un valor boolea true si s´ha pogut mostrar els elements de la action bar sense problemes
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_afegir_item, menu);
        return true;
    }

    /**
     * Metode per comprobar que he clickat a la action bar
     * Despres de comprobar si s´ha clickat el boto de check comprovem que totes les dades s´han omplert i que dades com
     *  el preu siguin correctes i siguin un numero.
     * que l´activitat del mapa ha finalitzat correctament amb totes les dades i les afegim a la base de dades
     * com un nou registre, finalitzem l´activitat amb un result_ok i un finish per que torni a l´activitat principal.
     *
     * @param item parametre amb objecte menuitem amb el qual identificarem mitjançant id l´item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        int c = 0;
        titol = et1.getText().toString();
        desc = et3.getText().toString();
        preu = et2.getText().toString();
        if (id == R.id.action_done) {
                try {
                    ColorDrawable buttonColor = (ColorDrawable) ib.getBackground();
                    c = buttonColor.getColor();
                }catch (Exception e) {
                    c = 0;
                }
            if (!preu.equals("")) {
                try {
                    int p = Integer.parseInt(preu);
                }catch (Exception e) {
                    preu = "";
                    Toast.makeText(this, "Introdueix un número al preu", Toast.LENGTH_SHORT).show();
                }
            }
            if (titol != null & !preu.equals("") & desc != null & imatge != null & c==Color.GREEN) {

                bd.obre();
                try {
                    bd.agefirItem(titol, desc, preu, imatge, lat, lon);
                    setResult(RESULT_OK);
                    finish();
                } catch (SQLiteException  e) {
                }
                bd.tanca();
            } else {
                Toast.makeText(this, "Completa les dades", Toast.LENGTH_LONG).show();
            }
        } else if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *Metode per canviar el tamany de la foto i ajustarla al tamany de la finestra de l imageView
     *
     * @param id identificador universal de la ruta de la foto
     */
    public void tamany_foto(Uri id) {
        Bitmap bitmap;
        ContentResolver content;
        content = getContentResolver();
        content.notifyChange(id, null);

        try {
           // int h = im.getMaxHeight();
            //  bitmap = ((BitmapDrawable)drawing).getBitmap();
            bitmap = MediaStore.Images.Media.getBitmap(content, id);
            int alt = (int) (bitmap.getHeight() * 800 / bitmap.getWidth());
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, 800, alt, true);

            FileOutputStream stream = new FileOutputStream(id.toString().replace("file://", ""));
            resized.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
            im.setImageBitmap(resized);
            imatge = ident.toString();

        } catch (Exception e) {
            Toast.makeText(this, "Error carregant imatge " + id, Toast.LENGTH_SHORT).show();
        }

}


}
