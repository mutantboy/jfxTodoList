package application;

import java.time.LocalDate;
import java.util.Objects;

import exception.BadStatusException;

public class Todo {
	
	public enum Status {
		PENDING(0), DONE(1);
		
		private int statusCode;
		
		Status(int statusCode) {
			switch(statusCode) {
				case 0, 1 -> this.statusCode = statusCode; 
				default -> throw new BadStatusException("Status number is not 1 or 0");
			}
			
		}
		
		public int getStatusCode() {return this.statusCode;}
		
		public static Status getStatusFromCode(int statusCode) {
			if (statusCode > 1 || statusCode < 0) throw new BadStatusException("Status number is not 1 or 0");
			for (Status st : Status.values()) {
				if (st.getStatusCode() == statusCode) return st;
			}
			return null;
		}
		
		
	}

	private String header;
	private String category;
	private String todo;
	private Status status;
	
	private LocalDate dateCreated;
	
	public Todo(String header, String category, String todo) {
		setHeader(header);
		setCategory(category);
		setTodo(todo);
		setStatus(Status.getStatusFromCode(0));
		dateCreated = LocalDate.now();
		System.out.println(generateCSVline());
	}
	
	

	public String generateCSVline() {
		return String.format("%s, %s, %s, %d, %s;", this.category, this.header, this.todo, this.status.ordinal(), this.dateCreated);
		//return " ";
	}
	

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getTodo() {
		return todo;
	}

	public void setTodo(String todo) {
		this.todo = todo;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public LocalDate getDateCreated() {
		return dateCreated;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(category, dateCreated, header, status, todo);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Todo other = (Todo) obj;
		return Objects.equals(category, other.category) && Objects.equals(dateCreated, other.dateCreated)
				&& Objects.equals(header, other.header) && status == other.status && Objects.equals(todo, other.todo);
	}



	@Override
	public String toString() {
		return this.header + ": " + this.dateCreated;
	}



	public String toCsv() {
		return getHeader() + ";" + getCategory() + ";" + getTodo();
	}
	
}
