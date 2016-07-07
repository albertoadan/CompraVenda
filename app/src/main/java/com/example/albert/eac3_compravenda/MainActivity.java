package com.example.albert.eac3_compravenda;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Clase principal que al mateix temps es l´activitat principal del projecte.
 *
 * Com a part lliure s´ha afegit que quan es motra la foto de l´objecte a l´activitat MostrarItem es carrega una animacio creada
 * a l´arxiu xml fer_venir.xml la qual lo que fa es fer venir de lluny la foto i rotant.
 * Es declara una nova animació Animation, se li asigna l´arxiu xml amb l´animacio i despres
 * se li diu al ImageView que comenci l´animació.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ListView lv ;
    Toolbar tb;
    public BaseDeDades bd;
    ImageView im;
    TextView tv;
    FloatingActionButton fab;

    public static final int APP_MOSTRAR_ITEM = 2;
    public static final int APP_AFEGIR_ITEM = 3;


    private Uri identificadorImatge;

    List<HashMap<String, String>> llistaPunts = new ArrayList<HashMap<String, String>>();

    SimpleAdapter adaptador = null;

    HashMap<String, String> element = null;

    double lat, lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bd = new BaseDeDades(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        tb = (Toolbar) findViewById(R.id.toolbar);
        tb.setTitle("EAC3-CompraVenda");

        lv = (ListView) findViewById(R.id.llista_obj);

        String [] origen = {"foto", "titol_preu"} ;
        int[]  desti  = {R.id.imageView2, R.id.textView2};

        adaptador = new SimpleAdapter(getBaseContext(), llistaPunts, R.layout.llista, origen, desti);
        lv.setAdapter(adaptador);

        /**
         * Al listview hi capturem quan s´ha clickat de forma prolongada un dels elements, que sera quan obtenim d´aquest element les altres
         * dades seves que tenim a la base de dades i les enviem amb un bundle quan cridem a l´activitat MostrarItem esperant un resultat
         * de l´activitat.
         */
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, Object> obj = (HashMap<String, Object>) adaptador.getItem(position);
                String foto = (String) obj.get("foto");
                double lat, lon ;
                String titol, preu, desc;
                String pos = null;

                bd = new BaseDeDades(MainActivity.this);
                bd.obre();
                Cursor c = bd.totItems();
                if (c.moveToFirst()) {
                    do {
                        pos = c.getString(3);
                        if (pos.equals(foto)) {
                            desc = c.getString(6);
                            titol = c.getString(1);
                            preu = c.getString(2);
                            lat = c.getDouble(4);
                            lon = c.getDouble(5);
                            Intent i = new Intent(MainActivity.this, MostrarItem.class);
                            Bundle b = new Bundle();
                            b.putString("titol_preu", titol + " - "+ preu + " €");
                            b.putString("desc", desc );
                            b.putDouble("lat", lat);
                            b.putDouble("lon", lon);
                            b.putString("foto", foto);
                            i.putExtras(b);
                            startActivityForResult(i, APP_MOSTRAR_ITEM);;
                        }
                    }while (c.moveToNext());
                }

                bd.tanca();

                return false;
            }
        });

        /**
         * Capturem quan s´ha clickat de manera curta algun element del listview. Busquem l´element a la base de dades
         * i quan tenim la descripcio la mostrem per pantalla amb un snackbar
         */
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bd = new BaseDeDades(MainActivity.this);
                bd.obre();
                HashMap<String, Object> obj = (HashMap<String, Object>) adaptador.getItem(position);
                String foto = (String) obj.get("foto");
                String pos=null;
                String desc = null;

                Cursor c = bd.totItems();
                if (c.moveToFirst()) {
                    do {
                        pos = c.getString(3);
                        if (pos.equals(foto)) {
                            desc = c.getString(6);
                        }
                    }while (c.moveToNext());
                }

                bd.tanca();
                Snackbar.make(view, desc , Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });
        adaptador.notifyDataSetChanged();
        obtenirObjectes();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     *Metode per obtenir els objectes de la base de dades i afegir-los a la llista del adaptador. Cridem al metode
     * notifyDataSetChanged per actualitzar
     *
     * Primer fem un clear de la llista per tornar a omplirla desde 0 i aixi que no apareguin objectes eliminats de la
     * base de dades. En un HashMap anem afegint els elements segons la clau corresponent
     */
    public void obtenirObjectes ()  {
        llistaPunts.clear();
        bd.obre();

        Cursor c = bd.totItems();

        if (c.moveToFirst()) {
            do {
                element = new HashMap<>();
                String titol_preu = c.getString(1) + " - " + c.getString(2) + " €";
                element.put("foto", c.getString(3));
                element.put("titol_preu", titol_preu);
                llistaPunts.add(element);
            } while (c.moveToNext());
        }
        bd.tanca();
        adaptador.notifyDataSetChanged();
    }

    /**
     *Metode per capturar quan es pitja el boto flotant de la suma per cridar a l´activity afegirItem i esperar resultat, per
     * aixo la cridem amb un startactivityForResult
     *
     * @param v Vista amb la qual podem sapiguer si s´ha pitjat el boto de suma o no.
     */
    @Override
    public void onClick(View v) {
        if (v==fab)  {
            Intent i = new Intent (MainActivity.this, AfegirItem.class);
            startActivityForResult(i, APP_AFEGIR_ITEM);
        }

    }

    /**
     *Metode que s´executa quan finalitza una activitat. Finalment s´executa el metode obtenirObjectes que
     * actuzalitza la llista y el adaptador     *
     *
     * @param requestCode Codi que ens serveix per sapiguer quina activitat ha sigut la que ha finalitzat.
     * @param resultCode Codi que ens diu si ha finalitzat correcament o no.
     * @param data Intent que en aquest cas no es fa servir
     */
    @Override
    public  void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APP_MOSTRAR_ITEM) {
            if (resultCode == Activity.RESULT_OK) {
                obtenirObjectes();
            }
        }
        else if (requestCode == APP_AFEGIR_ITEM) {
            if (resultCode == Activity.RESULT_OK) {
                obtenirObjectes();
            }
        }
            }
}
