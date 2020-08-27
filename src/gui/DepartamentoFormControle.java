package gui;

import java.net.URL;
import java.util.ResourceBundle;


import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import modelo.entidades.Departamento;
import modelo.service.DepartamentoService;

public class DepartamentoFormControle implements Initializable{
	
	private Departamento entidade;
	private DepartamentoService service;
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private Label labelErrorNome;
	
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
			Utils.estagioCorrente(evento).close();
		}
		catch (DbException e) {
			
			Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), AlertType.ERROR);
			// TODO: handle exception
		}
			
		
	}
	
	private Departamento getFormData() {
		
		Departamento obj = new Departamento();
		obj.setId(gui.util.Utils.tryParseToInt( txtId.getText()));
		obj.setNome(txtNome.getText());
		
		return obj;
	}

	public  void onBtcancelar(ActionEvent evento) {
		Utils.estagioCorrente(evento).close();
	}
	
	public void setDepartamento(Departamento entidade) {
		
		this.entidade = entidade;	
		
	}
	
	public void setDepartamentoService(DepartamentoService service) {
		
		this.service = service;
		
	}
	
	public void updateFormData() {
	
		if(entidade == null) {
			 throw new IllegalStateException("Entidade nulla");
		}
		
		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
	
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle rsb) {
				
		initializeNodes();
	}
	
	private void initializeNodes() {
		
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtNome, 30);
		
	}
	
	
}
