package textRelayProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

public class BaseballRecord {
	String name;
	int personId;

	int batG;
	int batGs;
	int batSb;
	int batCs;
	int batR;
	int batRbi;
	int batPitches;
	int batOrder;
	int batPa;
	int batAb;
	int batHit;
	int batHit2b;
	int batHit3b;
	int batHr;
	int batBb;
	int batHbp;
	int batSo;
	int batSh;
	int batSf;
	int batGdp;
	int batFo;
	int batGo;

	int pitG;
	int pitGs;
	int pitIp;
	int pitR;
	int pitEr;
	int pitWp;
	int pitPitches;
	int pitSb;
	int pitCs;
	int pitPa;
	int pitAb;
	int pitHit;
	int pitHit2b;
	int pitHit3b;
	int pitHr;
	int pitBb;
	int pitHbp;
	int pitSo;
	int pitSh;
	int pitSf;
	int pitGdp;
	int pitFo;
	int pitGo;

	Map<String, List<Integer>> pitchTypeCount = new HashMap<>();
	Map<String, List<String>> pitchTypeResult = new HashMap<>();
	Map<String, List<String>> firstPitchResult = new HashMap<>();
	Map<String, List<String>> afterTwoStrikeResult = new HashMap<>();

	Map<String, Map<String, Integer>> pitchTypePaResult = new HashMap<>();
	Map<String, Map<String, Integer>> battedBallResult = new HashMap<>();

	public BaseballRecord(JSONObject object, boolean isBatter) {
		this.name = (String) object.get("name");
		this.personId = Integer.valueOf((String) object.get("pcode"));

		if (isBatter) {
			batG++;
			if (((String) object.get("cin")) == null)
				batGs++;
			batR = (int) (long) object.get("run");
			batRbi = (int) (long) object.get("rbi");
			batOrder = (int) (long) object.get("batOrder");
			batBb = (int) (long) object.get("bb");
			batAb = (int) (long) object.get("ab");
			batHr = (int) (long) object.get("hr");
			batHit = (int) (long) object.get("hit");
			batHbp = (int) (long) object.get("hbp");
			batSo = (int) (long) object.get("so");
		} else {
			pitG++;
			if (((int) (long) object.get("seqno")) == 1)
				pitGs++;
			pitR = (int) (long) object.get("run");
			pitEr = (int) (long) object.get("er");
			pitWp = (int) (long) object.get("wp");
			pitIp = convertIp(Double.valueOf((String) object.get("inn")));
			pitPitches = (int) (long) object.get("ballCount");

			pitBb = (int) (long) object.get("bb");
			pitHr = (int) (long) object.get("hr");
			pitHit = (int) (long) object.get("hit");
			pitHbp = (int) (long) object.get("hbp");
			pitSo = (int) (long) object.get("kk");
		}
	}

	int convertIp(double ip) {
		int integral = (int) Math.floor(ip);
		int fractional = (int) Math.round((ip - integral) * 10);

		return integral * 3 + fractional;
	}

