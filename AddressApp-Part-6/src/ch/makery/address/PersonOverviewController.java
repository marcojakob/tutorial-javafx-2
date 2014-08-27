package ch.makery.address;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ch.makery.address.model.Person;
import ch.makery.address.util.CalendarUtil;

/**
 * The controller for the overview with address table and details view.
 * 
 * @author Marco Jakob
 */
public class PersonOverviewController {
	@FXML
	private TableView<Person> personTable;
	@FXML
	private TableColumn<Person, String> firstNameColumn;
	@FXML
	private TableColumn<Person, String> lastNameColumn;

	@FXML
	private Label firstNameLabel;
	@FXML
	private Label lastNameLabel;
	@FXML
	private Label streetLabel;
	@FXML
	private Label postalCodeLabel;
	@FXML
	private Label cityLabel;
	@FXML
	private Label birthdayLabel;

	// Reference to the main application
	private MainApp mainApp;

	/**
	 * The constructor. The constructor is called before the initialize()
	 * method.
	 */
	public PersonOverviewController() {
	}

	@FXML
	private void initialize() {
		// Initialize the person table
		firstNameColumn.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
		lastNameColumn.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));
		// Auto resize columns
		personTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// clear person
		showPersonDetails(null);
		
		// Listen for selection changes
		personTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Person>() {

			@Override
			public void changed(ObservableValue<? extends Person> observable,
					Person oldValue, Person newValue) {
				showPersonDetails(newValue);
			}
		});
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		// Add observable list data to the table
		personTable.setItems(mainApp.getPersonData());
	}
	
	/**
	 * Fills all text fields to show details about the person.
	 * If the specified person is null, all text fields are cleared.
	 * 
	 * @param person the person or null
	 */
	private void showPersonDetails(Person person) {
		if (person != null) {
			firstNameLabel.setText(person.getFirstName());
			lastNameLabel.setText(person.getLastName());
			streetLabel.setText(person.getStreet());
			postalCodeLabel.setText(Integer.toString(person.getPostalCode()));
			cityLabel.setText(person.getCity());
			birthdayLabel.setText(CalendarUtil.format(person.getBirthday()));
		} else {
			firstNameLabel.setText("");
			lastNameLabel.setText("");
			streetLabel.setText("");
			postalCodeLabel.setText("");
			cityLabel.setText("");
			birthdayLabel.setText("");
		}
	}
	
	/**
	 * Called when the user clicks on the delete button.
	 */
	@FXML
	private void handleDeletePerson() {
		int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
		if (selectedIndex >= 0) {
			personTable.getItems().remove(selectedIndex);
		} else {
			// Nothing selected
			Dialogs.showWarningDialog(mainApp.getPrimaryStage(),
					"Please select a person in the table.",
					"No Person Selected", "No Selection");
		}
	}
	
	/**
	 * Called when the user clicks the new button.
	 * Opens a dialog to edit details for a new person.
	 */
	@FXML
	private void handleNewPerson() {
		Person tempPerson = new Person();
		boolean okClicked = mainApp.showPersonEditDialog(tempPerson);
		if (okClicked) {
			mainApp.getPersonData().add(tempPerson);
		}
	}
	
	/**
	 * Called when the user clicks the edit button.
	 * Opens a dialog to edit details for the selected person.
	 */
	@FXML
	private void handleEditPerson() {
		Person selectedPerson = personTable.getSelectionModel().getSelectedItem();
		if (selectedPerson != null) {
			boolean okClicked = mainApp.showPersonEditDialog(selectedPerson);
			if (okClicked) {
				refreshPersonTable();
				showPersonDetails(selectedPerson);
			}
			
		} else {
			// Nothing selected
			Dialogs.showWarningDialog(mainApp.getPrimaryStage(),
					"Please select a person in the table.",
					"No Person Selected", "No Selection");
		}
	}
	
	/**
	 * Refreshes the table. This is only necessary if an item that is already in
	 * the table is changed. New and deleted items are refreshed automatically.
	 * 
	 * This is a workaround because otherwise we would need to use property
	 * bindings in the model class and add a *property() method for each
	 * property. Maybe this will not be necessary in future versions of JavaFX
	 * (see http://javafx-jira.kenai.com/browse/RT-22599)
	 */
	private void refreshPersonTable() {
		int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
		personTable.setItems(null);
		personTable.layout();
		personTable.setItems(mainApp.getPersonData());
		// Must set the selected index again (see http://javafx-jira.kenai.com/browse/RT-26291)
		personTable.getSelectionModel().select(selectedIndex);
	}
}