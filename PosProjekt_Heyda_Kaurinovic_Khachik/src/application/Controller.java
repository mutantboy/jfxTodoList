package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
	MenuItem noneCateg;
	@FXML
	MenuItem doneCateg;
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
	
	private ArrayList<Todo> todoAll;
	
	private HashMap<String, Todo> todoCategoryFilter;
	

	
	private Todo selectedItem;
	private int selectedItemidx;
	
	public Controller() {
		this.todoCategoryFilter = new HashMap<String, Todo>();
		this.todoAll = new ArrayList<Todo>();
		this.todoObsList= FXCollections.observableArrayList();
	}
	
	@FXML
	public void initialize() {
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
	}
	
	public void resetInput(ActionEvent e) {
		resetTextFields();
	}
	
	public void resetAll(ActionEvent e) {
		
	}
	
	//None ActionEvent listeners from FXML / Scene builder
	
	public boolean checkInputFields() {
		return (this.categoryTxt.getText().equals("") || this.headerTxt.getText().equals("") || this.todoTxt.getText().equals("")) ? false : true;
	}
	
	//Habe extra Methode gemacht, dass Heyda diese aufrufen kann, wenn er von CSV zur ListView/ObsList hinzufügt
	public void addToList(Todo todoItem) {
		this.todoAll.add(todoItem);
		this.todoObsList.add(todoItem);
	}
	
	
	public void updateTodo(Todo todoItem) {
		todoItem.setCategory(this.categoryTxt.getText());
		todoItem.setHeader(this.headerTxt.getText());
		todoItem.setTodo(this.todoTxt.getText());
		this.todoObsList.set(selectedItemidx, todoItem);

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
