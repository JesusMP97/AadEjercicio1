package com.example.aadejercicio1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

public class CargarActivity extends AppCompatActivity {

    Button btCargar, btBorrar;
    EditText etCargar;
    TextView tvContactosCargados, tvHistorial;
    Toolbar toolbarCargar;
    SharedPreferences pref;
    SharedPreferences config;
    RadioButton rbInterno, rbPrivado;
    RadioGroup rgCarga;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar);

        initComponents();
        initEvents();
    }

    private void initComponents() {
        btCargar = findViewById(R.id.btCargar);
        btBorrar = findViewById(R.id.btBorrar);
        etCargar = findViewById(R.id.etCargar);
        rgCarga = findViewById(R.id.rgCarga);
        rbInterno = findViewById(R.id.rbCargaInterna);
        rbPrivado = findViewById(R.id.rbCargaPrivada);
        tvContactosCargados = findViewById(R.id.tvContactosCargados);
        tvHistorial = findViewById(R.id.tvHistorial);
        toolbarCargar = findViewById(R.id.toolbarCargar);
        setSupportActionBar(toolbarCargar);
        pref = getSharedPreferences("storedValues", Context.MODE_PRIVATE);
        config = PreferenceManager.getDefaultSharedPreferences(this);
    }

    private void initEvents() {
        btCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    tvContactosCargados.setText(cargarArchivo());
            }
        });

        btBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrarHistorial();
            }
        });

        tvHistorial.setText(cargarHistorial());

        if(config.getBoolean("recordar", true)) {
            loadValues();
        }
    }

    private void loadValues() {
        etCargar.setText(pref.getString("cargarArchivo", ""));
    }

    private void storeValues() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("cargarArchivo", name);
        editor.commit();
    }

    private void borrarHistorial() {
        File f = new File(getExternalFilesDir(null),  "history.txt");
        try{
            FileWriter fw = new FileWriter(f);
            fw.write("");
            fw.flush();
            fw.close();
            tvHistorial.setText(cargarHistorial());
            msg("Historial borrado");
        }catch(IOException e){
            msg(e.getMessage());
        }
    }

    private String checkRB(int numeroRB) {
        RadioButton rb = findViewById(numeroRB);
        return rb.getText().toString();
    }


    private String cargarHistorial() {
        String name = "history";
        File f = new File(getExternalFilesDir(null), name + ".txt");
        String texto = "";
        if(f.exists()) {
            try {
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String linea = "";
                while((linea = br.readLine()) != null){
                    texto += linea + "\n";
                }
            } catch (IOException e) {
                msg(e.getMessage());
            }
        }else{
            msg("No hay historial");
        }
        return texto;

    }

    private String cargarArchivo() {
        name = etCargar.getText().toString();
        File f = null;
        final String selec = checkRB(rgCarga.getCheckedRadioButtonId());
        if(selec.equalsIgnoreCase("Privada")){
            f = new File(getExternalFilesDir(null), name + ".csv");
        }else if(selec.equalsIgnoreCase("Interna")){
            f = new File(getFilesDir(), name + ".csv");
        }else{
            msg("El archivo " + name + ".csv no existe en la memoria " + selec);
        }
        String texto = "";
        if(f.exists()) {
            try {
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String linea = "";
                while((linea = br.readLine()) != null){
                    texto += linea + "\n";
                }
                storeValues();
                msg("Cargados los contactos de: " + f.getAbsolutePath());
            } catch (IOException e) {
                msg(e.getMessage());
            }
        }else{
            msg("El archivo " + name + ".csv no existe");
        }
        return texto;
    }

    private void msg(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }



}
