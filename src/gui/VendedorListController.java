package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbException;
import gui.listner.DataChangeListner;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import modelo.entidades.Vendedor;
import modelo.service.VendedorService;

public class VendedorListController implements Initializable, DataChangeListner {

	private VendedorService service;

	@FXML
	private TableView<Vendedor> tableViewVendedor;
	@FXML
	private TableColumn<Vendedor, Integer> tableColumnId;
	@FXML
	private TableColumn<Vendedor, String> tableColumnNome;
	@FXML
	private TableColumn<Vendedor, String> tableColumnEmail;
	@FXML
	private TableColumn<Vendedor, Date> tableColumnDataNascimento;
	@FXML
	private TableColumn<Vendedor, Double> tableColumnSalario;
//	@FXML
//	private TableColumn<Vendedor, Departamento> tableColumnDepartamento;
	
	@FXML
	private TableColumn<Vendedor, Vendedor> tableColumnEDITAR;
	@FXML
	private TableColumn<Vendedor, Vendedor> tableColumnEXCLUIR;
	@FXML
	private Button btNew;

	private ObservableList<Vendedor> obsList;

	@FXML
	public void onBtNewAction(ActionEvent event) {

		Stage parentStage = Utils.estagioCorrente(event);
		Vendedor obj = new Vendedor();
		createDialogForm(obj, "/gui/VendedorForm.fxml", parentStage);
	}

	public void setVendedorService(VendedorService service) {

		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rsb) {

		initializeNodes();

	}

	private void initializeNodes() {

		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnDataNascimento.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
		Utils.formatTableColumnDate(tableColumnDataNascimento, "dd/MM/yyyy");
		tableColumnSalario.setCellValueFactory(new PropertyValueFactory<>("salario"));
		Utils.formatTableColumnDouble(tableColumnSalario, 2);
		
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewVendedor.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableview() {

		if (service == null) {

			throw new IllegalStateException("Serviço está vazio");
		}

		List<Vendedor> list = service.buscarTodos();
		obsList = FXCollections.observableArrayList(list);
		tableViewVendedor.setItems(obsList);
		initEditButtons();
		initRemoveButtons();

	}

	private void createDialogForm(Vendedor obj, String absoluteName, Stage parentStage) {

//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
//			Pane pane = loader.load();
//
//			VendedorFormControle controller = loader.getController();
//			controller.setVendedor(obj);
//			controller.setVendedorService(new VendedorService());
//			controller.subscribeDataChangeListener(this);
//			controller.updateFormData();
//
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Entre com os dados Vendedor: ");
//			dialogStage.setScene(new Scene(pane));
//			dialogStage.setResizable(false);
//			dialogStage.initOwner(parentStage);
//			dialogStage.initModality(Modality.WINDOW_MODAL);
//			dialogStage.showAndWait();
//
//		} catch (IOException e) {
//
//			Alerts.showAlert("IOException", "Erro ao carregar a Tela", e.getMessage(), AlertType.ERROR);
//		}
	}

	@Override
	public void onDataChange() {
		updateTableview();

	}

	private void initEditButtons() {
		tableColumnEDITAR.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDITAR.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Vendedor obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						evento -> createDialogForm(obj, "/gui/VendedorForm.fxml", Utils.estagioCorrente(evento)));
			}
		});
	}

	private void initRemoveButtons() {
		tableColumnEXCLUIR.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEXCLUIR.setCellFactory(param -> new TableCell<Vendedor, Vendedor>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Vendedor obj, boolean empty) {
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

	private void removeEntity(Vendedor obj) {
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
