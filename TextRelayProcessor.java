package textRelayProcessor;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.opencsv.CSVWriter;

public class TextRelayProcessor {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException, ParseException {
		int year = 2008;

		String path = "C:\\Users\\gd-wred\\Desktop\\DEV\\TextRelays\\" + String.valueOf(year);
		List<String> files = TextProcessorUtils.getJson(path);

		List<String[]> result = new ArrayList<>();
		result.add(TextProcessorUtils.HEADER);

		List<String[]> paResult = new ArrayList<>();
		paResult.add(TextProcessorUtils.PA_HEADER);

		List<String[]> battedBallResult = new ArrayList<>();
		battedBallResult.add(TextProcessorUtils.BATTED_BALL_RESULT_HEADER);

		for (String file : files) {
			JSONParser parser = new JSONParser();
			Reader reader = new FileReader(file);
			JSONObject textRelay = (JSONObject) parser.parse(reader);

			TextRelayContext context = new TextRelayContext(textRelay);

			for (int i = 0; i < (textRelay.size() - 2); i++) {
				JSONArray inning = (JSONArray) textRelay.get(String.valueOf(i + 1));
				Iterator<JSONObject> iterator = inning.iterator();

				while (iterator.hasNext()) {
					JSONObject pa = iterator.next();
					JSONArray textOptions = (JSONArray) pa.get("textOptions");

					context.ball = 0;
					context.strike = 0;

					Iterator<JSONObject> toIterator = textOptions.iterator();
					while (toIterator.hasNext()) {
						JSONObject textOption = toIterator.next();

						try {
							context.setContext((JSONObject) textOption.get("currentGameState"));
						} catch (Exception e) {
							continue;
						}

						process(context, textOption);
					}
				}
			}

			context.players.stream().forEach(p -> result.add(p.toString(year, file)));
			context.players.stream().forEach(p -> p.pitchTypePaResult.keySet().stream()
					.forEach(k -> paResult.add(p.paResultToString(year, file, k))));
			context.players.stream().forEach(p -> battedBallResult.add(p.battedBallResultToString(year, file)));
		}

		try (CSVWriter writer = new CSVWriter(new FileWriter(
				"C:\\Users\\gd-wred\\Desktop\\" + String.valueOf(year) + ".csv", Charset.forName("EUC-KR")))) {
			writer.writeAll(result);
		}

		try (CSVWriter writer = new CSVWriter(new FileWriter(
				"C:\\Users\\gd-wred\\Desktop\\pitch" + String.valueOf(year) + ".csv", Charset.forName("EUC-KR")))) {
			writer.writeAll(paResult);
		}

		try (CSVWriter writer = new CSVWriter(new FileWriter(
				"C:\\Users\\gd-wred\\Desktop\\batted" + String.valueOf(year) + ".csv", Charset.forName("EUC-KR")))) {
			writer.writeAll(battedBallResult);
		}
	}

