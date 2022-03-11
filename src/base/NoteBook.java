package base;
import java.util.ArrayList;
import java.util.Collections;


public class NoteBook {
	private ArrayList<Folder> folders;
	
	public NoteBook() {
		folders = new ArrayList<Folder>();
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
	
}
