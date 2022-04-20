package base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import base.Folder;
import base.Note;
import base.NoteBook;
import base.TextNote;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 
 * NoteBook GUI with JAVAFX
 * 
 * COMP 3021
 * 
 * 
 * @author valerio
 *
 */
public class NoteBookWindow extends Application {
	//C:\Users\minec\eclipse-workspace\comp3021-2\src\base\test.ser
	final TextArea textAreaNote = new TextArea("");
	final ListView<String> titleslistView = new ListView<String>();
	final ComboBox<String> foldersComboBox = new ComboBox<String>();
	final TextField search = new TextField("");
	
	NoteBook noteBook = null;
	String currentFolder = "";
	String currentSearch = "";
	String currentNote = "";
	
	Stage stage;
	
	public static void main(String[] args) {
		launch(NoteBookWindow.class, args);
	}

	@Override
	public void start(Stage stage) {
		loadNoteBook();
		// Use a border pane as the root for scene
		BorderPane border = new BorderPane();
		// add top, left and center
		border.setTop(addHBox());
		border.setLeft(addVBox());
		border.setCenter(addGridPane());

		Scene scene = new Scene(border);
		stage.setScene(scene);
		stage.setTitle("NoteBook COMP 3021");
		stage.show();
	}

	/**
	 * This create the top section
	 * 
	 * @return
	 */
	private HBox addHBox() {

		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 12, 15, 12));
		hbox.setSpacing(10); // Gap between nodes

		Button buttonLoad = new Button("Load");
		buttonLoad.setPrefSize(100, 20);
		buttonLoad.setOnAction(e -> {
			currentFolder = "";
			currentSearch = "";
			currentNote = "";
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Please Choose An File Which Cointains a NoteBook Object!");
			
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Serialized Object File (*.ser)", "*.ser");
			fileChooser.getExtensionFilters().add(extFilter);
			
			File file = fileChooser.showOpenDialog(stage);
			if (file!=null) {
				loadNoteBook(file.toString());
				System.out.println(noteBook.getFolders().toString());
			}
			
		});
		Button buttonSave = new Button("Save");
		buttonSave.setPrefSize(100, 20);
		buttonSave.setOnAction(e -> {
			
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Please Choose A Directory To Save!");
			
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Serialized Object File (*.ser)", "*.ser");
			fileChooser.getExtensionFilters().add(extFilter);
			
			File file = fileChooser.showOpenDialog(stage);
			if (file!=null) {
				if (noteBook.save(file.toString())) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Successfully saved");
					alert.setContentText("You file has been saved to file " + file.getName());
					alert.showAndWait().ifPresent(rs -> {});
				} else {
					Alert alert = new Alert(AlertType.WARNING);
			    	alert.setTitle("Warning");
			    	alert.setContentText("Exception Occur in saving...");
			    	alert.showAndWait().ifPresent(rs -> {});
				}
			}
		});
		
		Label labelSearch = new Label("Search : ");
		labelSearch.setPrefSize(60, 20);
		
		search.setText(currentSearch);
		
		//change here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		Button buttonSearch = new Button("Search");
		buttonSearch.setDefaultButton(true);
		buttonSearch.setPrefSize(100, 20);
		buttonSearch.setOnAction(e -> {
			currentSearch = search.getText();
			updateListView();
		});
		Button buttonClearSearch = new Button("Clear Search");
		buttonClearSearch.setCancelButton(true);
		buttonClearSearch.setPrefSize(100, 20);
		buttonClearSearch.setOnAction(e -> {
			currentSearch = "";
			currentFolder = "";
			currentNote = "";
			textAreaNote.setText("");
			search.setText(currentSearch);
			updateListView();
		});
		
		hbox.getChildren().addAll(buttonLoad, buttonSave, labelSearch, search, buttonSearch, buttonClearSearch);

		return hbox;
	}

	/**
	 * this create the section on the left
	 * 
	 * @return
	 */
	private VBox addVBox() {

		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10)); // Set all sides to 10
		vbox.setSpacing(8); // Gap between nodes
		
		//change here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		Button buttonAddFolder = new Button("Add a Folder");
		buttonAddFolder.setPrefSize(100, 20);
		buttonAddFolder.setOnAction(e -> {
			TextInputDialog dialog = new TextInputDialog("Add a Folder");
		    dialog.setTitle("Input");
		    dialog.setHeaderText("Add a new folder for your notebook:");
		    dialog.setContentText("Please enter the name you want to create:");
		    // Traditional way to get the response value.
		    Optional<String> result = dialog.showAndWait();
		    if (result.isPresent()){
		    	if (result.get()=="") {
		    		Alert alert = new Alert(AlertType.WARNING);
			    	alert.setTitle("Warning");
			    	alert.setContentText("Please input a valid folder name");
			    	alert.showAndWait().ifPresent(rs -> {});
		    	} else {
		    		boolean exist = false;
		    		for (Folder f1:noteBook.getFolders())  {	
		    			if (f1.equals(result.get())) 
		    				exist = true;
		    		}
		    		if (exist) {
		    			Alert alert = new Alert(AlertType.WARNING);
				    	alert.setTitle("Warning");
				    	alert.setContentText("You already have a folder named with " + result.get());
				    	alert.showAndWait().ifPresent(rs -> {});
			    		
		    		} else {
		    			noteBook.addFolder(result.get());
		    			updateFolders();
			    		Alert alert = new Alert(AlertType.INFORMATION);
			    		alert.setTitle("Successfully created");
			    		alert.setContentText("You folder "+result.get()+" has been created!");
			    		alert.showAndWait().ifPresent(rs -> {});
		    		}
		    	}
		    } 
		});
		
		Button buttonAddNote = new Button("Add a Note");
		buttonAddNote.setPrefSize(100, 20);
		buttonAddNote.setOnAction(e -> {
			boolean exist = false;
    		for (Folder f1:noteBook.getFolders())  {	
    			if (f1.equals(currentFolder)) 
    				exist = true;
    		}
			if (!(exist)) {
				Alert alert = new Alert(AlertType.WARNING);
		    	alert.setTitle("Warning");
		    	alert.setContentText("Please choose a folder first!");
		    	alert.showAndWait().ifPresent(rs -> {});
		    	return;
			}
			TextInputDialog dialog = new TextInputDialog("Add a Folder");
		    dialog.setTitle("Input");
		    dialog.setHeaderText("Add a new note to the current folder");
		    dialog.setContentText("Please enter the name of your note:");
		    Optional<String> result = dialog.showAndWait();
		    if (result.isPresent()){
		    	if (result.get()=="") {
		    		Alert alert = new Alert(AlertType.WARNING);
			    	alert.setTitle("Warning");
			    	alert.setContentText("Please input a valid folder name");
			    	alert.showAndWait().ifPresent(rs -> {});
			    	return;
		    	}
		    	noteBook.createTextNote(currentFolder, result.get());
		    	updateListView();
		    }
		});
		
		
		// TODO: This line is a fake folder list. We should display the folders in noteBook variable! Replace this with your implementation
		for (Folder f : noteBook.getFolders()){
			foldersComboBox.getItems().add(f.getName());
		}
		
		

		foldersComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				if(t1==null) {
					currentFolder = "";
				} else {
					currentFolder = t1.toString();
				}
				// this contains the name of the folder selected
				// TODO update listview
				
				updateListView();
			}

		});

		foldersComboBox.setValue("-----");
		
		titleslistView.setPrefHeight(100);

		titleslistView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object t, Object t1) {
				if (t1 == null) {
					currentNote = "";
					return;}
				String title = t1.toString();
				// This is the selected title
				// TODO load the content of the selected note in
				// textAreNote
				currentNote = title;
				System.out.println(currentNote);
				String content = "";
				Folder currentF = null;
				for (Folder f : noteBook.getFolders()){
					if(f.equals(currentFolder)){
						currentF = f ;
					};
				}
				
				for (Note n : currentF.getNotes()) if(n.getTitle()==title) 
						if(n instanceof TextNote) {
								TextNote tn =(TextNote)n;
								content = tn.getContent();
						}
				textAreaNote.setText(content);

			}
		});
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		hbox.getChildren().addAll(foldersComboBox, buttonAddFolder);
		
		vbox.getChildren().add(new Label("Choose folder: "));
		vbox.getChildren().add(hbox);
		vbox.getChildren().add(new Label("Choose note title"));
		vbox.getChildren().add(titleslistView);
		vbox.getChildren().add(buttonAddNote);
		return vbox;
	}

	private void updateListView() {
		ArrayList<String> list = new ArrayList<String>();

		// TODO populate the list object with all the TextNote titles of the
		// currentFolder

		if (currentNote=="") titleslistView.getSelectionModel().clearSelection();
		Folder current = null;
		for (Folder f : noteBook.getFolders()){
			if(f.equals(currentFolder)){
				current = f ;
			};
		}
		ObservableList<String> combox2 = FXCollections.observableArrayList(list);
		ArrayList<Note> N = new ArrayList<>();
		if (current!=null)
			if (currentSearch == "") N = current.getNotes();
			else N = current.searchNotes(currentSearch);
		
		if (current!=null) for (Note n : N) if (n instanceof TextNote) combox2.add(n.getTitle());
		titleslistView.setItems(combox2);
		textAreaNote.setText("");
	}
	
	private void updateFolders() {
		currentNote = "";
		currentFolder = "";
		updateListView();
		foldersComboBox.getSelectionModel().clearSelection();
		foldersComboBox.getItems().clear();
		for (Folder f : noteBook.getFolders()){
			foldersComboBox.getItems().add(f.getName());
		}
	}

	/*
	 * Creates a grid for the center region with four columns and three rows
	 */
	private GridPane addGridPane() {

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		
		Button buttonSaveNote = new Button("Save Note");
		buttonSaveNote.setPrefSize(100, 20);
		buttonSaveNote.setOnAction(e -> {
			if (currentFolder=="" || currentNote=="") {
				Alert alert = new Alert(AlertType.WARNING);
		    	alert.setTitle("Warning");
		    	alert.setContentText("Please select a folder and a note");
		    	alert.showAndWait().ifPresent(rs -> {});
		    	return;
			}
			noteBook.SaveNote(currentFolder, currentNote, textAreaNote.getText());
		});
		
		Button buttonDeleteNote = new Button("Delete Note");
		buttonDeleteNote.setPrefSize(100, 20);
		buttonDeleteNote.setOnAction(e -> {
			if (currentFolder=="" || currentNote=="") {
				Alert alert = new Alert(AlertType.WARNING);
		    	alert.setTitle("Warning");
		    	alert.setContentText("Please select a folder and a note");
		    	alert.showAndWait().ifPresent(rs -> {});
		    	return;
			}
			noteBook.DeleteNote(currentFolder, currentNote);
			currentNote = ""; 
			updateListView();
			Alert alert = new Alert(AlertType.INFORMATION);
	    	alert.setTitle("Confirmation");
	    	alert.setContentText("Your note has been successfully removed");
	    	alert.showAndWait().ifPresent(rs -> {});
		});
		InputStream stream = null;
		try {
			stream = new FileInputStream("C:\\Users\\minec\\eclipse-workspace\\comp3021-2\\src\\base\\save.png");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Image saveImage = new Image(stream, 20, 20, false, false);
		
		try {
			stream = new FileInputStream("C:\\Users\\minec\\eclipse-workspace\\comp3021-2\\src\\base\\delete.png");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		Image deleteImage = new Image(stream, 20, 20, false, false);
		
		HBox hbox = new HBox();
		hbox.setSpacing(10);
		hbox.getChildren().addAll(new ImageView(saveImage), 
				buttonSaveNote, 
				new ImageView(deleteImage), 
				buttonDeleteNote);
		
		textAreaNote.setEditable(true);
		textAreaNote.setMaxSize(450, 400);
		textAreaNote.setWrapText(true);
		textAreaNote.setPrefWidth(450);
		textAreaNote.setPrefHeight(400);
		// 0 0 is the position in the grid
		grid.add(hbox, 0, 0);
		grid.add(textAreaNote, 0, 1);

		return grid;
	}
	private void loadNoteBook(String file) {
		NoteBook nb = new NoteBook(file);
		noteBook = nb;
		updateListView();
		updateFolders();
	}
	private void loadNoteBook() {
		NoteBook nb = new NoteBook();
		nb.createTextNote("COMP3021", "COMP3021 syllabus", "Be able to implement object-oriented concepts in Java.");
		nb.createTextNote("COMP3021", "course information",
				"Introduction to Java Programming. Fundamentals include language syntax, object-oriented programming, inheritance, interface, polymorphism, exception handling, multithreading and lambdas.");
		nb.createTextNote("COMP3021", "Lab requirement",
				"Each lab has 2 credits, 1 for attendence and the other is based the completeness of your lab.");

		nb.createTextNote("Books", "The Throwback Special: A Novel",
				"Here is the absorbing story of twenty-two men who gather every fall to painstakingly reenact what ESPN called 鈥渢he most shocking play in NFL history鈥� and the Washington Redskins dubbed the 鈥淭hrowback Special鈥�: the November 1985 play in which the Redskins鈥� Joe Theismann had his leg horribly broken by Lawrence Taylor of the New York Giants live on Monday Night Football. With wit and great empathy, Chris Bachelder introduces us to Charles, a psychologist whose expertise is in high demand; George, a garrulous public librarian; Fat Michael, envied and despised by the others for being exquisitely fit; Jeff, a recently divorced man who has become a theorist of marriage; and many more. Over the course of a weekend, the men reveal their secret hopes, fears, and passions as they choose roles, spend a long night of the soul preparing for the play, and finally enact their bizarre ritual for what may be the last time. Along the way, mishaps, misunderstandings, and grievances pile up, and the comforting traditions holding the group together threaten to give way. The Throwback Special is a moving and comic tale filled with pitch-perfect observations about manhood, marriage, middle age, and the rituals we all enact as part of being alive.");
		nb.createTextNote("Books", "Another Brooklyn: A Novel",
				"The acclaimed New York Times bestselling and National Book Award鈥搘inning author of Brown Girl Dreaming delivers her first adult novel in twenty years. Running into a long-ago friend sets memory from the 1970s in motion for August, transporting her to a time and a place where friendship was everything鈥攗ntil it wasn鈥檛. For August and her girls, sharing confidences as they ambled through neighborhood streets, Brooklyn was a place where they believed that they were beautiful, talented, brilliant鈥攁 part of a future that belonged to them. But beneath the hopeful veneer, there was another Brooklyn, a dangerous place where grown men reached for innocent girls in dark hallways, where ghosts haunted the night, where mothers disappeared. A world where madness was just a sunset away and fathers found hope in religion. Like Louise Meriwether鈥檚 Daddy Was a Number Runner and Dorothy Allison鈥檚 Bastard Out of Carolina, Jacqueline Woodson鈥檚 Another Brooklyn heartbreakingly illuminates the formative time when childhood gives way to adulthood鈥攖he promise and peril of growing up鈥攁nd exquisitely renders a powerful, indelible, and fleeting friendship that united four young lives.");

		nb.createTextNote("Holiday", "Vietnam",
				"What I should Bring? When I should go? Ask Romina if she wants to come");
		nb.createTextNote("Holiday", "Los Angeles", "Peter said he wants to go next Agugust");
		nb.createTextNote("Holiday", "Christmas", "Possible destinations : Home, New York or Rome");
		noteBook = nb;

	}

}
