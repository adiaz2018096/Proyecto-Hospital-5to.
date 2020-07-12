
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
import org.angeldiaz.bean.Area;
import org.angeldiaz.bean.Cargo;
import org.angeldiaz.bean.Medico;
import org.angeldiaz.bean.Paciente;
import org.angeldiaz.bean.ResponsableTurno;
import org.angeldiaz.db.Conexion;
import static org.angeldiaz.db.Conexion.getInstancia;
import org.angeldiaz.sistema.Principal;



public class ResponsableTurnoController implements Initializable {

 private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<ResponsableTurno> listaResponsableTurno;
    private ObservableList<Area> listaArea;
    private ObservableList<Cargo> listaCargo;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtNumero;
    @FXML private ComboBox cmbArea;
    @FXML private ComboBox cmbCargo;
    @FXML private TableView tblContactos;
    @FXML private TableColumn colArea;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colCargo;
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
       cmbArea.setItems(getAreas());
       cmbCargo.setItems(getCargos());
    }


    public void cargarDatos(){
      tblContactos.setItems(getResponsableTurno());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Contacto, Integer>("codigoResponsableTurno"));
        colNombres.setCellValueFactory( new PropertyValueFactory<Contacto, String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Contacto, String>("apellidos"));
        colNumero.setCellValueFactory(new PropertyValueFactory<Contacto, String>("numeroContacto"));
        colArea.setCellValueFactory(new PropertyValueFactory<Contacto, Integer>("codigoArea"));
        colCargo.setCellValueFactory(new PropertyValueFactory<Contacto, Integer>("codigoCargo"));
    }
    
 public void seleccionarElemento() {
        
        txtNombres.setText(((ResponsableTurno) tblContactos.getSelectionModel().getSelectedItem()).getNombres());
        txtApellidos.setText(((ResponsableTurno) tblContactos.getSelectionModel().getSelectedItem()).getApellidos());
        txtNumero.setText(((ResponsableTurno) tblContactos.getSelectionModel().getSelectedItem()).getNumeroContacto());
        cmbArea.getSelectionModel().select(buscarArea(((ResponsableTurno)tblContactos.getSelectionModel().getSelectedItem()).getCodigoArea()));
        cmbCargo.getSelectionModel().select(buscarCargo(((ResponsableTurno)tblContactos.getSelectionModel().getSelectedItem()).getCodigoCargo()));
 }

    public ObservableList<ResponsableTurno> getResponsableTurno(){
        ArrayList<ResponsableTurno> lista = new ArrayList<ResponsableTurno>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarResponsableTurno}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new ResponsableTurno(resultado.getInt("codigoResponsableTurno"),
                                resultado.getString("nombreResponsable"),
                                resultado.getString("apellidosResponsable"),
                                resultado.getString("telefonoPersonal"),
                                resultado.getInt("codigoArea"),
                                resultado.getInt("codigoCargo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaResponsableTurno = FXCollections.observableList(lista);
    }


  public Area buscarArea(int codigoArea){
	Area resultado = null;
	try{
		PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarArea(?)}");
		procedimiento.setInt(1, codigoArea);
		ResultSet registro = procedimiento.executeQuery();
		while(registro.next()){
			resultado = new Area(registro.getInt("codigoArea"),
					registro.getString("nombreArea"));
		}
		}catch(Exception e){
		e.printStackTrace();
	}
		return resultado;
}
  

  public ObservableList<Area> getAreas() {
        ArrayList<Area> lista = new ArrayList<Area>();

        try {
            PreparedStatement procedimiento = (PreparedStatement) getInstancia().getConexion().prepareCall("{call sp_ListarAreas}");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new Area(resultado.getInt("codigoArea"),
                        resultado.getString("nombreArea")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaArea = FXCollections.observableArrayList(lista);
    }

public ObservableList<Cargo> getCargos() {
        ArrayList<Cargo> lista = new ArrayList<Cargo>();

        try {
            PreparedStatement procedimiento = (PreparedStatement) getInstancia().getConexion().prepareCall("{call sp_ListarCargos}");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()) {
                lista.add(new Cargo(resultado.getInt("codigoCargo"),
                        resultado.getString("nombreCargo")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaCargo = FXCollections.observableArrayList(lista);
    }
    
    public Cargo buscarCargo(int codigoCargo){
	Cargo resultado = null;
	try{
		PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarCargo(?)}");
		procedimiento.setInt(1, codigoCargo);
		ResultSet registro = procedimiento.executeQuery();
		while(registro.next()){
			resultado = new Cargo(registro.getInt("codigoCargo"),
					registro.getString("nombreCargo"));
		}
		}catch(Exception e){
		e.printStackTrace();
	}
		return resultado;

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
			if (tblContactos.getSelectionModel().getSelectedItem() != null){
				int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de querer eliminar el registro seleccionado?", "Eliminar ResponsableTurno", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (respuesta == JOptionPane.YES_OPTION){
					try{
						PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarResponsableTurno(?)}");
						procedimiento.setInt(1, ((ResponsableTurno)tblContactos.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
						procedimiento.execute();
						listaResponsableTurno.remove(tblContactos.getSelectionModel().getSelectedIndex());
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
        ResponsableTurno registro = new ResponsableTurno();
        registro.setNombres(txtNombres.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setNumeroContacto(txtNumero.getText());
        registro.setCodigoArea(((Area)cmbArea.getSelectionModel().getSelectedItem()).getCodigoArea());
        registro.setCodigoCargo(((Cargo)cmbCargo.getSelectionModel().getSelectedItem()).getCodigoCargo());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarResponsableTurno (?,?,?,?,?)}");
            procedimiento.setString(1, registro.getNombres());
            procedimiento.setString(2, registro.getApellidos());
            procedimiento.setString(3, registro.getNumeroContacto());
            procedimiento.setInt(4, registro.getCodigoArea());
            procedimiento.setInt(5, registro.getCodigoCargo());
            procedimiento.execute();
            listaResponsableTurno.add(registro);
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
                    tipoDeOperacion = ResponsableTurnoController.operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null, "Por favor seleccione un elemento");
                }
                break;
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoDeOperacion = ResponsableTurnoController.operaciones.NINGUNO;
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                cargarDatos();
                desactivarControles();
                limpiarControles();
                break;
        }
    }
    
public void actualizar(){
            ResponsableTurno registro = (ResponsableTurno) tblContactos.getSelectionModel().getSelectedItem();
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setNumeroContacto(txtNumero.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarResponsableTurno(?,?,?,?)}");
            procedimiento.setInt(1, registro.getCodigoResponsableTurno());
            procedimiento.setString(2, registro.getNombres());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getNumeroContacto());
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
        cmbArea.setDisable(true);
        cmbCargo.setDisable(true);
    }
    
    public void comboControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtNumero.setEditable(true);
        cmbArea.setDisable(true);
        cmbCargo.setDisable(true);

    }
    
    public void activarControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtNumero.setEditable(true);
        cmbArea.setDisable(false);
        cmbCargo.setDisable(false);
    }
    
    public void limpiarControles(){
        txtNombres.setText("");
        txtApellidos.setText("");
        txtNumero.setText("");
        cmbArea.getSelectionModel().clearSelection();
        cmbCargo.getSelectionModel().clearSelection();
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
