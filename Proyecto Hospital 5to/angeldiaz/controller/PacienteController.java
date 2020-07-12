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
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.angeldiaz.bean.Paciente;
import org.angeldiaz.db.Conexion;
import static org.angeldiaz.db.Conexion.getInstancia;
import org.angeldiaz.sistema.Principal;

public class PacienteController implements Initializable {

    private Principal escenarioPrincipal;

    private enum operaciones {
        NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO
    }
      
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Paciente> listaPaciente;
    private DatePicker fecha;

    @FXML
    private TextField txtEdad;
    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtApellidos;
    @FXML
    private TextField txtDireccion;
    @FXML
    private TextField txtOcupacion;
    @FXML
    private GridPane grpFecha;
    @FXML
    private TextField txtDPI;
    @FXML
    private TextField txtSexo;
    @FXML
    private TableView tblPacientes;
    @FXML
    private TableColumn colCodigo;
    @FXML
    private TableColumn colDPI;
    @FXML
    private TableColumn colNombres;
    @FXML
    private TableColumn colApellidos;
    @FXML
    private TableColumn colDireccion;
    @FXML
    private TableColumn colOcupacion;
    @FXML
    private TableColumn colFechaNacimiento;
    @FXML
    private TableColumn colEdad;
    @FXML
    private TableColumn colSexo;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnReporte;

      @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add("/org/angeldiaz/resource/DatePicker.css");
        grpFecha.add(fecha, 0, 0);

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
                
                break;
                
            default:
                if(tblPacientes.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de eliminar este Registro?", "Eliminar Paciente", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarPaciente(?)}");
                            procedimiento.setInt(1, ((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente());
                            procedimiento.execute();
                            listaPaciente.remove(tblPacientes.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                        }catch(Exception e){
                        e.printStackTrace();
                        }
                    }
                } else{
                JOptionPane.showMessageDialog(null, "Debe de seleccionar un elemento primero.");
                }
         
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

    public void cargarDatos() {
        tblPacientes.setItems(getPaciente());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Paciente, Integer>("codigoPaciente"));
        colDPI.setCellValueFactory(new PropertyValueFactory<Paciente, String>("DPI"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Paciente, String>("Nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Paciente, String>("Apellidos"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Paciente, String>("direccion"));
        colOcupacion.setCellValueFactory(new PropertyValueFactory<Paciente, String>("ocupacion"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<Paciente, String>("fechaNacimiento"));
        colEdad.setCellValueFactory(new PropertyValueFactory<Paciente, Integer>("Edad"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Paciente, String>("sexo"));
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

    public void seleccionarElemento() {
        txtNombres.setText(((Paciente) tblPacientes.getSelectionModel().getSelectedItem()).getNombres());
        txtApellidos.setText(((Paciente) tblPacientes.getSelectionModel().getSelectedItem()).getApellidos());
        txtDireccion.setText(((Paciente) tblPacientes.getSelectionModel().getSelectedItem()).getDireccion());
        txtOcupacion.setText(((Paciente) tblPacientes.getSelectionModel().getSelectedItem()).getOcupacion());
        fecha.selectedDateProperty().set(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getFechaNacimiento());
        txtDPI.setText(((Paciente) tblPacientes.getSelectionModel().getSelectedItem()).getDPI());
        txtSexo.setText(((Paciente) tblPacientes.getSelectionModel().getSelectedItem()).getSexo());
    }

    public void guardar() {
        Paciente registro = new Paciente();
        registro.setDPI(txtDPI.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setNombres(txtNombres.getText());
        registro.setFechaNacimiento(fecha.getSelectedDate());
        registro.setDireccion(txtDireccion.getText());
        registro.setOcupacion(txtOcupacion.getText());
        registro.setSexo(txtSexo.getText());

        try {
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarPaciente(?,?,?,?,?,?,?)}");
            procedimiento.setString(1, registro.getDPI());
            procedimiento.setString(2, registro.getApellidos());
            procedimiento.setString(3, registro.getNombres());
            procedimiento.setDate(4, new java.sql.Date(registro.getFechaNacimiento().getTime()));
            procedimiento.setString(5, registro.getDireccion());
            procedimiento.setString(6, registro.getOcupacion());
            procedimiento.setString(7, registro.getSexo());
            procedimiento.execute();
            listaPaciente.add(registro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void desactivarControles() {
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtDireccion.setEditable(false);
        txtOcupacion.setEditable(false);
        grpFecha.setDisable(true);
        txtDPI.setEditable(false);
        txtSexo.setEditable(false);
        txtEdad.setEditable(false);
    }

    public void activarControles() {
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtDireccion.setEditable(true);
        txtOcupacion.setEditable(true);
        grpFecha.setDisable(false);
        txtDPI.setEditable(true);
        txtSexo.setEditable(true);
        txtEdad.setEditable(false);
    }

    public void limpiarControles() {
        
       txtNombres.setText("");
        txtApellidos.setText("");
        txtDireccion.setText("");
        txtOcupacion.setText("");
        fecha.selectedDateProperty().set(null);
        txtDPI.setText("");
        txtSexo.setText("");
        txtEdad.setText("");
    }

    public void cancelar() {
        limpiarControles();
        desactivarControles();
        btnNuevo.setText("Nuevo");
        btnEliminar.setText("Eliminar");
        btnEditar.setDisable(false);
        btnReporte.setDisable(false);
        tipoDeOperacion = operaciones.NINGUNO;
    }

     public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblPacientes.getSelectionModel().getSelectedItem() != null){
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
    
         public void buscarPaciente(int codigoPaciente){
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
        }
    }
  
       public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarPaciente(?,?,?,?,?,?,?,?)}");
            Paciente registro = (Paciente)tblPacientes.getSelectionModel().getSelectedItem();
            registro.setDPI(txtDPI.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setNombres(txtNombres.getText());
            registro.setFechaNacimiento(fecha.getSelectedDate());
            registro.setDireccion(txtDireccion.getText());
            registro.setOcupacion(txtOcupacion.getText());
            registro.setSexo(txtSexo.getText());
            procedimiento.setInt(1, registro.getCodigoPaciente());
            procedimiento.setString(2, registro.getDPI());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getNombres());
            procedimiento.setDate(5, new java.sql.Date(registro.getFechaNacimiento().getTime()));
            procedimiento.setString(6, registro.getDireccion());
            procedimiento.setString(7, registro.getOcupacion());
            procedimiento.setString(8, registro.getSexo());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
        
    } 
      public void reporte(){
        switch(tipoDeOperacion){
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnReporte.setText("Editar");
                btnEditar.setText("Reporte");
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
   public void ventanaContactoUrgencia(){
        escenarioPrincipal.ventanaContactoUrgencia();
}

}