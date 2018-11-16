package cn.com.bjjdsy.ksp.calc;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import cn.com.bjjdsy.ksp.common.util.CalcConstant;
import cn.com.bjjdsy.ksp.entity.Line;
import cn.com.bjjdsy.ksp.entity.Path;
import cn.com.bjjdsy.ksp.entity.Section;
import cn.com.bjjdsy.ksp.entity.Station;

public class CalcEngine {

	private int k = 5;
	private int stationCounts;
	// the generated shortest paths
	private Path paths[][][];
	private int pathCounts[][];

	private int[] stationCodes;
	private int[] stationIndexes;

	private Station[] stations;
	private Line[] lines;

	private void init() {
		paths = new Path[stationCounts][stationCounts][k];
		pathCounts = new int[stationCounts][stationCounts];// 0

		DataInit dataInit = new DataFromFileInitImpl();
		dataInit.init();
	}

	private void generateStation(Map<Integer, String> stationDict) {
		for (Integer code : stationDict.keySet()) {
			stationIndexes[code] = stationCounts;
			stationCodes[stationCounts++] = code;
			stations[code] = new Station(code, null, stationDict.get(code));
		}
	}

	private void generateLine(Map<Integer, String> lineDict) {
		for (Integer code : lineDict.keySet()) {
			lines[code] = new Line(code, lineDict.get(code));
		}
	}

	public void calcPath() {

		for (int i = 0; i < stationCounts; ++i) {
			calc(i);
		}
	}

	private void calc(int startIdx) {
		Station source = stations[stationCodes[startIdx]];
		PriorityQueue<Path> pathQueue = new PriorityQueue<Path>();
		// create the first path
		Path begin = new Path();
		begin.addStation(source);
		pathQueue.add(begin);

		// iterate through possible paths
		while (!pathQueue.isEmpty()) {
			// get the shortest path and last station
			Path shortest = pathQueue.poll();
			Station lastStation = shortest.getEndStation();
			int endIdx = stationIndexes[lastStation.getCode()];
			// if enough paths have been found continue
			if (pathCounts[startIdx][endIdx] >= k) {
				continue;
			}
			Path tPath = new Path(shortest);
			paths[startIdx][endIdx][pathCounts[startIdx][endIdx]++] = tPath;

			boolean isTransfer = this.restorePathTimeAndImpedance(shortest, lastStation);
			// iterate through the neighboring nodes
			for (Station next : lastStation.getNeighbors()) {
				// if it creates a cycle continue
				if (shortest.getStations().contains(next)) {
					continue;
				}
				// create the new path
				Path newPath = new Path(shortest);
				Section section = lastStation.getSection(next);
				// skip successive transfer
				if (section.getDirection() == -1 && isTransfer) {
					continue;
				}
				if (section.getDirection() == -1) {
					// System.out.println(connect.getStart().getID() + "-" +
					// connect.getEnd().getID());
				} else {
					newPath.addTime(section.getTime());
					newPath.addImpedance(section.getImpedance());
				}
				newPath.addStation(next, section);
				// add the path
				pathQueue.add(newPath);

			}
		}
	}

	private boolean restorePathTimeAndImpedance(Path shortest, Station lastStation) {
		boolean isTransfer = false;
		int time = 0;
		double impedance = 0;
		if (shortest.getStations().size() > 2) {// skip start when its transfer
			Station prev = shortest.getPrevStation(lastStation);
			if (prev.getSection(lastStation).getDirection() == -1) {
				isTransfer = true;
				time = prev.getSection(lastStation).getTime();
				shortest.setTime(shortest.getTime() + time);
				impedance = prev.getSection(lastStation).getImpedance();
				shortest.setImpedance(shortest.getImpedance() + impedance);
			}
		}
		return isTransfer;
	}
}
