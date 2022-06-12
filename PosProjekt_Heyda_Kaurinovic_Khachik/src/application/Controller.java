package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import Csv.CsvIo;
import Csv.CsvWriter;
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

	
	private Todo selectedItem;
	private int selectedItemidx;
	
	public Controller() {
		todoObsList= FXCollections.observableArrayList();
	}
	
	@FXML
	public void initialize() {
		ArrayList<Todo> loadedTodos = CsvIo.LoadCSV();
		todoObsList.addAll(loadedTodos);
		todoList.setItems(todoObsList); //ListView is null when in construction stage, but not when initializing FXML file
		setListeners(); //setting listeners after initialization of FXML and not after construction of class
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
		this.todoObsList.remove(this.selectedItemidx);
		CsvIo.writeCsv(this.todoObsList);
	}
	
	public void resetInput(ActionEvent e) {
		resetTextFields();
	}
	
	public void resetAll(ActionEvent e) {
		
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
		this.todoObsList.add(todoItem);
		CsvIo.writeCsv(this.todoObsList);
	}
	
	
	public void updateTodo(Todo todoItem) {
		todoItem.setCategory(this.categoryTxt.getText());
		todoItem.setHeader(this.headerTxt.getText());
		todoItem.setTodo(this.todoTxt.getText());
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
