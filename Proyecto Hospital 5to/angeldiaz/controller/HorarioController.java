
package org.angeldiaz.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.angeldiaz.bean.Horario;

import org.angeldiaz.db.Conexion;
import org.angeldiaz.sistema.Principal;

public class HorarioController implements Initializable{
      private enum operaciones {
        NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
        private Principal escenarioPrincipal;
        private operaciones tipoDeOperacion = operaciones.NINGUNO;
        private ObservableList<Horario> listaHorario;
        @FXML private TextField txtHorarioInicio;
        @FXML private TextField txtHorarioSalida;
        @FXML private CheckBox cbLunes;
        @FXML private CheckBox cbMartes;
        @FXML private CheckBox cbMiercoles;
        @FXML private CheckBox cbJueves;
        @FXML private CheckBox cbViernes;
        @FXML private TableColumn colCodigo;
        @FXML private TableView tblHorarios;
        @FXML private TableColumn colHoraInicio;
        @FXML private TableColumn colHoraSalida;
        @FXML private TableColumn colLunes;
        @FXML private TableColumn colMartes;
        @FXML private TableColumn colMiercoles;
        @FXML private TableColumn colJueves;
        @FXML private TableColumn colViernes;
        @FXML private Button btnNuevo;
        @FXML private Button btnEditar;
        @FXML private Button btnEliminar;
        @FXML private Button btnReporte;
        
    
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
      cargarDatos();
        }

    
   public void cargarDatos() {
        tblHorarios.setItems(getHorarios());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Horario, Integer>("codigoHorario"));
        colHoraInicio.setCellValueFactory(new PropertyValueFactory<Horario, String>("horarioInicio"));
        colHoraSalida.setCellValueFactory(new PropertyValueFactory<Horario, String>("horarioSalida"));
        colLunes.setCellValueFactory(new PropertyValueFactory<Horario, String>("lunes"));
        colMartes.setCellValueFactory(new PropertyValueFactory<Horario, String>("martes"));
        colMiercoles.setCellValueFactory(new PropertyValueFactory<Horario, String>("miercoles"));
        colJueves.setCellValueFactory(new PropertyValueFactory<Horario, String>("jueves"));
        colViernes.setCellValueFactory(new PropertyValueFactory<Horario, String>("viernes"));
   }

   public ObservableList<Horario> getHorarios() {
        ArrayList<Horario> lista = new ArrayList<Horario>();

        try {
            PreparedStatement procedimiento = (PreparedStatement) Conexion.getInstancia().getConexion().prepareCall(("{call sp_ListarHorarios}"));
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new Horario(resultado.getInt("codigoHorario"),
                        resultado.getString("horarioInicio"),
                        resultado.getString("horarioSalida"),
                        resultado.getBoolean("lunes"),
                        resultado.getBoolean("martes"),
                        resultado.getBoolean("miercoles"),
                        resultado.getBoolean("jueves"),
                        resultado.getBoolean("viernes")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaHorario = FXCollections.observableArrayList(lista);
    }

   
       public Horario buscarHorario(int codigoHorario){
	Horario resultado = null;
	try{
		PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarHorario(?)}");
		procedimiento.setInt(1, codigoHorario);
		ResultSet registro = procedimiento.executeQuery();
		while(registro.next()){
			resultado = new Horario (registro.getInt("codigoHorario"),
					registro.getString("horarioInicio"),
                                        registro.getString("horarioSalida"),
                                        registro.getBoolean("lunes"),
                                        registro.getBoolean("martes"),
                                        registro.getBoolean("miercoles"),
                                        registro.getBoolean("jueves"),
                                        registro.getBoolean("viernes"));
		}
		}catch(Exception e){
		e.printStackTrace();
	}
		return resultado;
}


public void seleccionarElemento() {
      if (tblHorarios.getSelectionModel().getSelectedItem() != null){
      txtHorarioInicio.setText(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getHorarioInicio());
      txtHorarioSalida.setText(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getHorarioSalida());
      cbLunes.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isLunes ());
      cbMartes.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isMartes ());
      cbMiercoles.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isMiercoles ());
      cbJueves.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isJueves());        
      cbViernes.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isViernes ());
      cbLunes.setDisable(false);
      cbMartes.setDisable(false);
      cbMiercoles.setDisable(false);
      cbJueves.setDisable(false);
      cbViernes.setDisable(false);
     
      }
    }

 public void nuevo() {
        switch (tipoDeOperacion) {
            case NINGUNO:
                limpiarControles();
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                break;

                
            case GUARDAR:
                if(txtHorarioInicio.getText().equals("") || txtHorarioSalida.getText().equals("") ){
                    JOptionPane.showMessageDialog(null, "Debe de llenar todos los campos");
                }else{ 
                guardar();
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                tblHorarios.setDisable(false);
                break;
        }
    }

 }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                limpiarControles();
                tipoDeOperacion = operaciones.NINGUNO;
                tblHorarios.setDisable(false);
                break;
                
            default:
                if(tblHorarios.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de eliminar este Registro?", "Eliminar Horario", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarHorario(?)}");
                            procedimiento.setInt(1, ((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getCodigoHorario());
                            procedimiento.execute();
                            listaHorario.remove(tblHorarios.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                        e.printStackTrace();
                        }
                    
                } else{
                limpiarControles();
                desactivarControles();
                tblHorarios.getSelectionModel().select (null);
                
            }
            
      } else{
                JOptionPane.showMessageDialog(null, "Debe de seleccionar un elemento primero.");
                }
        
        
    }
 }

