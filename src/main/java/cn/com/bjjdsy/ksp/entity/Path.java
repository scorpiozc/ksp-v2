package cn.com.bjjdsy.ksp.entity;

import java.util.*;

public class Path implements Comparable<Path> {
	private ArrayList<Station> stations;
	private ArrayList<Section> sections;
	private int time;

	public void setTime(int time) {
		this.time = time;
	}

	private double impedance;

	public void setImpedance(double impedance) {
		this.impedance = impedance;
	}

	public double getImpedance() {
		return impedance;
	}

	public void addImpedance(double impedance) {
		this.impedance += impedance;
	}

	/**
	 * Default constructor
	 */
	public Path() {
		time = 0;
		impedance = 0;
		stations = new ArrayList<Station>();
		sections = new ArrayList<Section>();
	}

	/**
	 * Constructor copying from a path
	 * 
	 * @param Path to be copied from
	 */
	public Path(Path p) {
		time = p.time;
		impedance = p.impedance;
		stations = new ArrayList<Station>();
		sections = new ArrayList<Section>();
		for (Station s : p.stations)
			stations.add(s);
		for (Section l : p.sections)
			sections.add(l);
	}

	/**
	 * Add the source station
	 * 
	 * @param source The source station
	 */
	public void addStation(Station source) {
		if (source == null)
			throw new IllegalArgumentException();

		// add the station
		stations.add(source);
	}

	/**
	 * Add a station
	 * 
	 * @param add     Station to be added
	 * @param section Line that connects this station to the previous station
	 */
	public void addStation(Station add, Section section) {
		if (add == null)
			throw new IllegalArgumentException();

//		if (stations.contains(add))
//		{
//			// stations.add(add);
//			// lines.add(line);
//			return true;
//		}
//
		// add the station
		stations.add(add);
		sections.add(section);
//		return false;
	}

	/**
	 * 
	 * @return The stations in the path
	 */
	public ArrayList<Station> getStations() {
		return stations;
	}

	/**
	 * 
	 * @return The lines in the path
	 */
	public ArrayList<Section> getSections() {
		return sections;
	}

	/**
	 * Get the end station
	 * 
	 * pre: there is at least one station
	 * 
	 * @return The end station
	 */
	public Station getEndStation() {
		if (stations.size() == 0)
			throw new IllegalArgumentException();

		return stations.get(stations.size() - 1);
	}

	public Station getPrevStation(Station s) {
		int idx = stations.indexOf(s);
		return stations.get(idx - 1);
	}

	/**
	 * Add more distance
	 * 
	 * @param add distance to be added
	 */
	public void addTime(int time) {
		time += time;
	}

	/**
	 * 
	 * @return Distance of the path
	 */
	public int getTime() {
		return time;
	}

	@Override
	public int compareTo(Path other) {
		// compare the dist
//		if (other.dist != dist)
//			return dist - other.dist;

		// compare the impedance
		if (other.impedance != impedance) {
			return (int) (impedance - other.impedance);
		}

		// compare the number of stations
		return stations.size() - other.stations.size();
	}

	@Override
	public boolean equals(Object o) {
		// check the instance of the other object and cast
		if (!(o instanceof Path))
			return false;

		Path other = (Path) o;

		// compare the dist
		if (other.time != time)
			return false;

		// compare the number of stations
		if (other.stations.size() != stations.size())
			return false;

		// compare the stations themselves
		for (int i = 0; i < stations.size(); ++i)
			if (!other.stations.get(i).equals(stations.get(i)))
				return false;

		return true;
	}
}
