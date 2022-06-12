package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import Csv.CsvIo;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;



import application.Todo.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Controller {

	//Buttons from Scene Builder to Code
	@FXML
	Button doneBtn;
	@FXML
	Button delBtn;
	@FXML
	Button saveBtn;
	@FXML
	Button newTodoBtn;
	@FXML
	Button resetBtn;
	//MenuItems to Code
	@FXML
	MenuItem saveToMenuItem;
	@FXML
	MenuItem saveNormalMenuItem;
	@FXML
	MenuItem openMenuItem;
	@FXML
	MenuItem emptyListMenuItem;
	@FXML 
	MenuItem clearChanges;
	@FXML
	MenuItem allCateg;
	@FXML
	MenuItem doneCateg;
	
	@FXML
	Menu categoryMenu;
	//ListView
	@FXML
	ListView<Todo> todoList;
	//TextArea
	@FXML
	TextArea todoTxt;
	//TextField
	@FXML
	TextField categoryTxt;
	@FXML
	TextField headerTxt;
	
	//ObservableList
	private ObservableList<Todo> todoObsList;
	
	
	private HashMap<String, ArrayList<Todo>> todoCategoryFilter;
	

	
	private Todo selectedItem;
	private int selectedItemidx;
	
	private String selectedCategory;
	
	public Controller() {
		this.todoCategoryFilter = new HashMap<String, ArrayList<Todo>>();
		this.todoObsList= FXCollections.observableArrayList();
	}
	
	@FXML
	public void initialize() {
		ArrayList<Todo> loadedTodos = CsvIo.LoadCSV();
		todoObsList.addAll(loadedTodos);
		todoList.setItems(todoObsList); //ListView is null when in construction stage, but not when initializing FXML file
		setListeners(); //setting listeners after initialization of FXML and not after construction of class
		this.todoCategoryFilter.put(allCateg.getText().toUpperCase(), new ArrayList<Todo>());
		this.todoCategoryFilter.put(doneCateg.getText().toUpperCase(), new ArrayList<Todo>());
		this.selectedCategory = allCateg.getText().toUpperCase();
	}
	
	//ActionEvents from FXML / Scene builder
	public void newTodo(ActionEvent e) {
		if (!checkInputFields()) {
			createAlert("Bad input", "One or more input fields are empty. Please fill all of them out"); 
			return;
		}
		addToList(new Todo(this.headerTxt.getText(), this.categoryTxt.getText(), this.todoTxt.getText()));
		resetTextFields();
		
	}
	
	
	public void saveToExisting(ActionEvent e) {
		if (!checkInputFields()) {
			createAlert("Bad input", "One or more input fields are empty. Please fill all of them out"); 
			return;
		}
		updateTodo(selectedItem);
		resetTextFields();
	}
	
	public void todoDone(ActionEvent e) {
		selectedItem.setStatus(Status.getStatusFromCode(1));
		System.out.println(selectedItem.getStatus().ordinal());
	}
	
	public void deleteTodo(ActionEvent e) {
		Todo itemToDelete = this.todoObsList.get(selectedItemidx);

		if (selectedCategory.equals(allCateg.getText().toUpperCase())) {
			this.todoCategoryFilter.get(allCateg.getText().toUpperCase()).remove(itemToDelete);
			this.todoCategoryFilter.get(itemToDelete.getCategory().toUpperCase()).remove(itemToDelete);
		}
		else if (selectedCategory.equals(doneCateg.getText().toUpperCase())) {
			this.todoCategoryFilter.get(selectedCategory).remove(itemToDelete);
		}
		else {
			this.todoCategoryFilter.get(selectedCategory).remove(itemToDelete);
			this.todoCategoryFilter.get(allCateg.getText().toUpperCase()).remove(itemToDelete);

		}
		
		setObsListContent(this.todoCategoryFilter.get(selectedCategory));	
		resetInput(e);
		CsvIo.writeCsv(this.todoObsList);
	}
	
	public void resetInput(ActionEvent e) {
		resetTextFields();
	}
	
	public void resetAll(ActionEvent e) {
		
	}
	
	public void getClickedMenuItem(ActionEvent e) {
		this.selectedCategory = ((MenuItem)e.getTarget()).getText().toUpperCase();
		setObsListContent(this.todoCategoryFilter.get(selectedCategory));
	}
	
	public void displayAllTodos(ActionEvent e) {
		setObsListContent(this.todoCategoryFilter.get(allCateg.getText().toUpperCase()));

	}
	public void saveAs(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Wähle Todo Speicherort");
		
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

		fileChooser.getExtensionFilters().addAll(
			    new FileChooser.ExtensionFilter("TodoList", "*.csv")
		);
		File newFileLocation = fileChooser.showOpenDialog(new Stage());
		if(newFileLocation == null) {
			return;
		}
		CsvIo.setFilePath(newFileLocation.getPath());
		CsvIo.writeCsv(this.todoObsList);
	}
	
	public void openFile(ActionEvent e) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Wähle Todo Speicherort");
		
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

		fileChooser.getExtensionFilters().addAll(
			    new FileChooser.ExtensionFilter("TodoList", "*.csv")
		);
		File newFileLocation = fileChooser.showOpenDialog(new Stage());
		if(newFileLocation == null) {
			return;
		}
		CsvIo.setFilePath(newFileLocation.getPath());
		todoObsList.clear();
		ArrayList<Todo> loadedTodos = CsvIo.LoadCSV();
		todoObsList.addAll(loadedTodos);
	}
	
	//None ActionEvent listeners from FXML / Scene builder
	
	public boolean checkInputFields() {
		return (this.categoryTxt.getText().equals("") || this.headerTxt.getText().equals("") || this.todoTxt.getText().equals("")) ? false : true;
	}
	
	//Habe extra Methode gemacht, dass Heyda diese aufrufen kann, wenn er von CSV zur ListView/ObsList hinzuf�gt
	public void addToList(Todo todoItem) {
		if(!checkCategoryInMap(todoItem.getCategory())) {
			createCategoryMenuItem(todoItem.getCategory());
			this.todoCategoryFilter.put(todoItem.getCategory().toUpperCase(), new ArrayList<Todo>());
		} 
		this.todoCategoryFilter.get(todoItem.getCategory().toUpperCase()).add(todoItem);
		this.todoCategoryFilter.get(allCateg.getText().toUpperCase()).add(todoItem);
		this.todoObsList.add(todoItem);
		CsvIo.writeCsv(this.todoObsList);
	}
	
	
	public void updateTodo(Todo todoItem) {
		todoItem.setCategory(this.categoryTxt.getText());
		todoItem.setHeader(this.headerTxt.getText());
		todoItem.setTodo(this.todoTxt.getText());
		if (!todoItem.getCategory().equals(this.categoryTxt.getText())) {
			
		}
		//Get the indicies of both Lists
		int idxAll = this.todoCategoryFilter.get(allCateg.getText().toUpperCase()).indexOf(todoItem);
		int idxCateg = this.todoCategoryFilter.get(todoItem.getCategory().toUpperCase()).indexOf(todoItem);
		//set them
		this.todoCategoryFilter.get(allCateg.getText().toUpperCase()).set(idxAll, todoItem);
		this.todoCategoryFilter.get(todoItem.getCategory().toUpperCase()).set(idxCateg, todoItem);
		//System.out.println(idxAll + " " + idxCateg);
		this.todoObsList.set(selectedItemidx, todoItem);
		CsvIo.writeCsv(this.todoObsList);

	}
	
	public void resetTextFields() {
		this.categoryTxt.setText("");
		this.headerTxt.setText("");
		this.todoTxt.setText("");
	}
	
	
	
	public void createAlert(String header, String errMsg) {
		System.out.println(header + " " + errMsg);
	}
	
	public void setObsListContent(ArrayList<Todo> list) {
		this.todoObsList.setAll(list);
	}
	
	public boolean checkCategoryInMap(String category) {
		return (todoCategoryFilter.containsKey(category.toUpperCase())) ? true : false;
	}
	
	public void createCategoryMenuItem(String category) {
		MenuItem item = new MenuItem();
		item.setText(category);
		this.categoryMenu.getItems().add(item);
	}
	
	public void removeCategoryFromMenu(String category) {
		//this.categoryMenu.getItems().removeAll(null);
	}
	
	
	// custom listeners
	public void itemClicked(MouseEvent e) {
		this.selectedItemidx = todoList.getSelectionModel().getSelectedIndex();
		if (this.selectedItemidx == -1) return;
		this.selectedItem = todoObsList.get(selectedItemidx);
		this.categoryTxt.setText(this.selectedItem.getCategory());
		this.headerTxt.setText(this.selectedItem.getHeader());
		this.todoTxt.setText(this.selectedItem.getTodo());
	}
	
	public void setListeners() {
		this.todoList.setOnMouseClicked((e) -> itemClicked(e));
	}
	
}
