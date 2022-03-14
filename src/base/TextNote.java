package base;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TextNote extends Note implements Serializable{
	private String content;
	
	public TextNote (String title) {
		super(title);
	}
	
	public TextNote(File f) {
		super(f.getName());
		this.content = getTextFromFile(f.getAbsolutePath());
	}
	
	public TextNote (String title, String content) {
		super(title);
		this.content = content;
	}
	
	public String getContent() {
		return content;
	}
	
	private String getTextFromFile(String absolutePath) {
		String result = "";
		try{
			result = new String (Files.readAllBytes(Paths.get(absolutePath))) ;
		}catch (IOException e) {
			e.printStackTrace();
		}
	    	
		return result;
	}

	public void exportTextToFile(String pathFolder) {
		if(pathFolder == "") {
			pathFolder = ".";
		}
		try {
			FileWriter myWriter = new FileWriter(pathFolder + File.separator + this.getTitle().replaceAll(" ", "_")+".txt");
			myWriter.write(this.content);
			myWriter.close();
		} catch (IOException e) {
		      e.printStackTrace();
		}
	}
}
