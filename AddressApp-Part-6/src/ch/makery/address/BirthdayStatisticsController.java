package ch.makery.address;

import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import ch.makery.address.model.Person;

/**
 * The controller for the birthday statistics view.
 * 
 * @author Marco Jakob
 */
public class BirthdayStatisticsController {
	
	@FXML
	private BarChart<String, Integer> barChart;
	
	@FXML
	private CategoryAxis xAxis;
	
	private ObservableList<String> monthNames = FXCollections.observableArrayList();
	
	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
		// Get an array with the English month names.
		String[] months = DateFormatSymbols.getInstance(Locale.ENGLISH).getMonths();
		// Convert it to a list and add it to our ObservableList of months.
		monthNames.addAll(Arrays.asList(months));
		
		xAxis.setCategories(monthNames);
	}
	
	/**
	 * Sets the persons to show the statistics for.
	 * 
	 * @param persons
	 */
	public void setPersonData(List<Person> persons) {
		int[] monthCounter = new int[12];
		for (Person p : persons) {
			int month = p.getBirthday().get(Calendar.MONTH);
			monthCounter[month]++;
		}
		
		XYChart.Series<String, Integer> series = createMonthDataSeries(monthCounter);
        barChart.getData().add(series);
	}
	
	/**
	 * Creates a XYChart.Data object for each month. All month data is then
	 * returned as a series.
	 * 
	 * @param monthCounter Array with a number for each month. Must be of length 12!
	 * @return
	 */
	private XYChart.Series<String, Integer> createMonthDataSeries(int[] monthCounter) {
		XYChart.Series<String,Integer> series = new XYChart.Series<String,Integer>();
		
		for (int i = 0; i < monthCounter.length; i++) {
			XYChart.Data<String, Integer> monthData = new XYChart.Data<String,Integer>(monthNames.get(i), monthCounter[i]);
			series.getData().add(monthData);
		}
		
		return series;
	}
}
