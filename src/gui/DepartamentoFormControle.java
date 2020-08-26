package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Constraints;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import modelo.entidades.Departamento;

public class DepartamentoFormControle implements Initializable{
	
	private Departamento entidade;
	
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
	public  void onBtSalvar() {
		System.out.println("Botao está salvando");
	}
	
	public  void onBtcancelar() {
		System.out.println("Botao está cancelando");
	}
	
	public void setDepartamento(Departamento entidade) {
		
		this.entidade = entidade;	
		
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
