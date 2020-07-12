
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.angeldiaz.bean.ControlCita;
import org.angeldiaz.bean.Receta;
import org.angeldiaz.db.Conexion;
import static org.angeldiaz.db.Conexion.getInstancia;
import org.angeldiaz.sistema.Principal;


public class RecetaController implements Initializable{
    private Principal escenarioPrincipal;

    private enum operaciones {
        NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO
    };
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Receta> listaReceta;
     private ObservableList<ControlCita> listaControlCita;
    
    @FXML
    private TextField txtDescripcion;
    @FXML
    private TableView tblRecetas;
    @FXML
    private TableColumn colCodigoReceta;
    @FXML
    private TableColumn colDescripcion;
    @FXML
    private TableColumn colCodigoCitas;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnReporte;
    @FXML
    private ComboBox cmbControlCita;

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
       cmbControlCita.setItems(getControlCita());
     
    }

    public void cargarDatos(){
      tblRecetas.setItems(getReceta());
        colCodigoReceta.setCellValueFactory(new PropertyValueFactory<Receta, Integer>("codigoReceta"));
        colDescripcion.setCellValueFactory( new PropertyValueFactory<Receta, String>("descripcionReceta"));
        colCodigoCitas.setCellValueFactory(new PropertyValueFactory<Receta, Integer>("codigoControlCita"));
    }

    public void seleccionarElemento() {
        
        txtDescripcion.setText(((Receta) tblRecetas.getSelectionModel().getSelectedItem()).getDescripcionReceta());
        cmbControlCita.getSelectionModel().select(buscarControlCita(((ControlCita)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoControlCita()));
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
    
        public ObservableList<Receta> getReceta(){
        ArrayList<Receta> lista = new ArrayList<Receta>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarRecetas}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Receta(resultado.getInt("codigoReceta"),
                                resultado.getString("descripcionReceta"),
                                resultado.getInt("codigoControlCita")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return listaReceta = FXCollections.observableList(lista);
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
			if (tblRecetas.getSelectionModel().getSelectedItem() != null){
				int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de querer eliminar el registro seleccionado?", "Eliminar Receta", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (respuesta == JOptionPane.YES_OPTION){
					try{
						PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarReceta(?)}");
						procedimiento.setInt(1, ((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta());
						procedimiento.execute();
						listaReceta.remove(tblRecetas.getSelectionModel().getSelectedIndex());
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
        Receta registro = new Receta();
        registro.setDescripcionReceta(txtDescripcion.getText());
        registro.setCodigoControlCita(((ControlCita)cmbControlCita.getSelectionModel().getSelectedItem()).getCodigoControlCita());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarRecetas (?,?)}");
            procedimiento.setString(1, registro.getDescripcionReceta());
            procedimiento.setInt(2, registro.getCodigoControlCita());
            procedimiento.execute();
            listaReceta.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
        }
   
 
   public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblRecetas.getSelectionModel().getSelectedItem() != null){
                    comboControles();
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tipoDeOperacion = RecetaController.operaciones.ACTUALIZAR;
                }else{
                    JOptionPane.showMessageDialog(null, "Por favor seleccione un elemento");
                }
                break;
            case ACTUALIZAR:
                actualizar();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                tipoDeOperacion = RecetaController.operaciones.NINGUNO;
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                cargarDatos();
                desactivarControles();
                limpiarControles();
                break;
        }
    }
    
public void actualizar(){
            Receta registro = (Receta) tblRecetas.getSelectionModel().getSelectedItem();
            registro.setDescripcionReceta(txtDescripcion.getText());
            registro.setCodigoControlCita(((ControlCita)cmbControlCita.getSelectionModel().getSelectedItem()).getCodigoControlCita());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarRecetas(?,?,?,?)}");
            procedimiento.setInt(1, registro.getCodigoReceta());
            procedimiento.setString(2, registro.getDescripcionReceta());
            procedimiento.setInt(3, registro.getCodigoControlCita());
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
        txtDescripcion.setEditable(false);
        cmbControlCita.setDisable(true);
    }
    
    public void comboControles(){
        txtDescripcion.setEditable(true);
        cmbControlCita.setDisable(true);

    }
    
    public void activarControles(){
         txtDescripcion.setEditable(true);
        cmbControlCita.setDisable(false);
    }
    
    public void limpiarControles(){
        txtDescripcion.setText("");
        cmbControlCita.getSelectionModel().clearSelection();
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

