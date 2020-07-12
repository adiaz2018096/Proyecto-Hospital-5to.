package org.angeldiaz.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.angeldiaz.sistema.Principal;

public class MenuPrincipalController implements Initializable {

    private Principal escenarioPrincipal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    public void ventanaMedicos() {
        escenarioPrincipal.ventanaMedicos();
    }

    public void ventanaProgramador() {
        escenarioPrincipal.ventanaProgramador();
    }

    public void ventanaPacientes() {
        escenarioPrincipal.ventanaPacientes();
    }

    public void ventanaEspecialidades() {
        escenarioPrincipal.ventanaEspecialidades();
    }

    public void ventanaTelefonoMedico() {
        escenarioPrincipal.ventanaTelefonoMedico();
    }

    public void ventanaCargo() {
        escenarioPrincipal.ventanaCargo();
    }

    public void ventanaArea() {
        escenarioPrincipal.ventanaArea();
    }

    public void ventanaContactoUrgencia() {
        escenarioPrincipal.ventanaContactoUrgencia();
    }

        public void ventanaHorario(){
            escenarioPrincipal.ventanaHorario();
    }

      
    public void ventanaResponsableTurno(){
        escenarioPrincipal.ventanaResponsableTurno();
    }
    
    public void ventanaMedicoEspecialidad(){
        escenarioPrincipal.ventanaMedicoEspecialidad();
    }
   
    public void ventanaTurno(){
        escenarioPrincipal.ventanaTurno();
    }

    public void ventanaControlCita(){
        escenarioPrincipal.ventanaControlCita();
    }
     
    public void ventanaReceta(){
        escenarioPrincipal.ventanaReceta();
    }

}