package model.information;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Optional;

public abstract class AbstractInformation implements Information{
	
	private static final long serialVersionUID = 1L;

	public static Optional<Information> deserialize(String inputPath){
		try {
			FileInputStream fis = new FileInputStream(inputPath);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Information info = (Information) ois.readObject();
			ois.close();
			return Optional.of(info);
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
}
