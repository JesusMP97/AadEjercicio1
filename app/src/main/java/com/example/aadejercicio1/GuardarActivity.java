package com.example.aadejercicio1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GuardarActivity extends AppCompatActivity {

    TextView tvMostrar;
    EditText etNombreArchivo;
    Button btGuardar;
    List<Contacto> contactos;
    Toolbar toolbar2;
    String name;
    SharedPreferences pref;
    SharedPreferences config;
    RadioGroup rgMemoria;
    RadioButton rbInterna;
    RadioButton rbPrivada;
    final static int INTERNA = 0;
    final static int PRIVADA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardar);
        Intent i = getIntent();
        contactos = (List)i.getSerializableExtra("contactos");

        initComponents();
        initEvents();
    }

    private void initComponents() {
        toolbar2 = findViewById(R.id.toolbarGuardar);
        setSupportActionBar(toolbar2);
        tvMostrar = findViewById(R.id.tvMostrar);
        etNombreArchivo = findViewById(R.id.etNombreArchivo);
        btGuardar = findViewById(R.id.btGuardar);
        pref = getSharedPreferences("storedValues", Context.MODE_PRIVATE);
        config = PreferenceManager.getDefaultSharedPreferences(this);
        rgMemoria = findViewById(R.id.rgMemoria);
        rbInterna = findViewById(R.id.rbInterna);
        rbPrivada = findViewById(R.id.rbPrivada);

    }

    private void initEvents() {
        mostrarContactos();
        btGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String selec = checkRB(rgMemoria.getCheckedRadioButtonId());
                if(etNombreArchivo.getText().toString() != "") {
                    if(selec.equalsIgnoreCase("Interna")){
                        almacenarArchivo(INTERNA);
                    }else if(selec.equalsIgnoreCase("Privada")){
                        almacenarArchivo(PRIVADA);
                    }else{
                        msg("Selecciona donde almacenar el archivo");
                    }

                    almacenarHistorial(selec);

                    if(config.getBoolean("recordar", true)) {
                        storeValues();
                    }
                }
            }
        });
        if(config.getBoolean("recordar", true)) {
            loadValues();
        }
    }

    private String checkRB(int numeroRB) {
        RadioButton rb = findViewById(numeroRB);
        return rb.getText().toString();
    }

    private void loadValues() {
        etNombreArchivo.setText(pref.getString("guardarArchivo", ""));
    }

    private void storeValues() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("guardarArchivo", name);
        editor.commit();
    }

    private void msg(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }

    private void mostrarContactos() {
        tvMostrar.setText(contactos.toString());
    }

    private void almacenarArchivo(int almacenamiento) {
        name = etNombreArchivo.getText().toString();
        File f = null;
        if(almacenamiento == INTERNA){
            f = new File(getFilesDir(), name + ".csv");
        }else if(almacenamiento == PRIVADA) {
            f = new File(getExternalFilesDir(null), name + ".csv");
        }
        try{
            FileWriter fw = new FileWriter(f);
            fw.write(contactosToCSV());
            fw.flush();
            fw.close();
            String mensajeConfirmacion = "Guardado con exito en memoria";
            if(almacenamiento == INTERNA){
                mensajeConfirmacion += " interna: \n";
            }else if(almacenamiento == PRIVADA){
                mensajeConfirmacion += " privada: \n";
            }
            mensajeConfirmacion += f.getAbsolutePath();
            msg(mensajeConfirmacion);
        }catch(IOException e){
            tvMostrar.setText(e.getMessage());
            msg(e.getMessage());
        }

    }

    private void almacenarHistorial(String almacenamiento){
        File f = new File(getExternalFilesDir(null),  "history.txt");
        String texto = "";
        if(f.exists()){
            try {
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String linea = "";
                while ((linea = br.readLine()) != null) {
                    texto += linea + "\n";
                }
            }catch (IOException e){
                msg(e.getMessage());
            }
        }
        try{
            FileWriter fw = new FileWriter(f);
            fw.write( texto + name + ".csv /" + almacenamiento);
            fw.flush();
            fw.close();
        }catch(IOException e){
            msg(e.getMessage());
        }
    }

    private String contactosToCSV(){
        String cadena = "id,nombre,numero\n";
        for (int i = 0; i < contactos.size(); i++){
            cadena += contactos.get(i).toCSV() + "\n";
        }
        return cadena;
    }


}
