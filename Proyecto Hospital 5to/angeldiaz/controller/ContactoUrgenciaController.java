package org.angeldiaz.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Set;
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
import javafx.scene.input.KeyEvent;
import javax.swing.JOptionPane;
import org.angeldiaz.bean.Contacto;
import org.angeldiaz.bean.Paciente;
import org.angeldiaz.db.Conexion;
import org.angeldiaz.sistema.Principal;


public class ContactoUrgenciaController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Contacto> listaContactoUrgencia;
    private ObservableList<Paciente> listaPaciente;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtNumero;
    @FXML private ComboBox cmbPaciente;
    @FXML private TableView tblContactos;
    @FXML private TableColumn colPaciente;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colNumero;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
       cmbPaciente.setItems(getPacientes());
    }
    
    
    public void cargarDatos(){
        tblContactos.setItems(getContactos());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Contacto, Integer>("codigoContactoUrgencia"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Contacto, String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Contacto, String>("apellidos"));
        colNumero.setCellValueFactory(new PropertyValueFactory<Contacto, String>("numeroContacto"));
        colPaciente.setCellValueFactory(new PropertyValueFactory<Contacto, Integer>("codigoPaciente"));
    }
    
    public ObservableList<Contacto> getContactos(){
        ArrayList<Contacto> lista = new ArrayList<Contacto>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarContactoUrgencia}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Contacto(resultado.getInt("codigoContactoUrgencia"),
                                resultado.getString("nombres"),
                                resultado.getString("apellidos"),
                                resultado.getString("numeroContacto"),
                                resultado.getInt("codigoPaciente")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaContactoUrgencia = FXCollections.observableList(lista);
    }
    
    public ObservableList<Paciente> getPacientes(){
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarPacientes}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Paciente(resultado.getInt("codigoPaciente"),
                                resultado.getString("DPI"),
                                resultado.getString("apellidos"),
                                resultado.getString("nombres"),
                                resultado.getDate("fechaNacimiento"),
                                resultado.getInt("edad"),
                                resultado.getString("Direccion"),
                                resultado.getString("Ocupacion"),
                                resultado.getString("Sexo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaPaciente = FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblContactos.getSelectionModel().getSelectedItem() != null){
        txtNombres.setText(((Contacto)tblContactos.getSelectionModel().getSelectedItem()).getNombres());
        txtApellidos.setText(((Contacto)tblContactos.getSelectionModel().getSelectedItem()).getApellidos());
        txtNumero.setText(((Contacto)tblContactos.getSelectionModel().getSelectedItem()).getNumeroContacto());
        cmbPaciente.getSelectionModel().select(buscarPaciente(((Contacto)tblContactos.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
        }
    }
    
        public Contacto buscarContactoUrgencia(int codigoContactoUrgencia){
	Contacto resultado = null;
	try{
		PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarContactoUrgencia(?)}");
		procedimiento.setInt(1, codigoContactoUrgencia);
		ResultSet registro = procedimiento.executeQuery();
		while(registro.next()){
			resultado = new Contacto(registro.getInt("codigoContactoUrgencia"),
									registro.getString("nombres"),
									registro.getString("apellidos"),
									registro.getString("numeroContacto"),
									registro.getInt("codigoPaciente"));
		}
		}catch(Exception e){
		e.printStackTrace();
	}
		return resultado;
}
        
        public Paciente buscarPaciente(int codigoPaciente){
	Paciente resultado = null;
	try{
		PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarPaciente(?)}");
		procedimiento.setInt(1, codigoPaciente);
		ResultSet registro = procedimiento.executeQuery();
		while(registro.next()){
			resultado = new Paciente(registro.getInt("codigoPaciente"),
									registro.getString("DPI"),
									registro.getString("nombres"),
									registro.getString("apellidos"),
									registro.getDate("fechaNacimiento"),
									registro.getInt("edad"),
									registro.getString("direccion"),
                                                                        registro.getString("ocupacion"),
									registro.getString("sexo"));
		}
		}catch(Exception e){
		e.printStackTrace();
	}
		return resultado;
}
        
        public void validarNumero(KeyEvent evento){
        char e = evento.getCharacter().charAt(0);
        
        if(!Character.isDigit(e)){
            evento.consume();
            JOptionPane.showMessageDialog(null, "Por favor en esta casilla solo se aceptan numeros");
        }
    } 
    
    public void validarLe(KeyEvent evento){
        char e = evento.getCharacter().charAt(0);
        
        if(!Character.isLetter(e)){
            if(e != ' '){
                evento.consume();
                JOptionPane.showMessageDialog(null, "Por favor ingrese solamente Consonantes y vocales");
            }else;
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
			if (tblContactos.getSelectionModel().getSelectedItem() != null){
				int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de querer eliminar el registro seleccionado?", "Eliminar Contacto", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (respuesta == JOptionPane.YES_OPTION){
					try{
						PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarContactoUrgencia(?)}");
						procedimiento.setInt(1, ((Contacto)tblContactos.getSelectionModel().getSelectedItem()).getCodigoContactoUrgencia());
						procedimiento.execute();
						listaContactoUrgencia.remove(tblContactos.getSelectionModel().getSelectedIndex());
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
        if(txtNombres.getText().equals("") || (txtApellidos.getText().equals("")) || (txtNumero.getText().equals(""))){
            JOptionPane.showMessageDialog(null, "Verifique que todas las casillas esten llenas");
        }else{
        Contacto registro = new Contacto();
        registro.setNombres(txtNombres.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setNumeroContacto(txtNumero.getText());
        registro.setCodigoPaciente(((Paciente)cmbPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarContactoUrgencia(?,?,?,?)}");
            procedimiento.setString(1, registro.getNombres());
            procedimiento.setString(2, registro.getApellidos());
            procedimiento.setString(3, registro.getNumeroContacto());
            procedimiento.setInt(4, registro.getCodigoPaciente());
            procedimiento.execute();
            listaContactoUrgencia.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
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
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarContactoUrgencia(?,?,?,?,?)}");
            Contacto registro = (Contacto)tblContactos.getSelectionModel().getSelectedItem();
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setNumeroContacto(txtNumero.getText());
            registro.setCodigoPaciente(((Paciente)cmbPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
            procedimiento.setInt(1, registro.getCodigoContactoUrgencia());
            procedimiento.setString(2, registro.getNombres());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getNumeroContacto());
            procedimiento.setInt(5, registro.getCodigoPaciente());
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
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtNumero.setEditable(false);
        cmbPaciente.setDisable(true);
    }
    
    public void comboControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtNumero.setEditable(true);
        cmbPaciente.setDisable(true);
    }
    
    public void activarControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtNumero.setEditable(true);
        cmbPaciente.setDisable(false);
    }
    
    public void limpiarControles(){
        txtNombres.setText("");
        txtApellidos.setText("");
        txtNumero.setText("");
        cmbPaciente.getSelectionModel().clearSelection();
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaPacientes(){
        escenarioPrincipal.ventanaPacientes();
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }

}
