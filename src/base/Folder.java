package base;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Enumeration;
import java.util.Collections;
import java.util.regex.Pattern;

public class Folder implements Comparable<Folder>{
	
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
		
		public void sortNotes(){
			Collections.sort(notes);
		}
		
		@Override
		public int compareTo(Folder f) {
			int i = this.name.compareTo(f.name);
			if (i == 0) {
				return 0;
			} else if (i > 0) {
				return 1;
			} else {
				return -1;
			}
		}
		
		public ArrayList<Note> searchNotes(String keyword){
			ArrayList<Note> result = new ArrayList<Note>();
			String[] keywords = keyword.split(" ");
			String t;
			String c;
			Note n;
			boolean same, temp;
			for(int i = 0; i < notes.size(); i++){
				same = true;
				n = notes.get(i);
				for(int j = 0; j < keywords.length; j++){
					temp = false;
					//get temp and check if there's substring in note i for keyword j
					if (n instanceof TextNote) {
						TextNote tn = (TextNote)n;
						t = tn.getTitle();
						c = tn.getContent();
						temp = Pattern.compile(Pattern.quote(keywords[j]), Pattern.CASE_INSENSITIVE).matcher(t).find() || Pattern.compile(Pattern.quote(keywords[j]), Pattern.CASE_INSENSITIVE).matcher(c).find();
					} else {
						t = n.getTitle();
						temp = Pattern.compile(Pattern.quote(keywords[j]), Pattern.CASE_INSENSITIVE).matcher(t).find();
					}
					//check for OR
					while (j + 2 < keywords.length && keywords[j+1].equalsIgnoreCase("or") && temp == false) {
				    	j = j + 2;
				    	if (n instanceof TextNote) {
				    		TextNote tn = (TextNote)n;
				    		c = tn.getContent();
							temp = Pattern.compile(Pattern.quote(keywords[j]), Pattern.CASE_INSENSITIVE).matcher(t).find() || Pattern.compile(Pattern.quote(keywords[j]), Pattern.CASE_INSENSITIVE).matcher(c).find();
						} else {
							temp = Pattern.compile(Pattern.quote(keywords[j]), Pattern.CASE_INSENSITIVE).matcher(t).find();
						}
				    }
					while (j + 2 < keywords.length && keywords[j+1].equalsIgnoreCase("or") && temp == true) {
				    	j = j + 2;
				    }
					//update if it is true
					same = same && temp;
				}
				if (same) {
					result.add(n);
				}
			}
			
			return result;
		}
		
}
