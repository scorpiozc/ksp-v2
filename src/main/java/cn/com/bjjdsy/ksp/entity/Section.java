package cn.com.bjjdsy.ksp.entity;

/**
 * A short section that connects two stations Can exist strictly inside a
 * station
 *
 */
public class Section {
	// start and end stations
	private Station start;
	private Station end;

	// which Track this is part of
	private Line track;

	// ID of this section and the direction
	private int ID;
	private int direction; // if it's -1 then it's between stations

	// Distance and time for the section
	private int dist;
	private int time;

	private double impedance;

	public double getImpedance() {
		return impedance;
	}

	public void setImpedance(double impedance) {
		this.impedance = impedance;
	}

	/**
	 * Default constructor
	 */
	public Section() {
		this(null, null, null, -1, -1, -1, -1, -1);
	}

	/**
	 * Constructor for a line
	 * 
	 * @param start Start station
	 * @param end   End station
	 * @param track Track this belongs to
	 * @param ID    ID of this section
	 * @param dir   Direction of this section
	 * @param dist  Distance from start to end
	 * @param time  Time it takes from start to end
	 */
	public Section(Station start, Station end, Line track, int ID, int direction, int dist, int time,
			double impedance) {
		this.setStart(start);
		this.setEnd(end);
		this.setTrack(track);
		this.setID(ID);
		this.setDirection(direction);
		this.setDist(dist);
		this.setTime(time);
		this.setImpedance(impedance);
	}

	/**
	 * @return the start
	 */
	public Station getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(Station start) {
		this.start = start;
	}

	/**
	 * @return the end
	 */
	public Station getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd(Station end) {
		this.end = end;
	}

	/**
	 * @return the track
	 */
	public Line getTrack() {
		return track;
	}

	/**
	 * @param track the track to set
	 */
	public void setTrack(Line track) {
		this.track = track;
	}

	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @param iD the iD to set
	 */
	public void setID(int iD) {
		ID = iD;
	}

	/**
	 * @return the dir
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * @param dir the dir to set
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * @return the dist
	 */
	public int getDist() {
		return dist;
	}

	/**
	 * @param dist the dist to set
	 */
	public void setDist(int dist) {
		this.dist = dist;
	}

	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}

	@Override
	public boolean equals(Object o) {
		// if this is not a Station return false
		if (!(o instanceof Section))
			return false;

		// cast it to a Line
		Section other = (Section) o;

		// compare the IDs
		return other.ID == this.ID;
	}
}