	public String[] toString(int year, String filename) {

		String[] array = new String[78];

		array[0] = getDate(year, filename);
		array[1] = name;
		array[2] = String.valueOf(personId);
		array[3] = String.valueOf(batG);
		array[4] = String.valueOf(batGs);
		array[5] = String.valueOf(batSb);
		array[6] = String.valueOf(batCs);
		array[7] = String.valueOf(batR);
		array[8] = String.valueOf(batRbi);
		array[9] = String.valueOf(batPitches);
		array[10] = String.valueOf(batOrder);
		array[11] = String.valueOf(batPa);
		array[12] = String.valueOf(batAb);
		array[13] = String.valueOf(batHit);
		array[14] = String.valueOf(batHit2b);
		array[15] = String.valueOf(batHit3b);
		array[16] = String.valueOf(batHr);
		array[17] = String.valueOf(batBb);
		array[18] = String.valueOf(batHbp);
		array[19] = String.valueOf(batSo);
		array[20] = String.valueOf(batSh);
		array[21] = String.valueOf(batSf);
		array[22] = String.valueOf(batGdp);
		array[23] = String.valueOf(batFo);
		array[24] = String.valueOf(batGo);
		array[25] = String.valueOf(pitG);
		array[26] = String.valueOf(pitGs);
		array[27] = String.valueOf(pitIp);
		array[28] = String.valueOf(pitR);
		array[29] = String.valueOf(pitEr);
		array[30] = String.valueOf(pitWp);
		array[31] = String.valueOf(pitPitches);
		array[32] = String.valueOf(pitSb);
		array[33] = String.valueOf(pitCs);
		array[34] = String.valueOf(pitPa);
		array[35] = String.valueOf(pitAb);
		array[36] = String.valueOf(pitHit);
		array[37] = String.valueOf(pitHit2b);
		array[38] = String.valueOf(pitHit3b);
		array[39] = String.valueOf(pitHr);
		array[40] = String.valueOf(pitBb);
		array[41] = String.valueOf(pitHbp);
		array[42] = String.valueOf(pitSo);
		array[43] = String.valueOf(pitSh);
		array[44] = String.valueOf(pitSf);
		array[45] = String.valueOf(pitGdp);
		array[46] = String.valueOf(pitFo);
		array[47] = String.valueOf(pitGo);

		array[48] = getCountAvg("직구");
		array[49] = getCountAvg("투심");
		array[50] = getCountAvg("싱커");
		array[51] = getCountAvg("커터");
		array[52] = getCountAvg("커브");
		array[53] = getCountAvg("슬라이더");
		array[54] = getCountAvg("체인지업");
		array[55] = getCountAvg("포크");
		array[56] = getCountAvg("스플리터");
		array[57] = getCountAvg("너클볼");

		array[58] = getPitchCount("직구");
		array[59] = getPitchCount("투심");
		array[60] = getPitchCount("싱커");
		array[61] = getPitchCount("커터");
		array[62] = getPitchCount("커브");
		array[63] = getPitchCount("슬라이더");
		array[64] = getPitchCount("체인지업");
		array[65] = getPitchCount("포크");
		array[66] = getPitchCount("스플리터");
		array[67] = getPitchCount("너클볼");

		return array;
	}

	public String[] paResultToString(int year, String filename, String pitchType) {
		Map<String, Integer> paResultMap = pitchTypePaResult.get(pitchType);
		Map<String, Long> typeResult = pitchTypeResult.get(pitchType).stream()
				.collect(Collectors.groupingBy(p -> (String) p, Collectors.counting()));
		Map<String, Long> firstTypeResult = firstPitchResult.getOrDefault(pitchType, Collections.emptyList()).stream()
				.collect(Collectors.groupingBy(p -> (String) p, Collectors.counting()));
		Map<String, Long> afterTsResult = afterTwoStrikeResult.getOrDefault(pitchType, Collections.emptyList()).stream()
				.collect(Collectors.groupingBy(p -> (String) p, Collectors.counting()));

		if (paResultMap.isEmpty())
			return null;

		String[] array = new String[33];

		array[0] = getDate(year, filename);
		array[1] = name;
		array[2] = String.valueOf(personId);
		array[3] = pitchType;
		array[4] = String.valueOf(paResultMap.get("PA"));
		array[5] = String.valueOf(paResultMap.get("AB"));
		array[6] = String.valueOf(paResultMap.get("1B"));
		array[7] = String.valueOf(paResultMap.get("2B"));
		array[8] = String.valueOf(paResultMap.get("3B"));
		array[9] = String.valueOf(paResultMap.get("HR"));
		array[10] = String.valueOf(paResultMap.get("BB"));
		array[11] = String.valueOf(paResultMap.get("HBP"));
		array[12] = String.valueOf(paResultMap.get("SO"));
		array[13] = String.valueOf(paResultMap.get("GDP"));
		array[14] = String.valueOf(paResultMap.get("GO"));
		array[15] = String.valueOf(paResultMap.get("FO"));
		array[16] = String.valueOf(paResultMap.get("SH"));
		array[17] = String.valueOf(paResultMap.get("SF"));

		array[18] = String.valueOf(typeResult.getOrDefault("B", 0L));
		array[19] = String.valueOf(typeResult.getOrDefault("S", 0L));
		array[20] = String.valueOf(typeResult.getOrDefault("T", 0L));
		array[21] = String.valueOf(typeResult.getOrDefault("F", 0L));
		array[22] = String.valueOf(typeResult.getOrDefault("H", 0L));

		array[23] = String.valueOf(firstTypeResult.getOrDefault("B", 0L));
		array[24] = String.valueOf(firstTypeResult.getOrDefault("S", 0L));
		array[25] = String.valueOf(firstTypeResult.getOrDefault("T", 0L));
		array[26] = String.valueOf(firstTypeResult.getOrDefault("F", 0L));
		array[27] = String.valueOf(firstTypeResult.getOrDefault("H", 0L));

		array[28] = String.valueOf(afterTsResult.getOrDefault("B", 0L));
		array[29] = String.valueOf(afterTsResult.getOrDefault("S", 0L));
		array[30] = String.valueOf(afterTsResult.getOrDefault("T", 0L));
		array[31] = String.valueOf(afterTsResult.getOrDefault("F", 0L));
		array[32] = String.valueOf(afterTsResult.getOrDefault("H", 0L));

		return array;
	}

