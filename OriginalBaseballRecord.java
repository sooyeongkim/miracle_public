package textRelayProcessor;

import java.util.ArrayList;
import java.util.List;

public class OriginalBaseballRecord {

	String name;
	int personId;

	BatRecord bat = new BatRecord();
	SplitRecord batR = new SplitRecord();
	SplitRecord batL = new SplitRecord();
	SplitRecord batU = new SplitRecord();

	PitRecord pit = new PitRecord();
	SplitRecord pitR = new SplitRecord();
	SplitRecord pitL = new SplitRecord();

	Fielding dfP = new Fielding();
	Fielding dfC = new Fielding();
	Fielding dfB1 = new Fielding();
	Fielding dfB2 = new Fielding();
	Fielding dfB3 = new Fielding();
	Fielding dfSS = new Fielding();
	Fielding dfLF = new Fielding();
	Fielding dfCF = new Fielding();
	Fielding dfRF = new Fielding();

	class BatRecord {
		int g;
		int gs;
		int sb;
		int cs;
		int r;
	}

	class PitRecord {
		int g;
		int gs;
		int win;
		int lose;
		int hold;
		int save;
		int ip;
		int r;
		int er;
		int balk;
		int wp;
	}

	class SplitRecord {
		int pa;
		int ab;
		int hit;
		int hit2b;
		int hit3b;
		int hr;
		int bb;
		int ibb;
		int hbp;
		int so;
		int sh;
		int sf;
		int rbi;
		int gdp;
		int fo;
		int go;

		int[] direction = new int[9];
		int[] homerunDistance;
		List<Pitch> pitches = new ArrayList<>();
	}

	class Pitch {
		int strike;
		int ball;
		int swing;
		int foul;

		int[] speed;
	}

	class Fielding {
		int inning;
		int po;
		int assist;
		int err;
		int pb;
		int sb;
		int cs;
	}
}
