package com.example.aadejercicio1;

import java.io.Serializable;

public class Contacto implements Serializable {
    private long id;
    private String nombre;
    private String numero;

    public Contacto setId(long id) {
        this.id = id;
        return this;
    }

    public Contacto setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public Contacto setNumero(String numero){
        this.numero = numero;
        return this;
    }

    @Override
    public String toString() {
        return "ID: [ " + id + " ], NOMBRE: [ " + nombre + " ], NUMERO: [ " + numero + " ] \n";
    }

    public String toCSV(){
        return id + "," + nombre + "," + numero;
    }
}