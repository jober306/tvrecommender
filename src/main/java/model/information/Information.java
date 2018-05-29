package model.information;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

public interface Information extends Serializable{
	
	public String asString();
	
	default public void print(){
		System.out.println(asString());
	}
	
	default public void toFile(String outputPath){
		try(BufferedWriter bw = Files.newBufferedWriter(Paths.get(outputPath))){
			bw.write(asString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	default public void serialize(String outputPath){
		try {
			FileOutputStream fos = new FileOutputStream(outputPath);
		    ObjectOutputStream oos = new ObjectOutputStream(fos);
		    oos.writeObject(this);
		    oos.flush();
		    oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
