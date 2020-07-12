
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
import org.angeldiaz.bean.Area;
import org.angeldiaz.bean.Cargo;
import org.angeldiaz.bean.Contacto;
import org.angeldiaz.bean.Especialidad;
import org.angeldiaz.bean.Horario;
import org.angeldiaz.bean.Medico;
import org.angeldiaz.bean.MedicoEspecialidad;
import org.angeldiaz.bean.ResponsableTurno;
import org.angeldiaz.db.Conexion;
import static org.angeldiaz.db.Conexion.getInstancia;
import org.angeldiaz.sistema.Principal;


public class MedicoEspecialidadController implements Initializable{

    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<MedicoEspecialidad> listaMedicoEspecialidad;
    private ObservableList<Medico> listaMedico;
    private ObservableList<Especialidad> listaEspecialidad;
    private ObservableList<Horario> listaHorario;
    @FXML private ComboBox cmbMedico;
    @FXML private ComboBox cmbEspecialidad;
    @FXML private ComboBox cmbHorario;
    @FXML private TableView tblContactos;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colMedico;
    @FXML private TableColumn colEspecialidad;
    @FXML private TableColumn colHorario;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbMedico.setItems(getMedicos());
        cmbEspecialidad.setItems(getEspecialidad());
        cmbHorario.setItems(getHorarios());
    }
    
    public void cargarDatos(){
      tblContactos.setItems(getMedicoEspecialidad());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Contacto, Integer>("codigoMedicoEspecialidad"));
        colMedico.setCellValueFactory(new PropertyValueFactory<Contacto, Integer>("codigoMedico"));
        colEspecialidad.setCellValueFactory(new PropertyValueFactory<Contacto, Integer>("codigoEspecialidad"));
        colHorario.setCellValueFactory(new PropertyValueFactory<Contacto, Integer>("codigoHorario"));
        
    }
 
    public void seleccionarElemento() {
        cmbMedico.getSelectionModel().select(buscarMedico(((MedicoEspecialidad)tblContactos.getSelectionModel().getSelectedItem()).getCodigoMedico()));
        cmbEspecialidad.getSelectionModel().select(buscarEspecialidad(((MedicoEspecialidad)tblContactos.getSelectionModel().getSelectedItem()).getCodigoEspecialidad()));
        cmbHorario.getSelectionModel().select(buscarHorario(((MedicoEspecialidad)tblContactos.getSelectionModel().getSelectedItem()).getCodigoHorario()));
    }

   public ObservableList<MedicoEspecialidad> getMedicoEspecialidad(){
        ArrayList<MedicoEspecialidad> lista = new ArrayList<MedicoEspecialidad>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedico_Especialidad}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new MedicoEspecialidad(resultado.getInt("CodigoMedicoEspecialidad"),
                                resultado.getInt("codigoMedico"),
                                resultado.getInt("codigoEspecialidad"),
                                resultado.getInt("codigoHorario")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaMedicoEspecialidad = FXCollections.observableList(lista);
    }
   
  
       public Medico buscarMedico(int codigoMedico){
        Medico resultado = null;
        try {
        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedico(?)}");
        procedimiento.setInt(1, codigoMedico);
        ResultSet registro = procedimiento.executeQuery();
        while(registro.next()){
            resultado = new Medico(registro.getInt("codigOMedico"),
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
   
  
        public Especialidad buscarEspecialidad(int codigoEspecialidad){
        Especialidad resultado = null;
        try {
        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarEspecialidad(?)}");
        procedimiento.setInt(1, codigoEspecialidad);
        ResultSet registro = procedimiento.executeQuery();
        while(registro.next()){
            resultado = new Especialidad(registro.getInt("codigoEspecialidad"),
                                  registro.getString("nombreEspecialidad"));
                                 
        } 
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
 
        
        public ObservableList<Especialidad> getEspecialidad() {
        ArrayList<Especialidad> lista = new ArrayList<Especialidad>();
        try {
            PreparedStatement procedimiento = (PreparedStatement) getInstancia().getConexion().prepareCall("{call sp_ListarEspecialidades}");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new Especialidad(resultado.getInt("codigoEspecialidad"),
                        resultado.getString("nombreEspecialidad")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaEspecialidad = FXCollections.observableArrayList(lista);
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

public void eliminar(){
	switch(tipoDeOperacion){
		case GUARDAR:
			desactivarControles();
			limpiarControles();
			btnNuevo.setText("Nuevo");
			btnEliminar.setText("Eliminar");
			btnEditar.setDisable(false);
			btnReporte.setDisable(false);
			tipoDeOperacion = MedicoEspecialidadController.operaciones. NINGUNO;
			break;
		default:
			if (tblContactos.getSelectionModel().getSelectedItem() != null){
				int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de querer eliminar el registro seleccionado?", "Eliminar MedicoEspecialidad", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (respuesta == JOptionPane.YES_OPTION){
					try{
						PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarMedicoEspecialidad(?)}");
						procedimiento.setInt(1, ((MedicoEspecialidad)tblContactos.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
						procedimiento.execute();
						listaMedicoEspecialidad.remove(tblContactos.getSelectionModel().getSelectedIndex());
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
        MedicoEspecialidad registro = new MedicoEspecialidad();
        registro.setCodigoMedico(((Medico)cmbMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
        registro.setCodigoEspecialidad(((Especialidad)cmbEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
        registro.setCodigoHorario(((Horario)cmbHorario.getSelectionModel().getSelectedItem()).getCodigoHorario());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarMedico_Especialidad (?,?,?)}");
            procedimiento.setInt(1, registro.getCodigoMedico());
            procedimiento.setInt(2, registro.getCodigoEspecialidad());
            procedimiento.setInt(3, registro.getCodigoHorario());

            procedimiento.execute();
            listaMedicoEspecialidad.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
 }
   


   public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblContactos.getSelectionModel().getSelectedItem() != null){
                    comboControles();
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null, "Por favor seleccione un elemento");
                }
                break;
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
            MedicoEspecialidad registro = (MedicoEspecialidad) tblContactos.getSelectionModel().getSelectedItem();
           
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarMedicoEspecialidad(?,?,?,?)}");
            procedimiento.setInt(1, registro.getCodigoMedicoEspecialidad());
            procedimiento.setInt(2, registro.getCodigoMedico());
            procedimiento.setInt(3, registro.getCodigoEspecialidad());
            procedimiento.setInt(4, registro.getCodigoHorario());
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
        cmbMedico.setDisable(true);
        cmbEspecialidad.setDisable(true);
        cmbHorario.setDisable(true);
 }
    
    public void comboControles(){
        cmbMedico.setDisable(true);
        cmbEspecialidad.setDisable(true);
        cmbHorario.setDisable(true);
    }


  public void activarControles(){
        cmbMedico.setDisable(false);
        cmbEspecialidad.setDisable(false);
        cmbHorario.setDisable(false);
  
  }
    
    public void limpiarControles(){
        cmbMedico.getSelectionModel().clearSelection();
        cmbEspecialidad.getSelectionModel().clearSelection();
        cmbHorario.getSelectionModel().clearSelection();
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
