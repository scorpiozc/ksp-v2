package cn.com.bjjdsy.ksp.calc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

import cn.com.bjjdsy.ksp.entity.Section;
import cn.com.bjjdsy.ksp.entity.Path;
import cn.com.bjjdsy.ksp.entity.Station;
import cn.com.bjjdsy.ksp.entity.Line;

public class Graph {
	// input filenames, will change later
	public static final String STATION_INFO = "station_base_info.txt";
	public static final String LINE_INFO = "line_base_info.txt";
	public static final String SECTION_INFO = "section_base_info.txt";
	public static final String TRANSFER_INFO = "transfer_line_walktime_info.txt";
	public static final String TRANSFER_BASE = "transferstation_base_info.txt";
	public static final String STATION_PARKTIME = "station_parktime.txt";
	public static final String LINE_DEPART_INTERVAL_TIME = "line_depart_interval_time.txt";

	// output filenames
	public static final String OUTPUT = "shortest_distances_";

	public static final int MAXLINES = 100;
	public static final int MAXSTATION = 10000;

	private static final boolean PATHOUTPUT = true;

	// total number of stations and lines
	private int stations;
	private int lines;

	private Line[] tracks;
	private Station[] stats;
	private int[] statIDs;
	private int[] indices;
	private HashMap<Integer, ArrayList<Integer>> transferToID;

	// the generated shortest paths
	private Path paths[][][];
	private int pathCounts[][];

	private Map<String, Integer> parktimes;
	private Map<Integer, Integer> departIntervalTimes;
	private boolean departAlphaOn = false;

	public Graph() {
		stations = 0;
		lines = 0;

		tracks = new Line[MAXLINES];
		stats = new Station[MAXSTATION];
		statIDs = new int[MAXSTATION];
		indices = new int[MAXSTATION];

		for (int i = 0; i < MAXSTATION; i++)
			indices[i] = -1;

		transferToID = new HashMap<Integer, ArrayList<Integer>>();
		parktimes = new HashMap<>();
		departIntervalTimes = new HashMap<>();
	}

	/**
	 * Read in the graph with the given text files
	 */
	public void readGraph() {
		TreeMap<Integer, String> names = new TreeMap<Integer, String>();
		HashMap<Integer, String> lineNames = new HashMap<Integer, String>();

		// read in all the park time
		readPark();

		// read in all the depart interval time
		readDepart();

		// read in all the information
		readTransferBase();
		readStation(names);

		// read all the lines
		readLine(lineNames);

		// generate all the tracks
		genTracks(lineNames);

		// generate the stations
		genStations(names);

		// read in the all the sections
		readSection();

		// read in the transfer information
		readTransfer();
	}

	/**
	 * Generate the stations
	 * 
	 * @param names Names of the stations
	 */
	private void genStations(TreeMap<Integer, String> names) {
		// iterate through the stations and add the stations
		for (Integer i : names.keySet()) {
			indices[i] = stations;
			statIDs[stations++] = i;

			stats[i] = new Station(i, null, names.get(i));
		}
	}

	/**
	 * Generates the tracks
	 * 
	 * @param lineNames
	 */
	private void genTracks(HashMap<Integer, String> lineNames) {
		// iterate through the tracks and generate them
		for (Integer i : lineNames.keySet()) {
			tracks[i] = new Line(i, lineNames.get(i));
		}
	}

