package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listner.DataChangeListner;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.exceptios.ValidacaoDeExcecao;
import modelo.entidades.Vendedor;
import modelo.service.VendedorService;

public class VendedorFormControle implements Initializable{
	
	private Vendedor entidade;
	private VendedorService service;
	private List<DataChangeListner> dataChangeListners = new ArrayList<DataChangeListner>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private TextField txtEmail;
	
	@FXML
	private DatePicker dpDataNascimento;
	
	@FXML
	private TextField txtSalario;
	
	@FXML
	private Label labelErrorNome;
	
	@FXML
	private Label labelErrorEmail;
	
	@FXML
	private Label labelErrorDataNascimento;
	
	@FXML
	private Label labelErrorSalario;
	
	@FXML
	private Button btSalvar;
	
	@FXML
	private Button btCancelar;

	@FXML
	public  void onBtSalvar(ActionEvent evento) {
		
		if(entidade == null) {
			
			throw new IllegalStateException("A Entidade está vazia");
		}
		if (service == null){
			
			throw new IllegalStateException("O Serviço é nullo");
		}
		
		try {
		
			entidade = getFormData();
			service.salvarOuAtualizar(entidade);
			notifyDataChangeListner();
			Utils.estagioCorrente(evento).close();
		}
		catch (ValidacaoDeExcecao e) {
			
			setMensagemDeErros(e.getErros());
		}
		catch (DbException e) {
			
			Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), AlertType.ERROR);
			// TODO: handle exception
		}
			
	}
	
	private void notifyDataChangeListner() {
		
		for (DataChangeListner listner: dataChangeListners) {
			listner.onDataChange();
		}
	}

	private Vendedor getFormData() {
		
		ValidacaoDeExcecao validacaoErro = new ValidacaoDeExcecao("Erro de Validação");
		
		Vendedor obj = new Vendedor();
		obj.setId(gui.util.Utils.tryParseToInt( txtId.getText()));
		
		if(txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			
			validacaoErro.addErros("nome", "Campo não dever ser vazio");
		}
		obj.setNome(txtNome.getText());
		
		if (validacaoErro.getErros().size() >0) {
			throw validacaoErro;
		}
		
		return obj;
	}

	public  void onBtcancelar(ActionEvent evento) {
		Utils.estagioCorrente(evento).close();
	}
	
	public void setVendedor(Vendedor entidade) {
		
		this.entidade = entidade;	
		
	}
	
	public void setVendedorService(VendedorService service) {
		
		this.service = service;
		
	}
	
	public void subscribeDataChangeListener(DataChangeListner listner) {
		
		dataChangeListners.add(listner);
		
	}
	
	public void updateFormData() {
	
		if(entidade == null) {
			 throw new IllegalStateException("Entidade nulla");
		}
		
		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
		txtEmail.setText(entidade.getEmail());
		Locale.setDefault(Locale.US);
		txtSalario.setText(String.format("%.2f", entidade.getSalario()));
		if(entidade.getDataNascimento() != null) {
			
			dpDataNascimento.setValue(LocalDate.ofInstant(entidade.getDataNascimento().toInstant(), ZoneId.systemDefault() ) ); 
		}
		
	
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rsb) {
				
		initializeNodes();
	}
	
	private void initializeNodes() {
		
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 60);
		Constraints.setTextFieldDouble(txtSalario);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpDataNascimento, "dd/MM/yyyy");
	}
	
	private void setMensagemDeErros(Map<String , String> erros) {
		
		Set<String> campos = erros.keySet();
		if (campos.contains("nome")) {
			labelErrorNome.setText(erros.get("nome"));
			
		}
		
	}
	
	
}
