package textRelayProcessor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TextProcessorUtils {
	public static String[] HEADER = { "date", "name", "personId", "batG", "batGs", "batSb", "batCs", "batR", "batRbi",
			"batPitches", "batOrder", "batPa", "batAb", "batHit", "batHit2b", "batHit3b", "batHr", "batBb", "batHbp",
			"batSo", "batSh", "batSf", "batGdp", "batFo", "batGo", "pitG", "pitGs", "pitIp", "pitR", "pitEr", "pitWp",
			"pitPitches", "pitSb", "pitCs", "pitPa", "pitAb", "pitHit", "pitHit2b", "pitHit3b", "pitHr", "pitBb",
			"pitHbp", "pitSo", "pitSh", "pitSf", "pitGdp", "pitFo", "pitGo", "vFF", "vTF", "vSI", "vCT", "vCU", "vSL",
			"vCH", "vFO", "vSF", "vKN", "cFF", "cTF", "cSI", "cCT", "cCU", "cSL", "cCH", "cFO", "cSF", "cKN" };

	public static String[] PA_HEADER = { "date", "name", "personId", "pitchType", "PA", "AB", "1B", "2B", "3B", "HR",
			"BB", "HBP", "SO", "GDP", "GO", "FO", "SH", "SF", "B", "S", "T", "F", "H", "FB", "FS", "FT", "FF", "FH",
			"A2B", "A2S", "A2T", "A2F", "A2H" };

	public static String[] BATTED_BALL_RESULT_HEADER = { "date", "name", "personId", "BUNT_CNT", "BUNT_HIT", "BUNT_FO",
			"BUNT_GO", "IF_CNT", "IF_HIT", "IF_FO", "IF_GO", "OF_CNT", "OF_HIT", "OF_FO", "OF_GO" };

	public static List<String> getJson(String dirPath) {
		File dir = new File(dirPath);
		File files[] = dir.listFiles();

		List<String> fileList = new ArrayList<>();

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				fileList.addAll(getJson(file.getPath()));
			} else {
				fileList.add(file.getPath());
			}
		}

		return fileList;
	}

	public static List<BaseballRecord> getRecordList(JSONObject lineUp, boolean isBatter) {
		JSONArray player = isBatter ? (JSONArray) lineUp.get("batter") : (JSONArray) lineUp.get("pitcher");

		return IntStream.range(0, player.size()).mapToObj(i -> player.get(i))
				.map(j -> new BaseballRecord((JSONObject) j, isBatter)).collect(Collectors.toList());
	}

	public static Map<String, Integer> SIMPLE_PA_MAP;

	static {
		SIMPLE_PA_MAP = new HashMap<>();

		SIMPLE_PA_MAP.put("PA", 0);
		SIMPLE_PA_MAP.put("AB", 0);
		SIMPLE_PA_MAP.put("1B", 0);
		SIMPLE_PA_MAP.put("2B", 0);
		SIMPLE_PA_MAP.put("3B", 0);
		SIMPLE_PA_MAP.put("HR", 0);
		SIMPLE_PA_MAP.put("BB", 0);
		SIMPLE_PA_MAP.put("HBP", 0);
		SIMPLE_PA_MAP.put("SO", 0);
		SIMPLE_PA_MAP.put("GDP", 0);
		SIMPLE_PA_MAP.put("GO", 0);
		SIMPLE_PA_MAP.put("FO", 0);
		SIMPLE_PA_MAP.put("SH", 0);
		SIMPLE_PA_MAP.put("SF", 0);
	}

	public static Map<String, Integer> BATTED_BALL_MAP;

	static {
		BATTED_BALL_MAP = new HashMap<>();

		BATTED_BALL_MAP.put("CNT", 0);
		BATTED_BALL_MAP.put("HIT", 0);
		BATTED_BALL_MAP.put("FO", 0);
		BATTED_BALL_MAP.put("GO", 0);
	}

	public static double getAvg(Map<String, Integer> map) {
		double ab = map.get("AB");

		if (ab == 0)
			return 0;

		double avg = (map.get("1B") + map.get("2B") + map.get("3B") + map.get("HR")) / ab;

		return avg;
	}

	public static double getObp(Map<String, Integer> map) {
		double divisor = map.get("AB") + map.get("BB") + map.get("HBP") + map.get("SF");

		if (divisor == 0)
			return 0;

		double obp = (map.get("1B") + map.get("2B") + map.get("3B") + map.get("HR") + map.get("BB") + map.get("HBP"))
				/ divisor;

		return obp;
	}

	public static double getSlg(Map<String, Integer> map) {
		double ab = map.get("AB");

		if (ab == 0)
			return 0;

		double slg = (map.get("1B") + map.get("2B") * 2 + map.get("3B") * 3 + map.get("HR") * 4) / ab;

		return slg;
	}

	public static String getOps(Map<String, Integer> map) {
		if (map == null)
			return "0.000";

		return String.format("%.3f", getObp(map) + getSlg(map));
	}
}