public void guardar() {
        Horario registro = new Horario();
        registro.setHorarioInicio(txtHorarioInicio.getText());
        registro.setHorarioSalida(txtHorarioSalida.getText());
        registro.setLunes(cbLunes.isSelected());
        registro.setMartes(cbMartes.isSelected());
        registro.setMiercoles(cbMiercoles.isSelected());
        registro.setJueves(cbJueves.isSelected());
        registro.setViernes(cbViernes.isSelected());

        try {
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarHorario(?,?,?,?,?,?,?)}");
            procedimiento.setString(1, registro.getHorarioInicio());
            procedimiento.setString(2, registro.getHorarioSalida());
            procedimiento.setBoolean(3, registro.isLunes());
            procedimiento.setBoolean(4, registro.isMartes());
            procedimiento.setBoolean(5, registro.isMiercoles());
            procedimiento.setBoolean(6, registro.isJueves());
            procedimiento.setBoolean(7, registro.isViernes());
            procedimiento.execute();
            listaHorario.add(registro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblHorarios.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    
                    
                } else{
                    JOptionPane.showMessageDialog(null, "Seleccione un Elemento Porfavor");
                }break;
                
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoDeOperacion = operaciones.NINGUNO;
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                cargarDatos();
                limpiarControles();
                break;
        }
    }  

     public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarHorario(?,?,?,?,?,?,?,?)}");
            Horario registro = (Horario)tblHorarios.getSelectionModel().getSelectedItem();
            registro.setHorarioInicio(txtHorarioInicio.getText());
            registro.setHorarioSalida(txtHorarioSalida.getText());
            registro.setLunes(cbLunes.isSelected());
            registro.setMartes(cbMartes.isSelected());
            registro.setMiercoles(cbMiercoles.isSelected());
            registro.setJueves(cbJueves.isSelected());
            registro.setJueves(cbViernes.isSelected());
            procedimiento.setInt(1, registro.getCodigoHorario());
            procedimiento.setString(2, registro.getHorarioInicio());
            procedimiento.setString(3, registro.getHorarioSalida());
            procedimiento.setBoolean(4, registro.isLunes());
            procedimiento.setBoolean(5, registro.isMartes());
            procedimiento.setBoolean(6, registro.isMiercoles());
            procedimiento.setBoolean(7, registro.isJueves());
            procedimiento.setBoolean(8, registro.isViernes());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
        
    } 
 
    public void desactivarControles() {
        txtHorarioInicio.setEditable(false);
        txtHorarioSalida.setEditable(false);
        cbLunes.setDisable(true);
        cbMartes.setDisable(true);
        cbMiercoles.setDisable(true);
        cbJueves.setDisable(true);
        cbViernes.setDisable(true);
    }

    public void activarControles(){
        txtHorarioInicio.setEditable(true);
        txtHorarioSalida.setEditable(true);
        cbLunes.setDisable(false);
        cbMartes.setDisable(false);
        cbMiercoles.setDisable(false);
        cbJueves.setDisable(false);
        cbViernes.setDisable(false);
    }
    
    public void limpiarControles(){
        txtHorarioInicio.setText("");
        txtHorarioSalida.setText("");
        cbLunes.selectedProperty().set (false);
        cbMartes.selectedProperty().set(false);
        cbMiercoles.selectedProperty().set(false);
        cbJueves.selectedProperty().set(false);
        cbViernes.selectedProperty().set(false);
        
        
    }
 
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    public void menuPrincipal() {
        this.escenarioPrincipal.menuPrincipal();
    }

    
    }
    














