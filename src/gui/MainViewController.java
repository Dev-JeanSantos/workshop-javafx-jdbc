package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import modelo.service.DepartamentoService;
import modelo.service.VendedorService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemVendedor;
	@FXML
	private MenuItem menuItemDepartamento;
	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemVendedorAction() {

		loadView("/gui/Vendedor.fxml", 
				(VendedorListController controller) -> {controller.setVendedorService
				(new VendedorService()); controller.updateTableview();});
		

	}

	@FXML
	public void onMenuItemDepartamentoAction() {
		
		loadView("/gui/Departamento.fxml", 
				(DepartamentoListController controller) -> {controller.setDepartamentoService
				(new DepartamentoService()); controller.updateTableview();});
		

	}

	@FXML
	public void onMenuItemAboutAction() {

		loadView("/gui/About.fxml", x -> {});

	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

	}

	private synchronized <T> void loadView(String absoluteName, Consumer<T> iniciarAcao) {

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			VBox newVbox = loader.load();

			Scene mainScene = Main.getMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVbox.getChildren());
			
			T controller = loader.getController();
			iniciarAcao.accept(controller);

		} catch (IOException e) {

			Alerts.showAlert("IOException", "Error loading view", e.getMessage(), AlertType.ERROR);

		}
	}

}
