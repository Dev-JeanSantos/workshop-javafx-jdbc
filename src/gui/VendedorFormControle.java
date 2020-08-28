package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.exceptios.ValidacaoDeExcecao;
import modelo.entidades.Departamento;
import modelo.entidades.Vendedor;
import modelo.service.DepartamentoService;
import modelo.service.VendedorService;

public class VendedorFormControle implements Initializable {

	private Vendedor entidade;
	private VendedorService service;
	private DepartamentoService servideDep;
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
	private ComboBox<Departamento> comboBoxDepartamento;

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

	private ObservableList<Departamento> obsList;

	@FXML
	public void onBtSalvar(ActionEvent evento) {

		if (entidade == null) {

			throw new IllegalStateException("A Entidade está vazia");
		}
		if (service == null) {

			throw new IllegalStateException("O Serviço é nullo");
		}

		try {

			entidade = getFormData();
			service.salvarOuAtualizar(entidade);
			notifyDataChangeListner();
			Utils.estagioCorrente(evento).close();
		} catch (ValidacaoDeExcecao e) {

			setMensagemDeErros(e.getErros());
		} catch (DbException e) {
			
			
			Alerts.showAlert("Erro ao salvar objeto", null, e.getMessage(), AlertType.ERROR);
			// TODO: handle exception
		}

	}

	private void notifyDataChangeListner() {

		for (DataChangeListner listner : dataChangeListners) {
			listner.onDataChange();
		}
	}

	private Vendedor getFormData() {

		ValidacaoDeExcecao validacaoErro = new ValidacaoDeExcecao("Erro de Validação");

		Vendedor obj = new Vendedor();
		obj.setId(gui.util.Utils.tryParseToInt(txtId.getText()));

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {

			validacaoErro.addErros("nome", "Campo não dever ser vazio");
		}
		obj.setNome(txtNome.getText());
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {

			validacaoErro.addErros("email", "Campo não dever ser vazio");
		}
		obj.setEmail(txtEmail.getText());
		
		if(dpDataNascimento.getValue() == null) {
			
			validacaoErro.addErros("dataNascimento", "Campo não dever ser vazio");
		}
		else {
			
			Instant instant = Instant.from(dpDataNascimento.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setDataNascimento(Date.from(instant));
			
		}
		if (txtSalario.getText() == null || txtSalario.getText().trim().equals("")) {

			validacaoErro.addErros("salario", "Campo não dever ser vazio");
		}
		obj.setSalario(Utils.tryParseToDouble(txtSalario.getText()));

		if (validacaoErro.getErros().size() > 0) {
			throw validacaoErro;
		}
		
		obj.setDepartamento(comboBoxDepartamento.getValue());
		return obj;
	}

	public void onBtcancelar(ActionEvent evento) {
		Utils.estagioCorrente(evento).close();
	}

	public void setVendedor(Vendedor entidade) {

		this.entidade = entidade;

	}

	public void setServices(VendedorService service, DepartamentoService serviceDep) {

		this.service = service;
		this.servideDep = serviceDep;

	}

	public void subscribeDataChangeListener(DataChangeListner listner) {

		dataChangeListners.add(listner);

	}

	public void updateFormData() {

		if (entidade == null) {
			throw new IllegalStateException("Entidade nulla");
		}

		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
		txtEmail.setText(entidade.getEmail());
		Locale.setDefault(Locale.US);
		txtSalario.setText(String.format("%.2f", entidade.getSalario()));
		if (entidade.getDataNascimento() != null) {

			dpDataNascimento
					.setValue(LocalDate.ofInstant(entidade.getDataNascimento().toInstant(), ZoneId.systemDefault()));
		}
		
		if(entidade.getDepartamento() == null) {
			
			comboBoxDepartamento.getSelectionModel().selectFirst();
		}
		else {
		comboBoxDepartamento.setValue(entidade.getDepartamento());
		}
	}

	public void carregarObjetosAssociados() {

		if (servideDep == null) {
			throw new IllegalStateException("Serviço do Departamento não pode ser nulo");
		}
		List<Departamento> list = servideDep.buscarTodos();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartamento.setItems(obsList);
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
		initializeComboBoxDepartment();
	}

	private void setMensagemDeErros(Map<String, String> erros) {

		Set<String> campos = erros.keySet();
		if (campos.contains("nome")) {
			labelErrorNome.setText(erros.get("nome"));
		}else {
			labelErrorNome.setText(erros.get(""));
		}
		
		if (campos.contains("email")) {
			labelErrorEmail.setText(erros.get("email"));
		}else {
			labelErrorEmail.setText(erros.get(""));
		}
		
			
		if (campos.contains("salario")) {
			labelErrorSalario.setText(erros.get("salario"));
		}else {
			labelErrorSalario.setText(erros.get(""));
		}

		if (campos.contains("dataNascimento")) {
			labelErrorDataNascimento.setText(erros.get("dataNascimento"));
		}else {
			labelErrorDataNascimento.setText(erros.get(""));
		}

	}

	private void initializeComboBoxDepartment() {
		Callback<ListView<Departamento>, ListCell<Departamento>> factory = lv -> new ListCell<Departamento>() {
			@Override
			protected void updateItem(Departamento item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		comboBoxDepartamento.setCellFactory(factory);
		comboBoxDepartamento.setButtonCell(factory.call(null));
	}

}
