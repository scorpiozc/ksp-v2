package cn.com.bjjdsy.ksp.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import cn.com.bjjdsy.ksp.entity.Line;
import cn.com.bjjdsy.ksp.entity.Station;

public class CalcConstant {

	public static final String STATION_INFO = "station_base_info.txt";
	public static final String LINE_INFO = "line_base_info.txt";
	public static final String SECTION_INFO = "section_base_info.txt";
	public static final String TRANSFER_INFO = "transfer_line_walktime_info.txt";
	public static final String TRANSFER_BASE_INFO = "transferstation_base_info.txt";
	public static final String STATION_PARKTIME = "station_parktime.txt";
	public static final String LINE_DEPART_INTERVAL_TIME = "line_depart_interval_time.txt";

	// output filenames
	public static final String OUTPUT = "shortest_distances_";
	public static final int MAX_LINE = 100;
	public static final int MAX_STATION = 10000;
	private static final boolean PATHOUTPUT = true;
	private static final boolean DEPARTALPHAON = false;

	public static Map<Integer, String> STATION_DICT = new TreeMap<>();
	public static Map<Integer, String> LINE_DICT = new HashMap<>();
	public static Map<String, Integer> PARKTIMES = new HashMap<>();
	public static Map<Integer, Integer> DEPARTINTERVALTIMES = new HashMap<>();
	public static Station[] STATIONS;
	public static Line[] LINES;
}