	private static void process(TextRelayContext context, JSONObject textOption) {
		int type = (int) (long) textOption.get("type");
		List<BaseballRecord> players = context.players;
		String text = (String) textOption.get("text");

		BaseballRecord batter = context.batter;
		BaseballRecord pitcher = context.pitcher;

		switch (type) {
		case 0:
			// ????????????
		case 2:
			// ?????? ??????
		case 7:
			// ?????? ??????
		case 8:
			// ?????? ??????
		case 99:
			// ?????? ??????
			// ??????, ??????, ??????, ????????? ??????
			break;

		case 1:
			// ?????????
			batter.batPitches++;
			context.isSameSequence = false;

			String pitchType = (String) textOption.get("stuff");
			int speed = Integer.valueOf((String) textOption.get("speed"));
			long pitchNum = (long) textOption.get("pitchNum");
			String pitchResult = (String) textOption.get("pitchResult");

			setPitchTypes(batter, pitchType, speed, pitchResult, pitchNum, context.strike, context.ball);
			setPitchTypes(pitcher, pitchType, speed, pitchResult, pitchNum, context.strike, context.ball);

			if (pitchResult.equals("B"))
				context.ball++;
			else if (context.strike < 2)
				context.strike++;

			context.lastPitch = pitchType;

			break;
		case 13:
			// ?????? ??????
		case 23:
			// ????????? ????????? ?????? ??????
			if (text.contains("????????????") || text.contains("?????? ??????")) {
				batter.batSh++;
				batter.batGo++;
				pitcher.pitSh++;
				pitcher.pitGo++;
			} else if (text.contains("?????????") || text.contains("?????????")) {
				batter.batGdp++;
				batter.batGo++;
				pitcher.pitGdp++;
				pitcher.pitGo++;
			} else if (text.contains("????????????") || text.contains("??????") || text.contains("????????????") || text.contains("??????")
					|| text.contains("????????????")) {
				batter.batGo++;
				pitcher.pitGo++;
			} else if (text.contains("???????????????") || text.contains("?????? ?????????")) {
				batter.batSf++;
				batter.batFo++;
				pitcher.pitSf++;
				pitcher.pitFo++;
			} else if (text.contains("?????????") || text.contains("??????????????????")) {
				batter.batFo++;
				pitcher.pitFo++;
			} else if (text.contains("2??????")) {
				batter.batHit2b++;
				pitcher.pitHit2b++;
			} else if (text.contains("3??????")) {
				batter.batHit3b++;
				pitcher.pitHit3b++;
			}

			batter.batPa++;
			pitcher.pitPa++;

			setPitchTypes(batter, context.lastPitch, text);
			setPitchTypes(pitcher, context.lastPitch, text);

			setBattedBallResults(batter, text);
			setBattedBallResults(pitcher, text);

			String[] notAb = { "???????????????", "?????? ?????????", "????????????", "?????? ??????", "????????????", "?????? ??????", "?????? ???", "??????", "??? ???", "??????4???" };

			if (!Arrays.stream(notAb).anyMatch(text::contains))
				pitcher.pitAb++;
			break;
		case 14:
			// ??????
		case 24:
			// ????????? ????????? ??????

			String[] splitText = text.split(" ");

			if (splitText[0].contains("?????????") && (text.contains("?????? ??????") || text.contains("????????????"))) {
				players.stream().filter(p -> p.name.equals(splitText[1])).findFirst().get().batCs++;
				pitcher.pitCs++;
			} else if (splitText[0].contains("?????????") && text.contains("??????")) {
				players.stream().filter(p -> p.name.equals(splitText[1])).findFirst().get().batSb++;
				pitcher.pitSb++;
			} else if (text.contains("??????") && !context.isSameSequence) {
				pitcher.pitWp++;
				context.isSameSequence = true;
			}

			break;
		case 44:
			// ?????? ??????
			break;

		default:
			break;
		}
	}

	protected static void setPitchTypes(BaseballRecord player, String pitchType, int speed, String pitchResult,
			long pitchNum, int strike, int ball) {
		List<Integer> speeds = player.pitchTypeCount.getOrDefault(pitchType, new ArrayList<>());
		speeds.add(speed);
		player.pitchTypeCount.put(pitchType, speeds);

		List<String> pitchResults = player.pitchTypeResult.getOrDefault(pitchType, new ArrayList<>());
		pitchResults.add(pitchResult);
		player.pitchTypeResult.put(pitchType, pitchResults);

		if (pitchNum == 1L) {
			List<String> firstPitchResults = player.firstPitchResult.getOrDefault(pitchType, new ArrayList<>());
			firstPitchResults.add(pitchResult);
			player.firstPitchResult.put(pitchType, firstPitchResults);
		}

		if (strike == 2) {
			List<String> afterTwoStrike = player.afterTwoStrikeResult.getOrDefault(pitchType, new ArrayList<>());
			afterTwoStrike.add(pitchResult);
			player.afterTwoStrikeResult.put(pitchType, afterTwoStrike);
		}
	}