	/**
	 * Reads in information for the stations
	 * 
	 * @param names The names of the stations corresponded to the ID
	 */
	private void readStation(TreeMap<Integer, String> names) {
		BufferedReader fin = null;

		// initialize the reader
		try {
			fin = new BufferedReader(new FileReader(new File(STATION_INFO)));
			fin.readLine();
			fin.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			String line = null;

			// read in the input
			try {
				line = fin.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// if no input, break out
			if (line == null || line.length() == 0)
				break;

			String[] data = line.split(",");

			// parse the data
			int stationCode = Integer.parseInt(data[0]);
			String name = data[1];

			// add the name
			names.put(stationCode, name);
		}

		try {
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read in all the information about lines
	 * 
	 * @param lineNames The names of the lines
	 */
	private void readLine(HashMap<Integer, String> lineNames) {
		BufferedReader fin = null;

		// initialize the reader
		try {
			fin = new BufferedReader(new FileReader(new File(LINE_INFO)));
			fin.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			String line = null;

			// read in the input
			try {
				line = fin.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// if no input, break out
			if (line == null || line.length() == 0)
				break;

			String[] data = line.split(",");

			int lineCode = Integer.parseInt(data[0]);
			String lineName = data[1];

			lineNames.put(lineCode, lineName);
		}

		try {
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read in the data for the section
	 */
	private void readSection() {
		BufferedReader fin = null;

		// initialize the reader
		try {
			fin = new BufferedReader(new FileReader(new File(SECTION_INFO)));
			fin.readLine();
			fin.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			String line = null;

			// read in the input
			try {
				line = fin.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// if no input, break out
			if (line == null || line.length() == 0)
				break;

			String[] data = line.split(",");

			// parse the data
			int lineID = Integer.parseInt(data[0]);
			int s1 = Integer.parseInt(data[1]);
			int s2 = Integer.parseInt(data[2]);
			int dist = Integer.parseInt(data[3]);
			int time = Integer.parseInt(data[4]);
			int dir = Integer.parseInt(data[5]);

			// check that the stations are valid
			if (stats[s1].getLine() != null && stats[s1].getLine().getCode() != lineID)
				throw new IllegalArgumentException("Input first station is conflicting.");
			if (stats[s2].getLine() != null && stats[s2].getLine().getCode() != lineID)
				throw new IllegalArgumentException("Input first station is conflicting.");

			// set the tracks that they are on
			stats[s1].setLine(tracks[lineID]);
			stats[s2].setLine(tracks[lineID]);

			// create a new Line
			int traveltime = time + (parktimes.get(s1 + "-" + s2) == null ? 0 : parktimes.get(s1 + "-" + s2));
			double impedance = traveltime;
			Section connect = new Section(stats[s1], stats[s2], tracks[lineID], lines++, dir, dist, traveltime, impedance);
//			System.out.printf("s1:%s-s2:%s\n", s1, s2);
			stats[s1].addSection(connect);
			stats[s2].addSection(connect);
		}

		try {
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads in the data for the walk times between the stations
	 * 
	 * @param encode
	 * @param decode
	 */
	private void readTransfer() {
		Map<Integer, Integer> specials = new HashMap<>();
		specials.put(1001, 9069);
		specials.put(9069, 1001);
		specials.put(1043, 9021);
		specials.put(9021, 1043);
		specials.put(467, 9319);
		specials.put(9319, 467);

		BufferedReader fin = null;
		final double departWeight = 0.5;

		// initialize the reader
		try {
			fin = new BufferedReader(new FileReader(new File(TRANSFER_INFO)));
			fin.readLine();
			fin.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			String line = null;

			// read in the input
			try {
				line = fin.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// if no input, break out
			if (line == null || line.length() == 0)
				break;

			// extract the station code and transfer code
			String[] data = line.split(",");
			int tsCode = Integer.parseInt(data[0]);
			int start = Integer.parseInt(data[1]);
			int end = Integer.parseInt(data[2]);
			int time = Integer.parseInt(data[3]);

			// find the start and end station
			Station startStation = null, endStation = null;
			for (int code : transferToID.get(tsCode)) {
				if ((stats[code].getLine().getCode()) == start)
					startStation = stats[code];
			}
			for (int code : transferToID.get(tsCode)) {
				if ((stats[code].getLine().getCode()) == end)
					endStation = stats[code];
			}

			// make the new line
			int traveltime;
			double impedance;
			if (specials.get(startStation.getCode()) != null
					&& specials.get(startStation.getCode()) == endStation.getCode()) {
				traveltime = 0;
				impedance = 0;
			} else {
				traveltime = time + (int) (departIntervalTimes.get(end) * departWeight);
				impedance = (time + departIntervalTimes.get(end) * (departAlphaOn ? departWeight : 1)) * 1.5;
			}
//			System.out.printf("end:%d %d\n", end, departIntervalTimes.get(end));
			Section connect = new Section(startStation, endStation, null, lines++, -1, 0, traveltime, impedance);
			startStation.addSection(connect);
			endStation.addSection(connect);
		}

		try {
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read the corresponding conversions from the transfer station to the actual
	 * station
	 */
	private void readTransferBase() {
		BufferedReader fin = null;

		// initialize the reader
		try {
			fin = new BufferedReader(new FileReader(new File(TRANSFER_BASE)));
			fin.readLine();
			fin.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			String line = null;

			// read in the input
			try {
				line = fin.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// if no input, break out
			if (line == null || line.length() == 0)
				break;

			// extract the station code and transfer code
			String[] data = line.split(",");
			int station = Integer.parseInt(data[1]);
			int transferCode = Integer.parseInt(data[0]);

			// put the code in
			if (transferToID.get(transferCode) == null)
				transferToID.put(transferCode, new ArrayList<Integer>());

			// add the station
			transferToID.get(transferCode).add(station);
		}

		try {
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readPark() {
		BufferedReader fin = null;
		try {
			fin = new BufferedReader(new FileReader(new File(STATION_PARKTIME)));
			fin.readLine();
			fin.readLine();
			while (true) {
				String line = null;
				line = fin.readLine();
				if (line == null || line.length() == 0)
					break;

				String[] data = line.split(",");
				int start = Integer.parseInt(data[1]);// start station
				int direct = Integer.parseInt(data[2]);// direct
				int stop = Integer.parseInt(data[3]);// stop station
				int parktime = Integer.parseInt(data[4]);// parktime
				parktimes.put(start + "-" + stop, parktime);
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readDepart() {
		BufferedReader fin = null;

		// initialize the reader
		try {
			fin = new BufferedReader(new FileReader(new File(LINE_DEPART_INTERVAL_TIME)));
			fin.readLine();
			fin.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			String line = null;

			// read in the input
			try {
				line = fin.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// if no input, break out
			if (line == null || line.length() == 0)
				break;

			// extract the station code and transfer code
			String[] data = line.split(",");
			int lineCode = Integer.parseInt(data[0]);
			int departIntervalTime = Integer.parseInt(data[2]);

			// add the depart
			departIntervalTimes.put(lineCode, departIntervalTime * 60);
		}
	}

	/**
	 * @return the stations
	 */
	public int getStations() {
		return stations;
	}

	/**
	 * Generate all shortest paths
	 * 
	 * @param number Generate all the shortest "number" paths
	 */
	public void getPaths(int number) {
		// initialize paths and pathcounts
		paths = new Path[stations][stations][number];
		pathCounts = new int[stations][stations];// 0

		PrintWriter fout = null;
		try {
			fout = new PrintWriter(new File("multiDijkstra_process.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < stations; ++i) {
			multiDijkstra(i, number, fout);
		}

//		int ii = 0, jj = 0, k = 0;
//		for (Path[][] p2 : paths) {
//			fout.printf("K:%d\n", k);
//			for (Path[] p1 : p2) {
//				fout.printf("O:%d\n", jj++);
//				for (Path p : p1) {
//					fout.printf("D:%d", ii++);
//					if (p != null)
//						for (Station s : p.getStations()) {
//							if (s != null)
//								fout.printf("|%d", s.getID());
//						}
//					fout.println();
//				}
//			}
//			fout.println();
//		}
		fout.close();
	}

	/**
	 * Get all the shortest paths starting from a start
	 * 
	 * @param start  The starting node
	 * @param number The number of paths
	 */
	private void multiDijkstra(int start, int number, PrintWriter fout) {

		ArrayList<Section> lines = new ArrayList<>();
		// set up the source
		Station source = stats[statIDs[start]];

//		fout.printf("source station:%d\n", source.getID());

		// create the priority queue and the counts
		PriorityQueue<Path> enqueue = new PriorityQueue<Path>();

		// create the first path
		Path begin = new Path();
		begin.addStation(source);

		// add the first paths
		enqueue.add(begin);

		// iterate through possible paths
		while (!enqueue.isEmpty()) {

//			fout.println("enqueue.size:" + enqueue.size());

			// get the best path and last station Prev
			Path best = enqueue.poll();
			Station last = best.getEndStation();
			int end = indices[last.getCode()];

			if (pathCounts[start][end] == 1) {
//				fout.printf("K==1 start,%d,end,%d\n", start, end);
			}

			// if enough paths have been found continue
//			fout.printf("start:%d,station:%d,end:%d,station:%d,K:%d\n", start, statIDs[start], end, last.getID(),
//					pathCounts[start][end]);
			if (pathCounts[start][end] >= number)
				continue;

//			for (Station s : last.getLines().keySet()) {
//				System.out.println(last.getID() + " " + last.getLines().get(s).getStart().getID());
//			}

			// ---
			Path tPath = new Path(best);
			paths[start][end][pathCounts[start][end]++] = tPath;

			boolean isTrans = false;
			int time = 0;
			double impedance = 0;
			if (best.getStations().size() > 2) {// skip start when its transfer
				Station prev = best.getPrevStation(last);
				if (prev.getSection(last).getDirection() == -1) {
					isTrans = true;

					time = prev.getSection(last).getTime();
					best.setTime(best.getTime() + time);
					impedance = prev.getSection(last).getImpedance();
					best.setImpedance(best.getImpedance() + impedance);
//					paths[start][end][pathCounts[start][end]++] = tPath;
				} else {
//					paths[start][end][pathCounts[start][end]++] = best;
				}

			}

			// add the path
//			paths[start][end][pathCounts[start][end]++] = best;

			best.getStations().forEach(s -> {
//				fout.printf("|%s", (s.getID()));
			});
//			fout.println();

			// iterate through the neighboring nodes
//			System.out.println("Neighbors:" + last.getNeighbors().size());
			for (Station next : last.getNeighbors()) {

//				fout.printf("station %d neighbors:%d\n", last.getID(), next.getID());

				// if it creates a cycle continue
				if (best.getStations().contains(next))
					continue;

				// create the new path
				Path newPath = new Path(best);

				Section connect = last.getSection(next);
				// ---
				if (connect.getDirection() == -1 && isTrans) {
					
					newPath.setTime(newPath.getTime() - time);
					newPath.setImpedance(newPath.getImpedance() - impedance);
					continue;
				}
				// ---

				//
				int before = newPath.getTime();
				if (connect.getDirection() == -1) {
					// System.out.println(connect.getStart().getID() + "-" +
					// connect.getEnd().getID());
				} else {
					newPath.addTime(connect.getTime());
					newPath.addImpedance(connect.getImpedance());
				}
				newPath.addStation(next, connect);
				int after = newPath.getTime();
				if (before == after) {
//					System.out.println(before + "-" + after);
				}
				// add the path
				enqueue.add(newPath);

			}

//			enqueue.forEach(p -> {
//				p.getStations().forEach(s -> {
//					fout.printf("|%s", s.getID());
//				});
//				fout.printf("|%s", p.getImpedance());
//				fout.println();
//			});
		}

	}

	/**
	 * Print the paths to the output
	 */
	public void printPaths() {
		PrintWriter fout = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			fout = new PrintWriter(new File(OUTPUT + LocalDateTime.now().format(formatter) + ".txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		StringBuffer routeStationCode = new StringBuffer();
		StringBuffer routeLineCode = new StringBuffer();
		// iterate through all endpoints
		for (int start = 0; start < stations; ++start)
			for (int end = 0; end < stations; ++end)
				if (start != end) {
					// output the number of paths and the distance
//					fout.printf("Paths from %d (%s) to %d (%s): %d\n", statIDs[start], stats[statIDs[start]].getName(),
//							statIDs[end], stats[statIDs[end]].getName(), pathCounts[start][end]);

					// output the paths themselves
					if (PATHOUTPUT) {
						// go through each path and print it out
						for (int i = 0; i < pathCounts[start][end]; ++i) {
							Path cur = paths[start][end][i];
							ArrayList<Station> stations = cur.getStations();
							ArrayList<Section> lines = cur.getSections();

							List<Integer> tList = new ArrayList<>();
							// fout.printf("Path %d: %d seconds\n", i + 1, cur.getDist());
							for (int j = 0; j < lines.size(); ++j) {

								// adjust time when OorD is transfer
								if (j == 0 && lines.get(0).getDirection() == -1) {
									int time = lines.get(0).getTime();
//									cur.setDist(cur.getDist() - time);
									double impedance = lines.get(0).getImpedance();
//									cur.setImpedance(cur.getImpedance() - impedance);
								}
								if (j == lines.size() - 1 && lines.get(lines.size() - 1).getDirection() == -1) {
									int time = lines.get(lines.size() - 1).getTime();
//									cur.setDist(cur.getDist() - time);
									double impedance = lines.get(lines.size() - 1).getImpedance();
//									cur.setImpedance(cur.getImpedance() - impedance);
								}

								// adjust end

								if (lines.get(j).getDirection() == -1) {
//									fout.printf("Walk to ");
									if (j != 0 && j != lines.size() - 1) {
										// routeLineCode.append(lines.get(j).getStart().getLine().getID() + "-");
										tList.add(j);
									}
								} else {

								}
//									fout.printf("Take Line %d (%s) to ", lines.get(j).getTrack().getID(),
//											lines.get(j).getTrack().getName());
//
//								fout.printf("%d (%s)\n", stations.get(j + 1).getID(), stations.get(j + 1).getName());

								routeStationCode.append(stations.get(j).getCode() + "-");
							}
							routeStationCode.append(stations.get(lines.size()).getCode());
							if (tList.isEmpty()) {
								routeLineCode.append(stations.get(lines.size()).getLine().getCode());
							} else {
								for (int t : tList) {
									routeLineCode.append(lines.get(t).getStart().getLine().getCode() + "-");
								}
								routeLineCode.append(stations.get(tList.get(tList.size() - 1) + 1).getLine().getCode());
							}
							fout.printf("%s,%s,%d,%s,%s,%d,%.2f\n", statIDs[start], statIDs[end], i + 1, routeLineCode,
									routeStationCode, cur.getTime(), cur.getImpedance() / 60);
							routeStationCode.setLength(0);
							routeLineCode.setLength(0);
						}
//						fout.println();
					}
					// print

				}

		fout.close();
	}

	/**
	 * Print path from start to end
	 * 
	 * @param start Start point
	 * @param end   End point
	 */
	public void printPath(int start, int end) {
		PrintWriter fout = null;
		PriorityQueue<Path> pqueue = new PriorityQueue<Path>();

		String filename = start + "-" + end + ".txt";
		try {
			// fout = new PrintWriter(new File(OUTPUT));
			fout = new PrintWriter(new File(filename));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		start = indices[start];
		end = indices[end];

		if (start < 0 || end < 0)
			throw new IllegalArgumentException();

		// output the number of paths and the distance
//		fout.printf("Paths from %d (%s) to %d (%s): %d\n", statIDs[start], stats[statIDs[start]].getName(),
//				statIDs[end], stats[statIDs[end]].getName(), pathCounts[start][end]);

		StringBuffer routeStationCode = new StringBuffer();
		StringBuffer routeLineCode = new StringBuffer();
		// output the paths themselves
		if (PATHOUTPUT) {
			// go through each path and print it out
			for (int i = 0; i < pathCounts[start][end]; ++i) {
				Path cur = paths[start][end][i];
				ArrayList<Station> stations = cur.getStations();
				ArrayList<Section> lines = cur.getSections();

//				fout.printf("Path %d: %d seconds\n", i + 1, cur.getDist());

				for (int j = 0; j < lines.size(); ++j) {
					// adjust time when OorD is transfer
					if (j == 0 && lines.get(0).getDirection() == -1) {
						int time = lines.get(0).getTime();
//						cur.setDist(cur.getDist() - time);
						double impedance = lines.get(0).getImpedance();
//						cur.setImpedance(cur.getImpedance() - impedance);
					}
					if (j == lines.size() - 1 && lines.get(lines.size() - 1).getDirection() == -1) {
						int time = lines.get(lines.size() - 1).getTime();
						// cur.setDist(cur.getDist() - time);
						double impedance = lines.get(lines.size() - 1).getImpedance();
						// cur.setImpedance(cur.getImpedance() - impedance);
					}

					// adjust end
				}

				List<Integer> tList = new ArrayList<>();
				for (int j = 0; j < lines.size(); ++j) {

					if (lines.get(j).getDirection() == -1) {
//						fout.printf("Walk to ");
						if (j != 0 && j != lines.size() - 1) {
							// routeLineCode.append(lines.get(j).getStart().getLine().getID() + "-");
							tList.add(j);
						}
					} else {
//						fout.printf("Take Line %d (%s) to ", lines.get(j).getTrack().getID(),
//								lines.get(j).getTrack().getName());

					}

//					fout.printf("%d (%s)\n", stations.get(j + 1).getID(), stations.get(j + 1).getName());

					routeStationCode.append(stations.get(j).getCode() + "-");
				}
				routeStationCode.append(stations.get(lines.size()).getCode());

				if (tList.isEmpty()) {
					routeLineCode.append(stations.get(lines.size()).getLine().getCode());
				} else {
					for (int t : tList) {
						routeLineCode.append(lines.get(t).getStart().getLine().getCode() + "-");
					}
					routeLineCode.append(stations.get(tList.get(tList.size() - 1) + 1).getLine().getCode());
				}

				fout.printf("%s,%s,%d,%s,%s,%d,%.2f\n", statIDs[start], statIDs[end], i + 1, routeLineCode,
						routeStationCode, cur.getTime(), cur.getImpedance() / 60);
				routeStationCode.setLength(0);
				routeLineCode.setLength(0);
			}
//			fout.println();
		}

		fout.close();
	}

}
