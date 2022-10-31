package textRelayProcessor;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public class TextRelayContext {
	List<BaseballRecord> players = new ArrayList<>();
	BaseballRecord batter;
	BaseballRecord pitcher;
	boolean isSameSequence = false;
	String lastPitch;
	int strike = 0;
	int ball = 0;

	public TextRelayContext(JSONObject textRelay) {
		this.players.addAll(TextProcessorUtils.getRecordList((JSONObject) textRelay.get("homeLineup"), true));
		this.players.addAll(TextProcessorUtils.getRecordList((JSONObject) textRelay.get("homeLineup"), false));
		this.players.addAll(TextProcessorUtils.getRecordList((JSONObject) textRelay.get("awayLineup"), true));
		this.players.addAll(TextProcessorUtils.getRecordList((JSONObject) textRelay.get("awayLineup"), false));
	}

	public void setContext(JSONObject currentGameState) {
		this.batter = this.players.stream()
				.filter(b -> b.personId == Integer.valueOf((String) currentGameState.get("batter"))).findFirst().get();
		this.pitcher = this.players.stream()
				.filter(p -> p.personId == Integer.valueOf((String) currentGameState.get("pitcher"))).findFirst().get();
	}
}
