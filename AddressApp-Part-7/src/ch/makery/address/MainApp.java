package ch.makery.address;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Dialogs;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ch.makery.address.model.Person;
import ch.makery.address.util.FileUtil;

import com.thoughtworks.xstream.XStream;

/**
 * Start der applikation.
 * Verknüpft Model, View und Controller Klassen.
 *
 */
public class MainApp extends Application {
	
	private Stage primaryStage;
	private BorderPane rootLayout;
	
	/**
	 * The data as an observable list of Persons.
	 */
	private ObservableList<Person> personData = FXCollections.observableArrayList();
	
	/**
	 * Constructor
	 */
	public MainApp() {
		// Add some sample data
		personData.add(new Person("Hans", "Muster"));
		personData.add(new Person("Ruth", "Mueller"));
		personData.add(new Person("Heinz", "Kurz"));
		personData.add(new Person("Cornelia", "Meier"));
		personData.add(new Person("Werner", "Meyer"));
		personData.add(new Person("Lydia", "Kunz"));
		personData.add(new Person("Anna", "Best"));
		personData.add(new Person("Stefan", "Meier"));
		personData.add(new Person("Martin", "Mueller"));
	}
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("AddressApp");
		this.primaryStage.getIcons().add(new Image("file:resources/images/address_book_32.png"));
		
		try {
			// Load the root layout from the fxml file
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			
			// Give the controller access to the main app
			RootLayoutController controller = loader.getController();
			controller.setMainApp(this);
			
			primaryStage.show();
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
		}
		
		showPersonOverview();
		
		// Try to load last opened person file
		File file = getPersonFilePath();
		if (file != null) {
			loadPersonDataFromFile(file);
		}
	}
	
	/**
	 * Returns the data as an observable list of Persons. 
	 * @return
	 */
	public ObservableList<Person> getPersonData() {
		return personData;
	}
	
	/**
	 * Returns the main stage.
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	/**
	 * Shows the person overview page in the center of the root layout.
	 */
	public void showPersonOverview() {
		try {
			// Load the fxml file and set into the center of the main layout
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/PersonOverview.fxml"));
			AnchorPane overviewPage = (AnchorPane) loader.load();
			rootLayout.setCenter(overviewPage);
			
			// Give the controller access to the main app
			PersonOverviewController controller = loader.getController();
			controller.setMainApp(this);
			
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
		}
	}
	
	/**
	 * Opens a dialog to edit details for the specified person. If the user
	 * clicks OK, the changes are saved into the provided person object and
	 * true is returned.
	 * 
	 * @param person the person object to be edited
	 * @return true if the user clicked OK, false otherwise.
	 */
	public boolean showPersonEditDialog(Person person) {
		try {
			// Load the fxml file and create a new stage for the popup
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/PersonEditDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Edit Person");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			// Set the person into the controller
			PersonEditDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setPerson(person);
			
			// Show the dialog and wait until the user closes it
			dialogStage.showAndWait();
			
			return controller.isOkClicked();
			
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Opens a dialog to show birthday statistics. 
	 */
	public void showBirthdayStatistics() {
		try {
			// Load the fxml file and create a new stage for the popup
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/BirthdayStatistics.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Birthday Statistics");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.getIcons().add(new Image("file:resources/images/calendar.png"));
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			
			// Set the persons into the controller
			BirthdayStatisticsController controller = loader.getController();
			controller.setPersonData(personData);
			
			dialogStage.show();
			
		} catch (IOException e) {
			// Exception gets thrown if the fxml file could not be loaded
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads person data from the specified file. The current person data will
	 * be replaced.
	 * 
	 * @param file
	 */
	@SuppressWarnings("unchecked")
	public void loadPersonDataFromFile(File file) {
		XStream xstream = new XStream();
		xstream.alias("person", Person.class);
		
		try {
			String xml = FileUtil.readFile(file);
			
			ArrayList<Person> personList = (ArrayList<Person>) xstream.fromXML(xml);
			
			personData.clear();
			personData.addAll(personList);
			
			setPersonFilePath(file);
		} catch (Exception e) { // catches ANY exception
			Dialogs.showErrorDialog(primaryStage,
					"Could not load data from file:\n" + file.getPath(),
					"Could not load data", "Error", e);
		}
	}
	
	/**
	 * Saves the current person data to the specified file.
	 * 
	 * @param file
	 */
	public void savePersonDataToFile(File file) {
		XStream xstream = new XStream();
		xstream.alias("person", Person.class);

		// Convert ObservableList to a normal ArrayList
		ArrayList<Person> personList = new ArrayList<>(personData);
		
		String xml = xstream.toXML(personList);
		try {
			FileUtil.saveFile(xml, file);
			
			setPersonFilePath(file);
		} catch (Exception e) { // catches ANY exception
			Dialogs.showErrorDialog(primaryStage,
					"Could not save data to file:\n" + file.getPath(),
					"Could not save data", "Error", e);
		}
	}
	
	/**
	 * Returns the person file preference, i.e. the file that was last opened.
	 * The preference is read from the OS specific registry. If no such
	 * preference can be found, null is returned.
	 * 
	 * @return
	 */
	public File getPersonFilePath() {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		String filePath = prefs.get("filePath", null);
		if (filePath != null) {
			return new File(filePath);
		} else {
			return null;
		}
	}
	
	/**
	 * Sets the file path of the currently loaded file.
	 * The path is persisted in the OS specific registry.
	 * 
	 * @param file the file or null to remove the path
	 */
	public void setPersonFilePath(File file) {
		Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
		if (file != null) {
			prefs.put("filePath", file.getPath());
			
			// Update the stage title
			primaryStage.setTitle("AddressApp - " + file.getName());
		} else {
			prefs.remove("filePath");
			
			// Update the stage title
			primaryStage.setTitle("AddressApp");
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
