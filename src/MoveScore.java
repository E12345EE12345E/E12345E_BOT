import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MoveScore {
    public String movelist;
    public double score;

    public MoveScore(String movelist, double score) {
        this.movelist = movelist;
        this.score = score;
    }

    public static void sort(List<MoveScore> list) {
        Collections.sort(list, new SortByRoll());
    }
}

class SortByRoll implements Comparator<MoveScore> {
    public int compare(MoveScore a, MoveScore b) {
        return (int)((a.score - b.score)*100);
    }
}