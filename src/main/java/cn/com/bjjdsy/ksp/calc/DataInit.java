package cn.com.bjjdsy.ksp.calc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public abstract class DataInit {

	public abstract void loadParktimeData();

	public abstract void loadDepartData();

	public abstract void loadWalktimeData();

	public abstract void loadTransferData();

	public abstract void loadStationData();

	public abstract void loadLineData();
	// line dict (tracks)

	public abstract void loadSectionData();

	public BufferedReader readFile(String filename) {
		BufferedReader reader = null;

		// initialize the reader
		try {
			reader = new BufferedReader(new FileReader(new File(filename)));
			reader.readLine();
			reader.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reader;
	}

	public DataInit init() {
		return this;
	}
}
