package tr.com.msen.ytu.crypto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TemporaryFile {

	public static void main(String[] args) throws IOException {
		String filename = createTempFile("TemporaryContent".getBytes());
		printContent(filename);
	}

	// Creates a temporary file that will be deleted on JVM exit.
	public static String createTempFile(byte[] values) throws IOException {
		// Since Java 1.7 Files and Path API simplify operations on files
		String fileName = "Temp";
		Path path = Files.createTempFile(fileName, "");
		File file = path.toFile();
		// writing sample data
		Files.write(path, values);
		// This tells JVM to delete the file on JVM exit.
		// Useful for temporary files in tests.
		file.deleteOnExit();
		return file.getAbsolutePath();
	}

	private static void printContent(String filename) throws IOException {
		// In Java 8 you can use forEach() method instead of iterating
		// collection.
		// Moreover static methods can be passed as an arguments.
		Files.readAllLines(Paths.get(filename)).forEach(System.out::println);
	}

}
