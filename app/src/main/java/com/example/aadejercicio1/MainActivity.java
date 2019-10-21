package com.example.aadejercicio1;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btCargarTelefono;
    Button btCargarMemoria;
    Button btGuardar;
    TextView tvContactos;
    List<Contacto> contactos;
    private static final int ID_PERMISO_LEER_CONTACTOS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initComponents();
        initEvents();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void initComponents() {
        btCargarTelefono = findViewById(R.id.btCargarTelefono);
        btCargarMemoria = findViewById(R.id.btCargarMemoria);
        btGuardar = findViewById(R.id.btGuardar);
        tvContactos = findViewById(R.id.tvContactos);
    }

    private void initEvents() {
        btCargarTelefono.setOnClickListener(new View.OnClickListener() {    // OnClickListener del boton Descargar Contactos
            @Override
            public void onClick(View v) {
                obtenerListaContactos();
                msg("Contactos importados");
            }
        });

        btGuardar.setOnClickListener(new View.OnClickListener() {   // OnClickListener del boton Guardar Contactos
            @Override
            public void onClick(View v) {
                if(contactos == null){
                    msg("Debe importar sus contactos primero");
                }else{
                    lanzarGuardarActivity();
                }
            }
        });

        btCargarMemoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lanzarCargarActivity();
            }
        });
    }

    private void lanzarCargarActivity() {
        Intent intent = new Intent(this, CargarActivity.class);
        startActivity(intent);
    }

    private void lanzarGuardarActivity() {
        Intent intent = new Intent(this, GuardarActivity.class);
        intent.putExtra("contactos", (Serializable) contactos);
        startActivity(intent);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch(item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }

        return true;
    }

    private void msg(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    private void obtenerListaContactos() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mostrarContactos();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {

                    Toast.makeText(this,R.string.Razon,Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, ID_PERMISO_LEER_CONTACTOS);

                }else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, ID_PERMISO_LEER_CONTACTOS);
                }
            } else {
                mostrarContactos();
            }

        }

    }

    private void mostrarContactos(){
        contactos = getListaContactos();
        tvContactos.setText(contactos.toString());
    }

    public List<Contacto> getListaContactos(){
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        String proyeccion[] = null;

        String seleccion = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = ? and " + ContactsContract.Contacts.HAS_PHONE_NUMBER + "= ?";

        String argumentos[] = new String[]{"1","1"};

        String orden = ContactsContract.Contacts.DISPLAY_NAME + " collate localized asc";

        Cursor cursor = getContentResolver().query(uri, proyeccion, seleccion, argumentos, orden);

        int indiceId = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        int indiceNombre = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);

        List<Contacto> lista = new ArrayList<>();
        Contacto contacto;
        ContentResolver cr = getContentResolver();
        String phoneNo = "";

        while(cursor.moveToNext()){
            contacto = new Contacto();
            contacto.setId(cursor.getLong(indiceId));
            contacto.setNombre(cursor.getString(indiceNombre));

            if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id}, null);
                while (pCur.moveToNext()) {
                    phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                pCur.close();
            }

            contacto.setNumero(phoneNo);
            lista.add(contacto);
        }

        return lista;
    }




}
