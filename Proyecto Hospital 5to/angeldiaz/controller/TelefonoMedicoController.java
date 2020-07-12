package org.angeldiaz.controller;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.angeldiaz.bean.Medico;
import org.angeldiaz.bean.TelefonosMedico;
import org.angeldiaz.db.Conexion;
import org.angeldiaz.sistema.Principal;

public class TelefonoMedicoController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Medico> listaMedico;
    private ObservableList<TelefonosMedico> listaTelefonoMedico;
    @FXML private TextField txtTelefonoPersonal;
    @FXML private TextField txtTelefonoTrabajo;
    @FXML private ComboBox cmbCodigoMedico;
    @FXML private TableView tblTelefonosMedico;
    @FXML private TableColumn colCodigoTelefono;
    @FXML private TableColumn colTelPersonal;
    @FXML private TableColumn colTelTrabajo;
    @FXML private TableColumn colCodigoMedico;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoMedico.setItems(getMedicos());
    }
    
    public void cargarDatos(){
        tblTelefonosMedico.setItems(getTelefonos());
        colCodigoTelefono.setCellValueFactory(new PropertyValueFactory<TelefonosMedico, Integer>("codigoTelefonoMedico"));
        colTelPersonal.setCellValueFactory(new PropertyValueFactory<TelefonosMedico, String>("telefonoPersonal"));
        colTelTrabajo.setCellValueFactory(new PropertyValueFactory<TelefonosMedico, String>("telefonoTrabajo"));
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<TelefonosMedico, Integer>("codigoMedico"));
        
    }
   
    
    public ObservableList<TelefonosMedico> getTelefonos(){
        ArrayList<TelefonosMedico> lista = new ArrayList<TelefonosMedico>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarTelefonosMedico}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new TelefonosMedico(resultado.getInt("codigoTelefonoMedico"),
                                resultado.getString("telefonoPersonal"),
                                resultado.getString("telefonoTrabajo"),
                                resultado.getInt("codigoMedico")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaTelefonoMedico = FXCollections.observableList(lista);
    }
    
    public ObservableList<Medico> getMedicos(){
        ArrayList<Medico> lista = new ArrayList<Medico>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedicos}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Medico(resultado.getInt("codigoMedico"),
                                resultado.getInt("licenciaMedica"),
                                resultado.getString("nombres"),
                                resultado.getString("apellidos"),
                                resultado.getString("horaEntrada"),
                                resultado.getString("horaSalida"),
                                resultado.getInt("turnoMaximo"),
                                resultado.getString("Sexo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaMedico = FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        txtTelefonoPersonal.setText(String.valueOf(((TelefonosMedico)tblTelefonosMedico.getSelectionModel().getSelectedItem()).getTelefonoPersonal()));
        txtTelefonoTrabajo.setText(((TelefonosMedico)tblTelefonosMedico.getSelectionModel().getSelectedItem()).getTelefonoTrabajo());
        cmbCodigoMedico.getSelectionModel().select(buscarMedico(((TelefonosMedico)tblTelefonosMedico.getSelectionModel().getSelectedItem()).getCodigoMedico()));
        
    }
    
    public TelefonosMedico buscarTelefonosMedico(int codigoTelefonoMedico){
	TelefonosMedico resultado = null;
	try{
		PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarTelefonoMedico(?)}");
		procedimiento.setInt(1, codigoTelefonoMedico);
		ResultSet registro = procedimiento.executeQuery();
		while(registro.next()){
			resultado = new TelefonosMedico(registro.getInt("codigoTelefonoMedico"),
									registro.getString("telefonoPersonal"),
									registro.getString("telefonoTrabajo"),
									registro.getInt("codigoMedico"));
		}
		}catch(Exception e){
		e.printStackTrace();
	}
		return resultado;
}
    
    public Medico buscarMedico(int codigoMedico){
	Medico resultado = null;
	try{
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
	}
		return resultado;
}
  
    public void reporte(){
        switch(tipoDeOperacion){
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnReporte.setText("Reporte");
                btnEditar.setText("Editar");
            }
                
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
			tipoDeOperacion = operaciones.NINGUNO;
			break;
		default:
			if (tblTelefonosMedico.getSelectionModel().getSelectedItem() != null){
				int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de querer eliminar el registro seleccionado?", "Eliminar el Telefono del Medico", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (respuesta == JOptionPane.YES_OPTION){
					try{
						PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarTelefonoMedico(?)}");
						procedimiento.setInt(1, ((TelefonosMedico)tblTelefonosMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
						procedimiento.execute();
						listaTelefonoMedico.remove(tblTelefonosMedico.getSelectionModel().getSelectedIndex());
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
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }
    
    public void guardar(){
        TelefonosMedico registro = new TelefonosMedico();
        registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
        registro.setTelefonoTrabajo(txtTelefonoTrabajo.getText());
        registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarTelefonoMedico(?,?,?)}");
            procedimiento.setString(1, registro.getTelefonoPersonal());
            procedimiento.setString(2, registro.getTelefonoTrabajo());
            procedimiento.setInt(3, registro.getCodigoMedico());
            procedimiento.execute();
            listaTelefonoMedico.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblTelefonosMedico.getSelectionModel().getSelectedItem() != null){
                    activarControles();
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null, "Por favor seleccione un elemento");
                }break;
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoDeOperacion = operaciones.NINGUNO;
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                cargarDatos();
                desactivarControles();
                limpiarControles();
                break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarTelefonoMedico(?,?,?,?)}");
            TelefonosMedico registro = (TelefonosMedico)tblTelefonosMedico.getSelectionModel().getSelectedItem();
            registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
            registro.setTelefonoTrabajo(txtTelefonoTrabajo.getText());
            registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
            procedimiento.setInt(1, registro.getCodigoTelefonoMedico());
            procedimiento.setString(2, registro.getTelefonoPersonal());
            procedimiento.setString(3, registro.getTelefonoTrabajo());
            procedimiento.setInt(4, registro.getCodigoMedico());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        
        }
    }
    
    
    public void cancelar(){
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoDeOperacion = operaciones.NINGUNO;
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                cargarDatos();
                desactivarControles();
                limpiarControles();

    }
    
   
    public void desactivarControles(){
        txtTelefonoPersonal.setEditable(false);
        txtTelefonoTrabajo.setEditable(false);
        cmbCodigoMedico.setEditable(false);
    }
    
    public void activarControles(){
        txtTelefonoPersonal.setEditable(true);
        txtTelefonoTrabajo.setEditable(true);
        cmbCodigoMedico.setEditable(false);
    }
    
    public void limpiarControles(){
        txtTelefonoPersonal.setText("");
        txtTelefonoTrabajo.setText("");
        cmbCodigoMedico.getSelectionModel().clearSelection();
    }
    

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void VentanaMedicos(){
        escenarioPrincipal.ventanaMedicos();
    }
    
}
    



