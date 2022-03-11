package base;
import java.util.Date;
import java.util.Objects;

public class Note implements Comparable<Note>{
	private Date date;
	private String title;
	
	public Note(String title) {
		this.title = title;
		this.date = new Date(System.currentTimeMillis()); 
	}
	
	public String getTitle() {
		return this.title;
	}

	public boolean equals(Note note) {
		return this.title == note.title;
	}
	
	@Override
	public int compareTo(Note o) {
		//TO DO
		int i = this.date.compareTo(o.date);
		if (i == 0) {
			return 0;
		} else if (i > 0) {
			return -1;
		} else {
			return 1;
		}
	}
	
	public String toString() {
		return date.toString()+ "\t" + title;
	}
}
