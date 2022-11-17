package com.example.ejercicio2_4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ejercicio2_4.Conexion.SQLiteConexion;
import com.example.ejercicio2_4.Operaciones.Signaturess;
import com.example.ejercicio2_4.Operaciones.Transacciones;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class Firmas extends AppCompatActivity {

    SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase, null, 1);
    ImageView btnatras;
    ArrayList<Signaturess> listaSignatures= new ArrayList<Signaturess>();
    ImageView imageView;
    ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmas);

        SQLiteDatabase db = conexion.getWritableDatabase();
        String sql = "SELECT * FROM Signature";
        Cursor cursor = db.rawQuery(sql, new String[] {});

        while (cursor.moveToNext()){
            listaSignatures.add(new Signaturess(cursor.getString(0) , cursor.getBlob(1)));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        db.close();
        AdaptadorFirmas adaptador = new AdaptadorFirmas(this);
        lista = findViewById(R.id.lista);
        lista.setAdapter(adaptador);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                obtenerFoto(i);
            }
        });

        btnatras = (ImageView) findViewById(R.id.btnatras);
        btnatras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }

    private void obtenerFoto( int id) {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Signaturess lista_Firmas = null;
        listaSignatures = new ArrayList<Signaturess>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Transacciones.TbSignature,null);

        while (cursor.moveToNext())
        {
            lista_Firmas = new Signaturess();
            lista_Firmas.setDescripcion(cursor.getString(0));
            listaSignatures.add(lista_Firmas);
        }
        cursor.close();
        Signaturess signature = listaSignatures.get(id);
    }


    class AdaptadorFirmas extends ArrayAdapter<Signaturess> {

        AppCompatActivity appCompatActivity;

        AdaptadorFirmas(AppCompatActivity context) {
            super(context, R.layout.signatures_alone, listaSignatures);
            appCompatActivity = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = appCompatActivity.getLayoutInflater();
            View item = inflater.inflate(R.layout.signatures_alone, null);

            imageView = item.findViewById(R.id.imgFirma);

            SQLiteDatabase db = conexion.getWritableDatabase();

            String sql = "SELECT * FROM Signature";

            Cursor cursor = db.rawQuery(sql, new String[] {});
            Bitmap bitmap = null;
            TextView textView1 = item.findViewById(R.id.descripcionImagen);

            if (cursor.moveToNext()){
                textView1.setText(listaSignatures.get(position).getDescripcion());
                byte[] blob = listaSignatures.get(position).getImage();
                ByteArrayInputStream bais = new ByteArrayInputStream(blob);
                bitmap = BitmapFactory.decodeStream(bais);
                imageView.setImageBitmap(bitmap);
            }
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();

            return(item);
        }
    }


}