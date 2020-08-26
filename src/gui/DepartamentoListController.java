package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import modelo.entidades.Departamento;
import modelo.service.DepartamentoService;

public class DepartamentoListController implements Initializable{
	
	private DepartamentoService service;
	
	
	@FXML
	private TableView<Departamento> tableViewDepartamento;
	@FXML
	private TableColumn<Departamento, Integer> tableColumnId;
	@FXML
	private TableColumn<Departamento, String> tableColumnNome;
	@FXML
	private Button btNew;
	
	private ObservableList<Departamento> obsList;
	
	@FXML
	public void onBtNewAction() {
		
		System.out.println("onBtNewAction");
	}
	
	
	public void setDepartamentoService (DepartamentoService service) {		
		
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
		
		List<Departamento>list = service.buscarTodos();
		obsList = FXCollections.observableArrayList(list);
		tableViewDepartamento.setItems(obsList);
	}

}
