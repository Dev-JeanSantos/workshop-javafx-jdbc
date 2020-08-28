package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbExcecaoIntegridade;
import db.DbException;
import gui.listner.DataChangeListner;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.entidades.Departamento;
import modelo.service.DepartamentoService;

public class DepartamentoListController implements Initializable, DataChangeListner {

	private DepartamentoService service;

	@FXML
	private TableView<Departamento> tableViewDepartamento;
	@FXML
	private TableColumn<Departamento, Integer> tableColumnId;
	@FXML
	private TableColumn<Departamento, String> tableColumnNome;
	@FXML
	private TableColumn<Departamento, Departamento> tableColumnEDITAR;
	@FXML
	private TableColumn<Departamento, Departamento> tableColumnEXCLUIR;
	@FXML
	private Button btNew;

	private ObservableList<Departamento> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {

		Stage parentStage = Utils.estagioCorrente(event);
		Departamento obj = new Departamento();
		createDialogForm(obj, "/gui/DepartamentoForm.fxml", parentStage);
	}

	public void setDepartamentoService(DepartamentoService service) {

		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rsb) {

		initializeNodes();

	}

	private void initializeNodes() {

		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewDepartamento.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableview() {

		if (service == null) {

			throw new IllegalStateException("Serviço está vazio");
		}

		List<Departamento> list = service.buscarTodos();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartamento.setItems(obsList);
		initEditButtons();
		initRemoveButtons();

	}

	private void createDialogForm(Departamento obj, String absoluteName, Stage parentStage) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			Pane pane = loader.load();

			DepartamentoFormControle controller = loader.getController();
			controller.setDepartamento(obj);
			controller.setDepartamentoService(new DepartamentoService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Entre com os dados Departamento: ");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {

			Alerts.showAlert("IOException", "Erro ao carregar a Tela", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void onDataChange() {
		updateTableview();

	}

	private void initEditButtons() {
		tableColumnEDITAR.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDITAR.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Departamento obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						evento -> createDialogForm(obj, "/gui/DepartamentoForm.fxml", Utils.estagioCorrente(evento)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnEXCLUIR.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEXCLUIR.setCellFactory(param -> new TableCell<Departamento, Departamento>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Departamento obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Departamento obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmação", "Tem certeza dessa exclusão?");
		
		if (result.get() == ButtonType.OK) {
			
			if (service == null) {
				throw new IllegalStateException("Serviço está vazio");
			}
			try {
				service.remover(obj);
				updateTableview();
			}
			catch (DbException e){
				Alerts.showAlert("Erro ao remover o objeto",null, e.getMessage(), AlertType.ERROR);
				
			}
			
			
		}
	}

}
