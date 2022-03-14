package base;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;



public class NoteBook implements Serializable{
	private ArrayList<Folder> folders;
	private static final long serialVersionUID = 1L;
	
	public NoteBook() {
		folders = new ArrayList<Folder>();
	}
	
	public NoteBook(String file){
		FileInputStream fis = null;
	    ObjectInputStream in = null;
	    try {
	            fis = new FileInputStream(file);
	            in = new ObjectInputStream(fis);
	            NoteBook n = (NoteBook) in.readObject();
	            this.folders = new ArrayList<>(n.getFolders());
	            in.close();
	    } catch (Exception e) {
	            e.printStackTrace();
	    }
	}
	
	public boolean insertNote(String folderName, Note note) {
		boolean exist = false;
		Folder usingFolder = null;
		ArrayList<Note> Notes = null;
		//check if exist folder name
		for (Folder f1:folders) { 		      
			if (f1.equals(folderName)) {
				usingFolder = f1;
				exist = true;
			}
		}
		
		//if not exist, create folder
		if (!exist) {
			usingFolder = new Folder(folderName);
			folders.add(usingFolder);
		}
		
		//check note name
		Notes = usingFolder.getNotes();
		exist = false;
		for (int counter = 0; counter < Notes.size(); counter++) { 		      
		    if (Notes.get(counter).equals(note)) {
		    	  exist = true;
		    }
		}
		
		//if note exist send error, else add
		if (exist) {
			System.out.println("Creating note "+note.getTitle()+" under folder "+folderName+" failed");
			return false;
		} else {
			usingFolder.addNote(note);
			return true;
		}
	}
	
	public boolean createTextNote(String folderName, String title) {
		TextNote note = new TextNote(title);
		return insertNote(folderName, note);
	}
	
	//Overload
	public boolean createTextNote(String folderName, String title, String content) {
		TextNote note = new TextNote(title, content);
		return insertNote(folderName, note);
	}
	
	public boolean createImageNote(String folderName, String title) {
		ImageNote note = new ImageNote(title);
		return insertNote(folderName, note);
	}
	
	public ArrayList<Folder> getFolders(){
		return this.folders;
	}
	
	public void sortFolders(){
		for(int i = 0; i < folders.size(); i++){
		    folders.get(i).sortNotes();
		}
		Collections.sort(folders);
	}
	
	public ArrayList<Note> searchNotes(String keyword){
		ArrayList<Note> result = new ArrayList<Note>();
		for(int i = 0; i < folders.size(); i++){
		    result.addAll(folders.get(i).searchNotes(keyword));
		}
		return result;
	}
	
	public boolean save(String file){
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeObject(this);
			out.close();
		} catch (Exception e) {
		    return false;   
		}
		return true;
	}
	
}
