package com.example.albert.eac3_compravenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Albert on 04/11/2015.
 */
public class BaseDeDades {
    public static final String CLAU_ID = "_id";
    public static final String CLAU_TITOL = "titol";
    public static final String CLAU_DESC = "desc";
    public static final String CLAU_PREU ="preu";
    public static final String CLAU_IMATGE = "imatge" ;
    public static final String CLAU_LATITUT = "latitut" ;
    public static final String CLAU_LONGITUT = "longitut";



    public static final String TAG = "DBBaseDeDaDes";

    public static final String BD_NOM = "BDCompraVenda";
    public static final String BD_TAULA = "items";
    public static final int VERSIO = 1;


    /**
     * String per crear la base de dades amb tots els seus camps i el tipus de dades que hi tindrem
     */
    public static final String BD_CREATE =
            "create table " + BD_TAULA + "( " + CLAU_ID + " integer primary key autoincrement, " +
                    CLAU_TITOL + " text not null, " + CLAU_DESC + " text not null, " + CLAU_PREU + " text not null, "
                    + CLAU_IMATGE + " text not null, " +  CLAU_LATITUT + " double not null, "  + CLAU_LONGITUT + " double not null " +");";

    private final Context context;
    private AjudaBD ajuda;
    private SQLiteDatabase bd;

    public BaseDeDades(Context con) {
        this.context = con;
        ajuda = new AjudaBD(context);
    }

    //Obre la BD o si es la primera vegada que s´intenta obrir la crea
    public BaseDeDades obre() throws SQLException {
        bd = ajuda.getWritableDatabase();
        return this;
    }

    /**
     *Metode per tancar la base de dades
     */
    public void tanca() {
        ajuda.close();
    }

    /**
     *metode per afegir un registre a la bae de dades, Es fiquen els valor en un contentValues i despres fem un afegir el content a la base
     * de dades
     *
     * @param titol
     * @param desc
     * @param preu
     * @param imatge
     * @param latitut
     * @param longitut
     * @return Torna un numero per sapiguer si el metode ha funcionat correctament.
     */
    public long agefirItem(String titol, String desc, String preu, String imatge, double latitut, double longitut) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(CLAU_TITOL, titol);
        initialValues.put(CLAU_DESC, desc);
        initialValues.put(CLAU_PREU, preu);
        initialValues.put(CLAU_IMATGE, imatge);
        initialValues.put(CLAU_LATITUT, latitut);
        initialValues.put(CLAU_LONGITUT, longitut);
        return  bd.insert(BD_TAULA, null, initialValues);
    }


    /**
     *
     * @param foto
     * @return
     * @throws SQLException
     */
    public Cursor obtenirItem(String foto) throws SQLException {
        Cursor mCursor = bd.query(BD_TAULA, new String[] {CLAU_ID, CLAU_TITOL, CLAU_IMATGE, CLAU_PREU, CLAU_DESC}, CLAU_IMATGE + "='"+ foto+ "'", null , null, null, null );

        if (mCursor != null)
            mCursor.moveToFirst();
            String c = mCursor.getString(6);
        return mCursor;
    }

    public Cursor Items(String foto) {
        return bd.query(BD_TAULA, new String[]{CLAU_ID, CLAU_TITOL, CLAU_PREU, CLAU_IMATGE, CLAU_LATITUT, CLAU_LONGITUT, CLAU_DESC   }, CLAU_IMATGE + "='"+ foto+ "'", null, null, null, null);
    }

    /**
     *
     * @return
     */
    public Cursor totItems() {
        return bd.query(BD_TAULA, new String[]{CLAU_ID, CLAU_TITOL, CLAU_PREU, CLAU_IMATGE, CLAU_LATITUT, CLAU_LONGITUT, CLAU_DESC   }, null, null, null, null, null);
    }




//    public boolean actualitzarItem(long IDFila, String titol, String desc, String preu, String imatge, double latitut, double longitut) {
//        ContentValues args = new ContentValues();
//        args.put(CLAU_TITOL, titol);
//        args.put(CLAU_DESC, desc);
//        args.put(CLAU_PREU, preu);
//        args.put(CLAU_IMATGE, imatge);
//        args.put(CLAU_LATITUT, latitut);
//        args.put(CLAU_LONGITUT, longitut);
//        return bd.update(BD_TAULA, args, CLAU_ID + " = " + IDFila, null) > 0;
//    }

    /**
     *
     * @param foto
     * @return
     */
    public long eliminarItem(String foto) {
        return bd.delete(BD_TAULA, CLAU_IMATGE +" = '"+ foto+"'" ,null);
    }


    private static class AjudaBD extends SQLiteOpenHelper {
        AjudaBD(Context con) {
            super(con, BD_NOM, null, VERSIO);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(BD_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int VersioAntiga, int VersioNova) {
            Log.w(TAG, "Actualitzant Base de dades de la versió" + VersioAntiga + " a " + VersioNova + ". Destruirà totes les dades");
            db.execSQL("DROP TABLE IF EXISTS " + BD_TAULA);

            onCreate(db);
        }
    }

}
