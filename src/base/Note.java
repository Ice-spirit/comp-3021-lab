package base;
import java.util.Date;
import java.util.Objects;

public class Note {
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
	
}
