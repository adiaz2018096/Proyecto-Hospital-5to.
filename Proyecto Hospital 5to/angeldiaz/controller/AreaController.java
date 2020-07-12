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
import org.angeldiaz.bean.Area;
import org.angeldiaz.db.Conexion;
import static org.angeldiaz.db.Conexion.getInstancia;
import org.angeldiaz.sistema.Principal;
import java.util.HashMap;
import java.util.Map;
import org.angeldiaz.report.GenerarReporte;

public class AreaController implements Initializable {

    private Principal escenarioPrincipal;

    private enum operaciones {
        NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO
    };
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Area> listaArea;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    @FXML
    private TextField txtArea;
    @FXML
    private TableView tblAreas;
    @FXML
    private TableColumn colArea;
    @FXML
    private TableColumn colCodigoArea;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnReporte;
    
    
    public void seleccionarElemento() {
        txtArea.setText(((Area) tblAreas.getSelectionModel().getSelectedItem()).getNombreArea());
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
    
    public void guardar(){if((txtArea.getText().equals(""))){
            JOptionPane.showMessageDialog(null, "Alguna casilla esta vacia");
        }else{
        Area registro = new Area();
        registro.setNombreArea(txtArea.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarArea(?)}");
            procedimiento.setString(1, registro.getNombreArea());
            procedimiento.execute();
            listaArea.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
        }
    }

    public void cargarDatos() {
        tblAreas.setItems(getAreas());
        colCodigoArea.setCellValueFactory(new PropertyValueFactory<Area, Integer>("codigoArea"));
        colArea.setCellValueFactory(new PropertyValueFactory<Area, String>("nombreArea"));
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
                if(tblAreas.getSelectionModel().getSelectedItem() != null){
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
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarArea(?,?)}");
            Area registro = (Area)tblAreas.getSelectionModel().getSelectedItem();
            registro.setNombreArea(txtArea.getText());
            procedimiento.setInt(1, registro.getCodigoArea());
            procedimiento.setString(2, registro.getNombreArea());
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
			if (tblAreas.getSelectionModel().getSelectedItem() != null){
				int respuesta = JOptionPane.showConfirmDialog(null, "¿eliminar el registro seleccionado?", "Eliminar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (respuesta == JOptionPane.YES_OPTION){
					try{
						PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarArea(?)}");
						procedimiento.setInt(1, ((Area)tblAreas.getSelectionModel().getSelectedItem()).getCodigoArea());
						procedimiento.execute();
						listaArea.remove(tblAreas.getSelectionModel().getSelectedIndex());
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
        txtArea.setEditable(false);
    }

    public void activarControles() {
        txtArea.setEditable(true);
    }

    public void limpiarControles() {
        txtArea.setText("");
        
    }

       public void generarReporte(){
        switch (tipoDeOperacion){
            case NINGUNO:
                imprimirReporte();
                tipoDeOperacion = operaciones.ACTUALIZAR;
                break;
            case ACTUALIZAR:
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoDeOperacion = operaciones.NINGUNO;
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                break;       
                
        }
    
    }
    
    
      public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoAreas", null);
        GenerarReporte.mostrarReporte("ReporteAreas.jasper", "Reporte de Ares", parametros);
    
    
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