	protected static void setPitchTypes(BaseballRecord player, String pitchType, String paResult) {
		Map<String, Integer> pitchTypePaResults = player.pitchTypePaResult.getOrDefault(pitchType,
				new HashMap<String, Integer>(TextProcessorUtils.SIMPLE_PA_MAP));

		pitchTypePaResults.compute("PA", (k, v) -> ++v);

		if (paResult.contains("??????") || paResult.contains("1??????")) {
			pitchTypePaResults.compute("1B", (k, v) -> ++v);
			pitchTypePaResults.compute("AB", (k, v) -> ++v);
		} else if (paResult.contains("2??????")) {
			pitchTypePaResults.compute("2B", (k, v) -> ++v);
			pitchTypePaResults.compute("AB", (k, v) -> ++v);
		} else if (paResult.contains("3??????")) {
			pitchTypePaResults.compute("3B", (k, v) -> ++v);
			pitchTypePaResults.compute("AB", (k, v) -> ++v);
		} else if (paResult.contains("??????")) {
			pitchTypePaResults.compute("HR", (k, v) -> ++v);
			pitchTypePaResults.compute("AB", (k, v) -> ++v);
		} else if (paResult.contains("??????4???") || paResult.contains("??????")) {
			pitchTypePaResults.compute("BB", (k, v) -> ++v);
		} else if (paResult.contains("?????? ???")) {
			pitchTypePaResults.compute("HBP", (k, v) -> ++v);
		} else if (paResult.contains("?????????")) {
			pitchTypePaResults.compute("GDP", (k, v) -> ++v);
			pitchTypePaResults.compute("GO", (k, v) -> ++v);
			pitchTypePaResults.compute("AB", (k, v) -> ++v);
		} else if (paResult.contains("????????????") || paResult.contains("?????? ??????")) {
			pitchTypePaResults.compute("SH", (k, v) -> ++v);
		} else if (paResult.contains("???????????????") || paResult.contains("?????? ?????????")) {
			pitchTypePaResults.compute("SF", (k, v) -> ++v);
			pitchTypePaResults.compute("FO", (k, v) -> ++v);
		} else if (paResult.contains("??????") || paResult.contains("??????")) {
			pitchTypePaResults.compute("GO", (k, v) -> ++v);
			pitchTypePaResults.compute("AB", (k, v) -> ++v);
		} else if (paResult.contains("??????") || paResult.contains("??????")) {
			pitchTypePaResults.compute("AB", (k, v) -> ++v);
		} else if (paResult.contains("??????") || paResult.contains("??????????????? ???") || paResult.contains("????????????")) {
			pitchTypePaResults.compute("SO", (k, v) -> ++v);
			pitchTypePaResults.compute("AB", (k, v) -> ++v);
		} else if (paResult.contains("?????????") || paResult.contains("??????????????????")) {
			pitchTypePaResults.compute("FO", (k, v) -> ++v);
			pitchTypePaResults.compute("AB", (k, v) -> ++v);
		}

		player.pitchTypePaResult.put(pitchType, pitchTypePaResults);
	}

	private static void setBattedBallResults(BaseballRecord player, String text) {
		String ffWord = text.replace("  ", " ").split(" : ")[1];
		boolean isOutfield = ffWord.startsWith("?????????") || ffWord.startsWith("?????????") || ffWord.startsWith("?????????")
				|| ffWord.contains("?????????") || ffWord.contains("?????????");
		boolean isInfield = ffWord.startsWith("??????") || ffWord.startsWith("??????") || ffWord.contains("??????")
				|| ffWord.startsWith("?????????");
		boolean isBunt = text.contains("??????") && !text.contains("??????");

		if (!isOutfield && !isInfield && !isBunt)
			return;

		String resultType = isBunt ? "BUNT" : isOutfield ? "OF" : "IF";

		Map<String, Integer> battedBallResults = player.battedBallResult.getOrDefault(resultType,
				new HashMap<String, Integer>(TextProcessorUtils.BATTED_BALL_MAP));

		setBattedBallResults(battedBallResults, text);
		player.battedBallResult.put(resultType, battedBallResults);

		if (resultType.equals("BUNT")) {
			battedBallResults = player.battedBallResult.getOrDefault("IF",
					new HashMap<String, Integer>(TextProcessorUtils.BATTED_BALL_MAP));

			setBattedBallResults(battedBallResults, text);
			player.battedBallResult.put("IF", battedBallResults);
		}
	}

	private static void setBattedBallResults(Map<String, Integer> battedBallResults, String text) {
		battedBallResults.compute("CNT", (k, v) -> ++v);

		if (text.contains("??????") || text.contains("??????") || text.contains("??????"))
			battedBallResults.compute("HIT", (k, v) -> ++v);
		else if (text.contains("?????????") || text.contains("??????????????????"))
			battedBallResults.compute("FO", (k, v) -> ++v);
		else if (text.contains("??????") || text.contains("??????") || text.contains("?????????"))
			battedBallResults.compute("GO", (k, v) -> ++v);
	}

}
