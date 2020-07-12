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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javax.swing.JOptionPane;
import org.angeldiaz.bean.Cargo;
import org.angeldiaz.db.Conexion;
import static org.angeldiaz.db.Conexion.getInstancia;
import org.angeldiaz.sistema.Principal;

public class CargoController implements Initializable {

    private Principal escenarioPrincipal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }

    private enum operaciones {
        NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO
    };
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Cargo> ListaCargo;

    @FXML
    private TextField txtCargo;
    @FXML
    private TableView tblCargos;
    @FXML
    private TableColumn colCargo;
    @FXML
    private TableColumn colCodigoCargo;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnReporte;

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
    
    public void guardar(){if((txtCargo.getText().equals(""))){
            JOptionPane.showMessageDialog(null, "Alguna casilla esta vacia");
        }else{
        Cargo registro = new Cargo();
        registro.setNombreCargo(txtCargo.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarCargo(?)}");
            procedimiento.setString(1, registro.getNombreCargo());
            procedimiento.execute();
            ListaCargo.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
        }
    }

    public void cargarDatos() {
        tblCargos.setItems(getCargos());
        colCodigoCargo.setCellValueFactory(new PropertyValueFactory<Cargo, Integer>("codigoCargo"));
        colCargo.setCellValueFactory(new PropertyValueFactory<Cargo, String>("nombreCargo"));
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
        return ListaCargo = FXCollections.observableArrayList(lista);
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
   
    public void validarLe(KeyEvent evento){
    char e = evento.getCharacter().charAt(0);

    if(!Character.isLetter(e)){
        if(e != ' '){
            evento.consume();
            JOptionPane.showMessageDialog(null, "Por favor ingrese letras");
        }else;
    }
}
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblCargos.getSelectionModel().getSelectedItem() != null){
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
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarCargo(?,?)}");
            Cargo registro = (Cargo)tblCargos.getSelectionModel().getSelectedItem();
            registro.setNombreCargo(txtCargo.getText());
            procedimiento.setInt(1, registro.getCodigoCargo());
            procedimiento.setString(2, registro.getNombreCargo());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        
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
			if (tblCargos.getSelectionModel().getSelectedItem() != null){
				int respuesta = JOptionPane.showConfirmDialog(null, "¿eliminar el registro seleccionado?", "Eliminar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (respuesta == JOptionPane.YES_OPTION){
					try{
						PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarCargo(?)}");
						procedimiento.setInt(1, ((Cargo)tblCargos.getSelectionModel().getSelectedItem()).getCodigoCargo());
						procedimiento.execute();
						ListaCargo.remove(tblCargos.getSelectionModel().getSelectedIndex());
						limpiarControles();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}else{
				JOptionPane.showMessageDialog(null, "seleccione un elemento");	
			}
	}
}

    public void desactivarControles() {
        txtCargo.setEditable(false);
    }

    public void activarControles() {
        txtCargo.setEditable(true);
    }

    public void limpiarControles() {
        txtCargo.setText("");
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
