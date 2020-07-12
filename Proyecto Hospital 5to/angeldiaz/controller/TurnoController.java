
package org.angeldiaz.controller;


import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.angeldiaz.bean.MedicoEspecialidad;
import org.angeldiaz.bean.Paciente;
import org.angeldiaz.bean.ResponsableTurno;
import org.angeldiaz.bean.Turno;
import org.angeldiaz.db.Conexion;
import org.angeldiaz.sistema.Principal;


public class TurnoController implements Initializable{
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO};
    private Principal escenarioPrincipal; 
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Turno> listaTurno;
    private ObservableList<ResponsableTurno> listaResponsableTurno;
    private ObservableList<Paciente> listaPaciente;
    private ObservableList<MedicoEspecialidad> listaMedicoEspecialidad;
    private DatePicker fecha1;
    private DatePicker fecha2;
    @FXML private GridPane grpFecha1;
    @FXML private GridPane grpFecha2;
    @FXML private TextField txtValorCita;
    @FXML private ComboBox cmbCodigoEspecialidad;
    @FXML private ComboBox cmbCodigoResponsable;
    @FXML private ComboBox cmbCodigoPaciente;
    @FXML private TableView tblTurnos;
    @FXML private TableColumn colCodigo;
    @FXML private TableColumn colFechaTurno;
    @FXML private TableColumn colFechaCita;
    @FXML private TableColumn colValorCita;
    @FXML private TableColumn colCodigoMedico;
    @FXML private TableColumn colCodigoTurno;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fecha1 = new DatePicker(Locale.ENGLISH);
        fecha1.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha1.getCalendarView().todayButtonTextProperty().set("Today");
        fecha1.getCalendarView().setShowWeeks(false);
        fecha1.getStylesheets().add("/org/angeldiaz/resource/DatePicker.css");
        grpFecha1.add(fecha1, 0, 0);
        
        fecha2 = new DatePicker(Locale.ENGLISH);
        fecha2.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha2.getCalendarView().todayButtonTextProperty().set("Today");
        fecha2.getCalendarView().setShowWeeks(false);
        fecha2.getStylesheets().add("/org/angeldiaz/resource/DatePicker.css");
        grpFecha2.add(fecha2, 0 , 0);
        
        cmbCodigoEspecialidad.setItems(getMedicoEspecialidad());
        cmbCodigoResponsable.setItems(getResponsableTurno());
        cmbCodigoPaciente.setItems(getPacientes());
    }
    
    public void cargarDatos(){
        tblTurnos.setItems(getTurno());
        colCodigo.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoTurno"));
        colFechaTurno.setCellValueFactory(new PropertyValueFactory<Turno, Date>("fechaTurno"));
        colFechaCita.setCellValueFactory(new PropertyValueFactory<Turno, Date>("fechaCita"));
        colValorCita.setCellValueFactory(new PropertyValueFactory<Turno, Double>("ValorCita"));
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoMedicoEspecialidad"));
        colCodigoTurno.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("CodigoTurnoResponsable"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoPaciente"));
            }
    
    public ObservableList<Turno> getTurno(){
        ArrayList<Turno> lista = new ArrayList<Turno>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarTurno}" );
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Turno(resultado.getInt("codigoTurno"),
                        resultado.getDate("fechaTurno"),
                        resultado.getDate("fechaCita"),
                        resultado.getDouble("valorCita"),
                        resultado.getInt("codigoMedicoEspecialidad"),
                        resultado.getInt("CodigoResponsableTurno"),
                        resultado.getInt("codigoPaciente")));         
            }
        }catch(Exception e){
            e.printStackTrace();
        }return listaTurno = FXCollections.observableList(lista);
    }
    
    public ObservableList<ResponsableTurno> getResponsableTurno(){
        ArrayList<ResponsableTurno> lista = new ArrayList<ResponsableTurno>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarResponsableTurno}" );
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
        }return listaResponsableTurno = FXCollections.observableList(lista);
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
                                resultado.getString("direccion"),
                                resultado.getString("ocupacion"),
                                resultado.getString("sexo")));                           
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaPaciente=FXCollections.observableList(lista);
        
    }
    
    public ObservableList<MedicoEspecialidad> getMedicoEspecialidad(){
        ArrayList<MedicoEspecialidad> lista = new ArrayList<MedicoEspecialidad>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedico_Especialidad}" );
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new MedicoEspecialidad(resultado.getInt("codigoMedicoEspecialidad"),
                        resultado.getInt("codigoMedico"),
                        resultado.getInt("codigoEspecialidad"),
                        resultado.getInt("codigoHorario")));         
            }
        }catch(Exception e){
            e.printStackTrace();
        }return listaMedicoEspecialidad = FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento() {
        if (tblTurnos.getSelectionModel().getSelectedItem() != null){       
        fecha1.selectedDateProperty().set(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getFechaTurno());
        fecha2.selectedDateProperty().set(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getFechaCita());
        txtValorCita.setText(String.valueOf(((Turno) tblTurnos.getSelectionModel().getSelectedItem()).getValorCita()));
        cmbCodigoEspecialidad.getSelectionModel().select(buscarMedicoEspecialidad(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad()));
        cmbCodigoResponsable.getSelectionModel().select(buscarResponsable(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoTurnoResponsable()));
        cmbCodigoPaciente.getSelectionModel().select(buscarPaciente(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
        cmbCodigoEspecialidad.setDisable(true);
        cmbCodigoResponsable.setDisable(true);
        cmbCodigoPaciente.setDisable(true);
        grpFecha1.setDisable(true);
        grpFecha2.setDisable(true);
        
    }
    }  
    
    
    public Turno buscarTurno(int codigoTurno){
        Turno resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarTurno(?)}");
            procedimiento.setInt(1, codigoTurno);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Turno ( registro.getInt("codigoTurno"),
                                        registro.getDate("fechaTurno"),
                                        registro.getDate("fechaCita"),
                                        registro.getDouble("valorCita"),
                                        registro.getInt("codgioMedicoEspecialidad"),
                                        registro.getInt("codigoResponsable"),
                                        registro.getInt("codigoPaciente"));
                                        
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public ResponsableTurno buscarResponsable(int codigoResponsableTurno){
        ResponsableTurno resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarResponsableTurno(?)}");
            procedimiento.setInt(1, codigoResponsableTurno);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new ResponsableTurno ( registro.getInt("codigoResponsableTurno"),
                                        registro.getString("nombreResponsable"),
                                        registro.getString("apellidosResponsable"),
                                        registro.getString("telefonoPersonal"),
                                        registro.getInt("codigoArea"),
                                        registro.getInt("codigoCargo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public Paciente buscarPaciente(int codigoPaciente){
        Paciente resultado = null;
        try{
            PreparedStatement procedimiento= Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarPaciente(?)}");
            procedimiento.setInt(1, codigoPaciente);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Paciente (registro.getInt("codigoPaciente"),
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
        return resultado;
    }
    
    public MedicoEspecialidad buscarMedicoEspecialidad (int codigoMedicoEspecialidad){
        MedicoEspecialidad resultado = null;
                try{
                    PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedicoEspecialidad(?)}");
                    procedimiento.setInt(1, codigoMedicoEspecialidad);
                    ResultSet registro = procedimiento.executeQuery();
                    while (registro.next()){
                       resultado = new MedicoEspecialidad (registro.getInt("codigoMedicoEspecialidad") ,
                                                           registro.getInt("codigoMedico"),
                                                           registro.getInt("codigoEspecialidad"),
                                                           registro.getInt("codigoHorario"));
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                return resultado;
    }
    
    
     public void eliminar() {
        switch (tipoDeOperacion) {
            case GUARDAR:
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tblTurnos.getSelectionModel().select (null);
                tipoDeOperacion = operaciones.NINGUNO;
                tblTurnos.setDisable(false);
                break;
            default:
                if (tblTurnos.getSelectionModel().getSelectedItem() != null) {
                    int respuesta = JOptionPane.showConfirmDialog(null, "¿Está seguro de eliminar el registro?", "Eliminar ContactoUrgencia",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarTurno(?)}");
                            procedimiento.setInt(1, ((Turno) tblTurnos.getSelectionModel().getSelectedItem()).getCodigoTurno());
                            procedimiento.execute();
                            listaResponsableTurno.remove(tblTurnos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            cargarDatos();
                            cmbCodigoEspecialidad.setDisable(true);
                            cmbCodigoResponsable.setDisable(true);
                            cmbCodigoPaciente.setDisable(true);
                            tblTurnos.getSelectionModel().select (null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        limpiarControles();
                        desactivarControles();
                        cmbCodigoEspecialidad.setDisable(true);
                        cmbCodigoResponsable.setDisable(true);
                        cmbCodigoPaciente.setDisable(true);
                        tblTurnos.getSelectionModel().select (null);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }
        }
    }
    
    public void nuevo(){
        switch (tipoDeOperacion){
            case NINGUNO:
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                cmbCodigoEspecialidad.setDisable(false);
                cmbCodigoResponsable.setDisable(false);
                cmbCodigoPaciente.setDisable(false);
                limpiarControles();
                tblTurnos.getSelectionModel().select(null);
                tipoDeOperacion = operaciones.GUARDAR;
                tblTurnos.setDisable(true);
                break;
            case GUARDAR:
                if(cmbCodigoEspecialidad.getSelectionModel().getSelectedItem() == null || 
                        cmbCodigoResponsable.getSelectionModel().getSelectedItem() == null || 
                        cmbCodigoPaciente.getSelectionModel().getSelectedItem() == null || 
                        txtValorCita.getText().equals("") || fecha1.selectedDateProperty().get()==null || fecha2.selectedDateProperty().get() == null){
                    JOptionPane.showMessageDialog(null,"Deve llenar todos los campos");
                }else{
                guardar();
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                cmbCodigoEspecialidad.setDisable(true);
                cmbCodigoResponsable.setDisable(true);
                cmbCodigoPaciente.setDisable(true);
                tipoDeOperacion = operaciones.NINGUNO;
                tblTurnos.setDisable(false);
                cargarDatos();
                break;
        }
        }
    }
    
    
   public void guardar(){
        
        Turno registro = new Turno();
        registro.setFechaTurno(fecha1.getSelectedDate());
        registro.setFechaCita(fecha2.getSelectedDate());
        registro.setValorCita(Double.valueOf(txtValorCita.getText()));
        registro.setCodigoTurnoResponsable(((ResponsableTurno)cmbCodigoResponsable.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
        registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        registro.setCodigoMedicoEspecialidad(((MedicoEspecialidad)cmbCodigoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarTurno(?,?,?,?,?,?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFechaTurno().getTime()));
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaCita().getTime()));
            procedimiento.setDouble(3, registro.getValorCita());
            procedimiento.setInt(4, registro.getCodigoMedicoEspecialidad());
            procedimiento.setInt(5, registro.getCodigoTurnoResponsable());
            procedimiento.setInt(6, registro.getCodigoPaciente());
            
            procedimiento.execute();
            listaTurno.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        } 
    }
   
    public void editar() {
        switch (tipoDeOperacion) {
            case NINGUNO:
                if (tblTurnos.getSelectionModel().getSelectedItem() != null) {
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    cmbCodigoEspecialidad.setDisable(true);
                    cmbCodigoResponsable.setDisable(true);
                    cmbCodigoPaciente.setDisable(true);
                    tblTurnos.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                } else {
                    JOptionPane.showMessageDialog(null, "Debe seleccionar un elemento.");
                }
                break;
            case ACTUALIZAR:
                actualizar();
                desactivarControles();
                limpiarControles();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                cmbCodigoEspecialidad.setDisable(true);
                cmbCodigoResponsable.setDisable(true);
                cmbCodigoPaciente.setDisable(true);
                tblTurnos.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }
    
    public void Reporte (){
        switch (tipoDeOperacion) {
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                cmbCodigoEspecialidad.setDisable(true);
                cmbCodigoResponsable.setDisable(true);
                cmbCodigoPaciente.setDisable(true);
                tblTurnos.setDisable(false);
                tblTurnos.getSelectionModel().select (null);
                tipoDeOperacion = operaciones.NINGUNO;
                break;
    }
    }
    
    
    public void actualizar() {
        try {
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EditarTurno(?,?,?,?,?,?,?)}");
            Turno registro = (Turno) tblTurnos.getSelectionModel().getSelectedItem();
            registro.setFechaTurno(fecha1.getSelectedDate());
            registro.setFechaCita(fecha2.getSelectedDate());
            registro.setValorCita(Double.valueOf(txtValorCita.getText()));
            registro.setCodigoTurnoResponsable(((ResponsableTurno)cmbCodigoResponsable.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
            registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
            registro.setCodigoMedicoEspecialidad(((MedicoEspecialidad)cmbCodigoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
            procedimiento.setInt(1, registro.getCodigoTurno());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaTurno().getTime()));
            procedimiento.setDate(3, new java.sql.Date(registro.getFechaCita().getTime()));
            procedimiento.setDouble(4, registro.getValorCita());
            procedimiento.setInt(5, registro.getCodigoTurnoResponsable());
            procedimiento.setInt(6, registro.getCodigoPaciente());
            procedimiento.setInt(7, registro.getCodigoMedicoEspecialidad());
            procedimiento.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void validarNumeros(KeyEvent evento){
        char e = evento.getCharacter().charAt(0);
        
        if(!Character.isDigit(e))
            evento.consume();
    }
    
    
    public void validarLetras(KeyEvent evento){
        char e = evento.getCharacter().charAt(0);
        
        if(!Character.isLetter(e)){
            if(e != ' ')
                evento.consume();
        }
    }
   
    
    public void desactivarControles(){
        grpFecha1.setDisable(true);
        grpFecha2.setDisable(true);
        txtValorCita.setEditable(false);
        cmbCodigoEspecialidad.setDisable(true);
        cmbCodigoResponsable.setDisable(true);
        cmbCodigoPaciente.setDisable(true);
    }
    
    public void activarControles(){
        grpFecha1.setDisable(false);
        grpFecha2.setDisable(false);
        txtValorCita.setEditable(true);
        cmbCodigoEspecialidad.setDisable(false);
        cmbCodigoResponsable.setDisable(false);
        cmbCodigoPaciente.setDisable(false);
    }
    
    public void limpiarControles(){
       
        txtValorCita.setText("");
        cmbCodigoEspecialidad.getSelectionModel().select(null);
        cmbCodigoResponsable.getSelectionModel().select(null);
        cmbCodigoPaciente.getSelectionModel().select(null);
        fecha1.selectedDateProperty().set(null);
        fecha2.selectedDateProperty().set(null);
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