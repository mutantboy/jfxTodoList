package application;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import Csv.CsvIo;
import application.Todo.Status;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
	MenuItem pendingCateg;
	
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
		todoList.setItems(todoObsList); //ListView is null when in construction stage, but not when initializing FXML file
		setListeners(); //setting listeners after initialization of FXML and not after construction of class
		this.todoCategoryFilter.put(allCateg.getText().toUpperCase(), new ArrayList<Todo>());
		this.todoCategoryFilter.put(doneCateg.getText().toUpperCase(), new ArrayList<Todo>());
		this.todoCategoryFilter.put(pendingCateg.getText().toUpperCase(), new ArrayList<Todo>());
		ArrayList<Todo> loadedTodos = CsvIo.LoadCSV();
		loadedTodos.forEach(item -> {
			populateCategoryMenuItemsAndCategoryLists(item);
		});
		this.selectedCategory = allCateg.getText().toUpperCase();
		

		todoObsList.addAll(this.todoCategoryFilter.get(allCateg.getText().toUpperCase()));

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
		if (selectedItemidx == -1) {
			createAlert("No Item selected", "Please select an item first, before updating the change");
			return;
		}
		if (!checkInputFields()) {
			createAlert("Bad input", "One or more input fields are empty. Please fill all of them out"); 
			return;
		}
		updateTodo(selectedItem);
		resetTextFields();
	}
	
	public void todoDone(ActionEvent e) {
		try {
			Todo  itemToBeSetDone = this.todoObsList.get(selectedItemidx);
			itemToBeSetDone.setStatus(Status.DONE);
			this.todoCategoryFilter.get(itemToBeSetDone.getCategory().toUpperCase()).remove(itemToBeSetDone);
			this.todoCategoryFilter.get(allCateg.getText().toUpperCase()).remove(itemToBeSetDone);
			this.todoCategoryFilter.get(doneCateg.getText().toUpperCase()).add(itemToBeSetDone);
			if (selectedCategory.equals(allCateg.getText().toUpperCase())) setObsListContent(this.todoCategoryFilter.get(allCateg.getText().toUpperCase()));
			if (selectedCategory.equals(itemToBeSetDone.getCategory().toUpperCase())) setObsListContent(this.todoCategoryFilter.get(itemToBeSetDone.getCategory().toUpperCase()));
		}catch (IndexOutOfBoundsException exp) {
			System.out.println("index is out of bound in todoDone call");
		}
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
		ArrayList<Todo> categoryFilteredList = this.todoCategoryFilter.get(selectedCategory);
		if (categoryFilteredList == null) return;
		setObsListContent(categoryFilteredList);
	}
	
	public void displayAllTodos(ActionEvent e) {
		setObsListContent(this.todoCategoryFilter.get(allCateg.getText().toUpperCase()));

	}
	
	public void displayDone(ActionEvent e) {
		setObsListContent(this.todoCategoryFilter.get(doneCateg.getText().toUpperCase()));
	}
	public void displayPending(ActionEvent e) {
		
	}
	
	//File Logic
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
		CsvIo.writeCsv(this.todoCategoryFilter.get(allCateg.getText()));
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
		for (Todo item : loadedTodos) {
			populateCategoryMenuItemsAndCategoryLists(item);
		}
		todoObsList.setAll(this.todoCategoryFilter.get(allCateg.getText().toUpperCase()));
	}
	
	//None ActionEvent listeners from FXML / Scene builder
	
	public boolean checkInputFields() {
		return (this.categoryTxt.getText().equals("") || this.headerTxt.getText().equals("") || this.todoTxt.getText().equals("")) ? false : true;
	}
	
	private void populateCategoryMenuItemsAndCategoryLists(Todo todoItem) {
		if (todoItem.getStatus().ordinal() == 1) {
			this.todoCategoryFilter.get(doneBtn.getText().toUpperCase()).add(todoItem);
			return;
		}
		if(!checkCategoryInMap(todoItem.getCategory())) {
			createCategoryMenuItem(todoItem.getCategory());
			this.todoCategoryFilter.put(todoItem.getCategory().toUpperCase(), new ArrayList<Todo>());
		} 
		this.todoCategoryFilter.get(todoItem.getCategory().toUpperCase()).add(todoItem);
		this.todoCategoryFilter.get(allCateg.getText().toUpperCase()).add(todoItem);
	}
	
	//Habe extra Methode gemacht, dass Heyda diese aufrufen kann, wenn er von CSV zur ListView/ObsList hinzufï¿½gt
	public void addToList(Todo todoItem) {
		/*if(!checkCategoryInMap(todoItem.getCategory())) {
			createCategoryMenuItem(todoItem.getCategory());
			this.todoCategoryFilter.put(todoItem.getCategory().toUpperCase(), new ArrayList<Todo>());
		} 
		this.todoCategoryFilter.get(todoItem.getCategory().toUpperCase()).add(todoItem);
		this.todoCategoryFilter.get(allCateg.getText().toUpperCase()).add(todoItem);*/
		populateCategoryMenuItemsAndCategoryLists(todoItem);
		this.todoObsList.setAll(this.todoCategoryFilter.get(selectedCategory));
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
		try {
			this.todoCategoryFilter.get(allCateg.getText().toUpperCase()).set(idxAll, todoItem);
			this.todoCategoryFilter.get(todoItem.getCategory().toUpperCase()).set(idxCateg, todoItem);
		} catch(java.lang.IndexOutOfBoundsException e) {
			createAlert("Something went wrong", "Something went wrong with updating the selected todo");
		}
		
		//System.out.println(idxAll + " " + idxCateg);
		setObsListContent(this.todoCategoryFilter.get(selectedCategory));
	}
	
	public void resetTextFields() {
		this.categoryTxt.setText("");
		this.headerTxt.setText("");
		this.todoTxt.setText("");
	}

	public void createAlert(String header, String errMsg) {
		System.out.println(header + " " + errMsg);
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText(header);
		alert.setContentText(errMsg);
		alert.show();
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
