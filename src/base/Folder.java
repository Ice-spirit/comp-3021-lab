package base;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Enumeration;
import java.util.Collections;

public class Folder {
	
		private ArrayList<Note> notes = new ArrayList<Note>();
		private String name;
		
		public Folder(String name) {
			this.name = name;
		}
		
		public void addNote(Note newNote) {
			this.notes.add(newNote);
		}
		
		public String getName() {
			return this.name;
		}
		
		public ArrayList<Note> getNotes() {
			return this.notes;
		}

		public String toString() {
			int nText = 0;
			int nImage = 0;
			Enumeration<Note> e = Collections.enumeration(notes);
			 while(e.hasMoreElements()) {
				 if (e.nextElement() instanceof TextNote) {nText+=1;} else {nImage+=1;}
			 }
			return name + " : " + nText + " : " + nImage;
		}

		public boolean equals(String checkname) {
			return this.name == checkname;
		}
		
		
}
