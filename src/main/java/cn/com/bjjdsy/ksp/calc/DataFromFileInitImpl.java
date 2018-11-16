package cn.com.bjjdsy.ksp.calc;

import java.io.BufferedReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.com.bjjdsy.ksp.common.util.CalcConstant;
import cn.com.bjjdsy.ksp.entity.Line;
import cn.com.bjjdsy.ksp.entity.Section;
import cn.com.bjjdsy.ksp.entity.Station;

public class DataFromFileInitImpl extends DataInit {

	private static final Logger logger = LoggerFactory.getLogger("ksp");

	@Override
	public void loadParktimeData() {
		readFile(CalcConstant.STATION_PARKTIME);
	}

	@Override
	public void loadDepartData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadWalktimeData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadTransferData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadStationData() {
		try (BufferedReader reader = readFile(CalcConstant.STATION_INFO);) {
			reader.readLine();
			reader.readLine();
			while (true) {
				String line = null;
				line = reader.readLine();
				// if no input, break out
				if (line == null || line.length() == 0) {
					break;
				}
				// parse the data
				String[] data = line.split(",");
				int stationCode = Integer.parseInt(data[0]);
				String stationName = data[1];
				CalcConstant.STATION_DICT.put(stationCode, stationName);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void loadLineData() {
		try (BufferedReader reader = readFile(CalcConstant.LINE_INFO);) {
			reader.readLine();
			reader.readLine();
			while (true) {
				String line = null;
				line = reader.readLine();
				// if no input, break out
				if (line == null || line.length() == 0) {
					break;
				}
				String[] data = line.split(",");
				int lineCode = Integer.parseInt(data[0]);
				String lineName = data[1];
				CalcConstant.LINE_DICT.put(lineCode, lineName);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void loadSectionData() {
		Station[] stations = new Station[CalcConstant.MAX_STATION];
		Line[] lines = new Line[CalcConstant.MAX_LINE];
		try (BufferedReader reader = readFile(CalcConstant.SECTION_INFO);) {
			reader.readLine();
			reader.readLine();
			while (true) {
				String line = null;
				line = reader.readLine();
				// if no input, break out
				if (line == null || line.length() == 0) {
					break;
				}
				String[] data = line.split(",");
				int lineCode = Integer.parseInt(data[0]);
				int s1 = Integer.parseInt(data[1]);
				int s2 = Integer.parseInt(data[2]);
				int dist = Integer.parseInt(data[3]);
				int time = Integer.parseInt(data[4]);
				int dir = Integer.parseInt(data[5]);

				// check that the stations are valid
				if (stations[s1].getLine() != null && stations[s1].getLine().getCode() != lineCode)
					throw new IllegalArgumentException("Input first station is conflicting.");
				if (stations[s2].getLine() != null && stations[s2].getLine().getCode() != lineCode)
					throw new IllegalArgumentException("Input first station is conflicting.");

				// set the tracks that they are on
				stations[s1].setLine(lines[lineCode]);
				stations[s2].setLine(lines[lineCode]);

				// create a new Line
				int traveltime = time + (CalcConstant.PARKTIMES.get(s1 + "-" + s2) == null ? 0
						: CalcConstant.PARKTIMES.get(s1 + "-" + s2));
				double impedance = traveltime;
				Section section = new Section(stations[s1], stations[s2], lines[lineCode], lines++, dir, dist, traveltime,
						impedance);
//				System.out.printf("s1:%s-s2:%s\n", s1, s2);
				stations[s1].addSection(section);
				stations[s2].addSection(section);
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		CalcConstant.STATIONS = stations;
	}

}
