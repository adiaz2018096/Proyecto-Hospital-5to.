
package org.angeldiaz.controller;


//import eu.schudt.javafx.controls.calendar.DatePicker;
//import java.net.URL;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.text.DateFormat;
//import java.util.ArrayList;
//import java.util.Locale;
//import java.util.ResourceBundle;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.Button;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.TextField;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.layout.GridPane;
//import javax.swing.JOptionPane;
//import org.angeldiaz.bean.ControlCita;
//import org.angeldiaz.bean.Medico;
//import org.angeldiaz.bean.Paciente;
//import org.angeldiaz.db.Conexion;
//import org.angeldiaz.sistema.Principal;
//import javafx.scene.control.Control;
//import static org.angeldiaz.db.Conexion.getInstancia;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.angeldiaz.bean.ControlCita;
import org.angeldiaz.bean.Medico;
import org.angeldiaz.bean.Paciente;
import org.angeldiaz.db.Conexion;
import static org.angeldiaz.db.Conexion.getInstancia;
import org.angeldiaz.sistema.Principal;

public class ControlCitaController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<ControlCita> listaControlCita;
    private ObservableList<Medico> listaMedico;
    private ObservableList<Paciente> listaPaciente;
    private DatePicker fecha;   
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraFin;
    @FXML private GridPane grpFecha;
    @FXML private ComboBox cmbCodigoMedico;
    @FXML private ComboBox cmbCodigoPaciente;
    @FXML private TableView tblControlCitas;
    @FXML private TableColumn colCodigoControl;
    @FXML private TableColumn colInicio;
    @FXML private TableColumn colFin;
    @FXML private TableColumn colCodigoMedico;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private TableColumn colFecha;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       cargarDatos();
       fecha = new DatePicker (Locale.ENGLISH);
       fecha.setDateFormat(new SimpleDateFormat ("yyyy-MM-dd"));
       fecha.getCalendarView().todayButtonTextProperty().set("Today");
       fecha.getCalendarView().setShowWeeks(false);
       fecha.getStylesheets().add("/org/angeldiaz/resource/DatePicker.css");
       grpFecha.add(fecha, 0, 0);
       cmbCodigoMedico.setItems(getMedicos());
       cmbCodigoPaciente.setItems(getPaciente());
    }
    
    public void cargarDatos(){
        tblControlCitas.setItems(getControlCita());
        colCodigoControl.setCellValueFactory(new PropertyValueFactory<ControlCita, Integer>("codigoControlCita")); 
        colInicio.setCellValueFactory( new PropertyValueFactory<ControlCita, String>("horaInicio"));
        colFin.setCellValueFactory(new PropertyValueFactory<ControlCita, String>(" horaFin"));
        colFecha.setCellValueFactory(new PropertyValueFactory<ControlCita, String>("fecha"));
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<ControlCita, Integer>("codigoMedico"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<ControlCita, Integer>("codigoPaciente"));
    }


     public void seleccionarElemento() {
        
        txtHoraInicio.setText(((ControlCita) tblControlCitas.getSelectionModel().getSelectedItem()).getHoraInicio());
        txtHoraFin.setText(((ControlCita) tblControlCitas.getSelectionModel().getSelectedItem()).getHoraFin());
        fecha.selectedDateProperty().set(((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getFecha());
        cmbCodigoMedico.getSelectionModel().select(buscarMedico(((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getCodigoMedico()));
        cmbCodigoPaciente.getSelectionModel().select(buscarPaciente(((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
    }


    public ObservableList<ControlCita> getControlCita(){
        ArrayList<ControlCita> lista = new ArrayList<ControlCita>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarControlCitas}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new ControlCita(resultado.getInt("codigoControlCita"),
                                resultado.getDate("fecha"),
                                resultado.getString("horaInicio"),
                                resultado.getString("horaFin"),
                                resultado.getInt("codigoMedico"),
                                resultado.getInt("codigoPaciente")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaControlCita = FXCollections.observableList(lista);
    }

    public ControlCita buscarControlCita(int codigoArea){
	ControlCita resultado = null;
	try{
		PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarControlCita(?)}");
		procedimiento.setInt(1, codigoArea);
		ResultSet registro = procedimiento.executeQuery();
		while(registro.next()){
			resultado = new ControlCita(registro.getInt("codigoControlCita"),
					registro.getDate("fecha"),
                                        registro.getString("horaInicio"),
                                        registro.getString("horaFin"),
                                        registro.getInt("codigoMedico"),
                                        registro.getInt("codigoPaciente"));
		}
		}catch(Exception e){
		e.printStackTrace();
	}
		return resultado;
}
    
  public ObservableList<Medico> getMedicos() {
        ArrayList<Medico> lista = new ArrayList<Medico>();
        try {
            PreparedStatement procedimiento = (PreparedStatement) getInstancia().getConexion().prepareCall("{call sp_ListarMedicos}");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new Medico(resultado.getInt("codigoMedico"),
                        resultado.getInt("licenciaMedica"),
                        resultado.getString("nombres"),
                        resultado.getString("apellidos"),
                        resultado.getString("horaEntrada"),
                        resultado.getString("horaSalida"),
                        resultado.getInt("turnoMaximo"),
                        resultado.getString("sexo")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listaMedico = FXCollections.observableList(lista);
    }
 


      public Medico buscarMedico(int codigoMedico){
        Medico resultado = null;
        try {
        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedico(?)}");
        procedimiento.setInt(1, codigoMedico);
        ResultSet registro = procedimiento.executeQuery();
        while(registro.next()){
            resultado = new Medico(registro.getInt("codigoMedico"),
                                  registro.getInt("licenciaMedica"),
                                  registro.getString("nombres"),
                                  registro.getString("apellidos"),
                                  registro.getString("horaEntrada"),
                                  registro.getString("horaSalida"),
                                  registro.getInt("turnoMaximo"),
                                  registro.getString("sexo"));
        } 
        }catch(Exception e){
            e.printStackTrace();
        } return resultado;
    }
 public ObservableList<Paciente> getPaciente() {
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
        try {
            PreparedStatement procedimiento = (PreparedStatement) getInstancia().getConexion().prepareCall("{call sp_ListarPacientes}");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new Paciente(resultado.getInt("codigoPaciente"),
                        resultado.getString("DPI"),
                        resultado.getString("apellidos"),
                        resultado.getString("nombres"),
                        resultado.getDate("fechaNacimiento"),
                        resultado.getInt("edad"),
                        resultado.getString("direccion"),
                        resultado.getString("ocupacion"),
                        resultado.getString("sexo")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaPaciente = FXCollections.observableList(lista);
    }

       public Paciente buscarPaciente(int codigoPaciente){
        Paciente resultado = null;
        try {
        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarPaciente(?)}");
        procedimiento.setInt(1, codigoPaciente);
        ResultSet registro = procedimiento.executeQuery();
        while(registro.next()){
            resultado = new Paciente(registro.getInt("codigoPaciente"),
                                 
                                  registro.getString("DPI"),
                                  registro.getString("apellidos"),
                                  registro.getString("nombres"),
                                  registro.getDate("fechaNacimiento"),
                                  registro.getInt("edad"),
                                  registro.getString("direccion"),
                                  registro.getString("ocupacion"),
                                  registro.getString("sexo"));
        } 
        }catch(Exception e){
            e.printStackTrace();
        } return resultado;
    }
       public void eliminar(){
	switch(tipoDeOperacion){
		case GUARDAR:
			desactivarControles();
			limpiarControles();
			btnNuevo.setText("Nuevo");
			btnEliminar.setText("Eliminar");
			btnEditar.setDisable(false);
			btnReporte.setDisable(false);
			tipoDeOperacion = operaciones. NINGUNO;
			break;
		default:
			if (tblControlCitas.getSelectionModel().getSelectedItem() != null){
				int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de querer eliminar el registro seleccionado?", "Eliminar Control Citas", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (respuesta == JOptionPane.YES_OPTION){
					try{
						PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarControlCitas(?)}");
						procedimiento.setInt(1, ((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getCodigoControlCita());
						procedimiento.execute();
						listaControlCita.remove(tblControlCitas.getSelectionModel().getSelectedIndex());
						limpiarControles();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}else{
				JOptionPane.showMessageDialog(null, "Por favor seleccione un elemento");	
			}
	}
   }
       
 public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                limpiarControles();
                break;
            case GUARDAR:
                guardar();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                desactivarControles();
                limpiarControles();
                cargarDatos();
                break;
        }
    }


    public void guardar(){

        ControlCita registro = new ControlCita();
        registro.setFecha(fecha.getSelectedDate());
        registro.setHoraInicio(txtHoraInicio.getText());
        registro.setHoraFin(txtHoraFin.getText());
        registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
        registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarControlCita (?,?,?,?,?)}");
           
            procedimiento.setDate(1, new java.sql.Date(registro.getFecha().getTime()));
            procedimiento.setString(2, registro.getHoraInicio());
            procedimiento.setString(3, registro.getHoraFin());
            procedimiento.setInt(4, registro.getCodigoMedico());
            procedimiento.setInt(5, registro.getCodigoPaciente());
            procedimiento.execute();
            listaControlCita.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
        }
   


    public void editar(){
           switch(tipoDeOperacion){
            case NINGUNO:
                if(tblControlCitas.getSelectionModel().getSelectedItem() != null){
                    comboControles();
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tipoDeOperacion = ControlCitaController.operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null, "Por favor seleccione un elemento");
                }
                break;
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoDeOperacion = ControlCitaController.operaciones.NINGUNO;
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                cargarDatos();
                desactivarControles();
                limpiarControles();
                break;
        }
    }
    


    public void actualizar(){
            ControlCita registro = (ControlCita) tblControlCitas.getSelectionModel().getSelectedItem();
            registro.setFecha(fecha.getSelectedDate());
            registro.setHoraInicio(txtHoraInicio.getText());
            registro.setHoraFin(txtHoraFin.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarControlCitas(?,?,?,?)}");
            procedimiento.setInt(1, registro.getCodigoControlCita());
            procedimiento.setDate(2, new java.sql.Date(registro.getFecha().getTime()));
            procedimiento.setString(3, registro.getHoraInicio());
            procedimiento.setString(4, registro.getHoraFin());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        
        }
    }

  
    public void reporte(){
                switch(tipoDeOperacion){
                case ACTUALIZAR:
                desactivarControles();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                limpiarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                cargarDatos();
                tipoDeOperacion = operaciones.NINGUNO;
                break;
                }
    }

    
    
    public void desactivarControles(){
         grpFecha.setDisable(false);
        txtHoraInicio.setEditable(false);
        txtHoraFin.setEditable(false);
        cmbCodigoMedico.setDisable(true);
        cmbCodigoPaciente.setDisable(true);
    }
    
    public void comboControles(){
        grpFecha.setDisable(true);
        txtHoraInicio.setEditable(true);
        txtHoraFin.setEditable(true);
        cmbCodigoMedico.setDisable(true);
        cmbCodigoPaciente.setDisable(true);

    }
    
    public void activarControles(){
        grpFecha.setDisable(true);
        txtHoraInicio.setEditable(true);
        txtHoraFin.setEditable(true);
        cmbCodigoMedico.setDisable(false);
        cmbCodigoPaciente.setDisable(false);
    }
    
    public void limpiarControles(){
        fecha.selectedDateProperty().set(null);
        txtHoraInicio.setText("");
        txtHoraFin.setText("");
        cmbCodigoMedico.getSelectionModel().clearSelection();
        cmbCodigoPaciente.getSelectionModel().clearSelection();
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }

}