package Csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import application.Todo;
import javafx.collections.ObservableList;

public class CsvIo {
	
	static String filePath = "./TodoList.csv";
	
	public static void setFilePath(String filePath_) {
		filePath = filePath_;
	}
	
	public static ArrayList<Todo> LoadCSV()
	{
	    ArrayList<Todo> todoList= new ArrayList<>();
	    
	    File file = new File(filePath);
	     
	    // prüfen, ob Datei existiert
	    if (file.exists() && file.isFile())
	    {
	        BufferedReader br = null;
	        FileReader fr = null;
	 
	        try
	        {
	            fr = new FileReader(file);
	            br = new BufferedReader(fr);
	             
	            String line;
	             
	            // solange Zeilen in der Datei vorhanden
	            while ((line = br.readLine()) != null)
	            {
	                // Zeilen anhand des Separators,
	                // z.B. ";", aufsplitten
	                String[] todoArray = line.split(";");
	                Todo todo = new Todo (todoArray[0], todoArray[1], todoArray[2]);
	                todoList.add(todo);
	               
	            }
	             

	            if (br != null)
	            {
	                br.close();
	            }
	 
	            if (fr != null)
	            {
	                fr.close();
	            }
	            
	        }
	        
	        catch(Exception e){
	        	e.printStackTrace();
	        }
	        
	    }
	     
	    return todoList;
	}
	
	public static void writeCsv(List<Todo> todoList)
	{
	    File file = new File(filePath);
	    System.out.println(file.getAbsolutePath());
	    try {
	    	
	    	PrintWriter pw = new PrintWriter(file);
	    	for(Todo e : todoList) {
		    	String todoCsv = e.toCsv();
		    	pw.println(todoCsv);
	    	}
	    	pw.close();
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
}
