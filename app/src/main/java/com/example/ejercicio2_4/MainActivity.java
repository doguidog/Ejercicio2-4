package com.example.ejercicio2_4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ejercicio2_4.Conexion.SQLiteConexion;
import com.example.ejercicio2_4.Operaciones.Transacciones;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    EditText txtdescripcion;
    ImageView btnguardar,btngaleria;
    Bitmap imagen;
    private ConfiguracionesBitmap config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        casteo();

        LinearLayout mContent = (LinearLayout) findViewById(R.id.signature);
        config = new ConfiguracionesBitmap(this, null);
        mContent.addView(config, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

        btnguardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();}
        });


        btngaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Firmas.class);
                startActivity(intent);
            }
        });
    }

    private void LimpiarPantalla()
    {
        txtdescripcion.setText("");
        config.ClearCanvas();

    }

    public void casteo() {
        txtdescripcion = findViewById(R.id.txtdescripcion);
        btnguardar = findViewById(R.id.btnguardar);
        btngaleria = findViewById(R.id.btngaleria);
    }

    public void guardar(){
        try {
            firmas(config.getBitmap());
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            MediaStore.Images.Media.insertImage(getContentResolver(), imagen, imageFileName , "yourDescription");

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            LimpiarPantalla();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error a guardar Datos ",Toast.LENGTH_LONG).show();
        }

    }

    private void firmas( Bitmap bitmap) {

        SQLiteConexion conexion = new SQLiteConexion(this, Transacciones.NameDatabase,null,1);
        SQLiteDatabase db = conexion.getWritableDatabase();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] ArrayFoto  = stream.toByteArray();

        ContentValues valores = new ContentValues();

        valores.put(Transacciones.Descripcion,txtdescripcion.getText().toString());
        valores.put(String.valueOf(Transacciones.Firma),ArrayFoto);

        Long resultado = db.insert(Transacciones.TbSignature, null, valores);

        Toast.makeText(getApplicationContext(), "Registro ingreso con exito: " + resultado.toString()
                ,Toast.LENGTH_LONG).show();

        db.close();
    }


}