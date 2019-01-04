package tr.com.msen.ytu.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunAppFromJava {

	public static String run(String path) throws IOException, InterruptedException {

		String processName = path.substring(path.lastIndexOf("\\") + 1);
		Process exec = Runtime.getRuntime().exec(path);
		return processName;
	}

	public static void exit(String processName) throws Exception {
		if (isProcessRunning(processName)) {
			killProcess(processName);
		}
	}

	private static final String TASKLIST = "tasklist";
	private static final String KILL = "taskkill /F /IM ";

	private static boolean isProcessRunning(String serviceName) throws Exception {

		Process p = Runtime.getRuntime().exec(TASKLIST);
		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {

			if (line.contains(serviceName)) {
				return true;
			}
		}

		return false;

	}

	private static void killProcess(String serviceName) throws Exception {

		Runtime.getRuntime().exec(KILL + serviceName);

	}

}
