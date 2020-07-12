
package org.angeldiaz.bean;

public class ResponsableTurno {
    private int codigoResponsableTurno;
    private String nombres;
    private String apellidos;
    private String numeroContacto;
    private int codigoArea;
    private int codigoCargo;
    
public ResponsableTurno(){

}



 public ResponsableTurno (int codigoResponsableTurno, String nombres, String apellidos, String numeroContacto, int codigoArea, int codigoCargo) {
        this.codigoResponsableTurno = codigoResponsableTurno;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.numeroContacto = numeroContacto;
        this.codigoArea = codigoArea;
        this.codigoCargo = codigoCargo;
    }

    public int getCodigoResponsableTurno() {
        return codigoResponsableTurno;
    }

    public void setCodigoResponsableTurno(int codigoResponsableTurno) {
        this.codigoResponsableTurno = codigoResponsableTurno;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNumeroContacto() {
        return numeroContacto;
    }

    public void setNumeroContacto(String numeroContacto) {
        this.numeroContacto = numeroContacto;
    }

    public int getCodigoArea() {
        return codigoArea;
    }

    public void setCodigoArea(int codigoArea) {
        this.codigoArea = codigoArea;
    }

    public int getCodigoCargo() {
        return codigoCargo;
    }

    public void setCodigoCargo(int codigoCargo) {
        this.codigoCargo = codigoCargo;
    }
 

  public String toString(){
        return getCodigoResponsableTurno() + " | "+ getNombres()+ " , "+ getApellidos();
    }
    

}