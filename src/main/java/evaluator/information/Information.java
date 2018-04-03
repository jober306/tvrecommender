package evaluator.information;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public interface Information {
	
	public String asString();
	
	default public void print(){
		System.out.println(asString());
	}
	
	default public void toFile(String outputPath){
		try(BufferedWriter bw = Files.newBufferedWriter(Paths.get(outputPath))){
			bw.write(asString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
