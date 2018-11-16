package cn.com.bjjdsy.ksp.entity;

/**
 * Used to store the basic information about an entire line
 *
 */
public class Line implements Comparable<Line> {
	// ID and name of the track
	private int code;
	private String name;

	/**
	 * Constructor for the Track
	 */
	public Line() {
		setCode(0);
		setName("");
	}

	/**
	 * Constructor for the Track, given inputs
	 * 
	 * @param _code ID of the track
	 * @param _name Name of the track
	 */
	public Line(int _code, String _name) {
		setCode(_code);
		setName(_name);
	}

	/**
	 * Gets the ID of the track
	 * 
	 * @return The ID of the track
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Sets the ID of the track
	 * 
	 * @param ID new ID of the track
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * Gets the name of the track
	 * 
	 * @return name of the track
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the track
	 * 
	 * @param name new name of the track
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(Line t) {
		return code - t.code;
	}

	@Override
	public boolean equals(Object o) {
		// if this is not a Station return false
		if (!(o instanceof Line))
			return false;

		// cast it to a Line
		Line other = (Line) o;

		// compare the IDs
		return other.code == this.code;
	}
}