	public String[] battedBallResultToString(int year, String filename) {
		Map<String, Integer> buntResult = battedBallResult.getOrDefault("BUNT", Collections.emptyMap());
		Map<String, Integer> ifResult = battedBallResult.getOrDefault("IF", Collections.emptyMap());
		Map<String, Integer> ofResult = battedBallResult.getOrDefault("OF", Collections.emptyMap());

		if (buntResult.isEmpty() && ifResult.isEmpty() && ofResult.isEmpty())
			return null;

		String[] array = new String[15];

		array[0] = getDate(year, filename);
		array[1] = name;
		array[2] = String.valueOf(personId);

		array[3] = String.valueOf(buntResult.getOrDefault("CNT", 0));
		array[4] = String.valueOf(buntResult.getOrDefault("HIT", 0));
		array[5] = String.valueOf(buntResult.getOrDefault("FO", 0));
		array[6] = String.valueOf(buntResult.getOrDefault("GO", 0));

		array[7] = String.valueOf(ifResult.getOrDefault("CNT", 0));
		array[8] = String.valueOf(ifResult.getOrDefault("HIT", 0));
		array[9] = String.valueOf(ifResult.getOrDefault("FO", 0));
		array[10] = String.valueOf(ifResult.getOrDefault("GO", 0));

		array[11] = String.valueOf(ofResult.getOrDefault("CNT", 0));
		array[12] = String.valueOf(ofResult.getOrDefault("HIT", 0));
		array[13] = String.valueOf(ofResult.getOrDefault("FO", 0));
		array[14] = String.valueOf(ofResult.getOrDefault("GO", 0));

		return array;
	}

	private String getCountAvg(String pitchType) {
		List<Integer> pitch = pitchTypeCount.getOrDefault(pitchType, new ArrayList<>());
		double count = (double) pitch.stream().count();
		int sum = pitch.stream().mapToInt(p -> p).sum();

		return count == 0 ? "0" : String.format("%.1f", sum / count);
	}

	private String getPitchCount(String pitchType) {
		return String.valueOf(pitchTypeCount.getOrDefault(pitchType, new ArrayList<>()).stream().count());
	}

	private String getDate(int year, String filename) {
		String[] fn = filename.split("\\\\");
		String gameId = fn[fn.length - 1].split("\\.")[0];

		return String.valueOf(year) + "-" + gameId.substring(4, 6) + "-" + gameId.substring(6, 8);
	}

}
