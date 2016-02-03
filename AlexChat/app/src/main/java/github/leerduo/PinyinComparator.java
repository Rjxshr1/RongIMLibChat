package github.leerduo;
import java.util.Comparator;
/**
 * @Link https://github.com/leerduo/SortListView
 * @SDK 2.1
 * */
public class PinyinComparator implements Comparator<SortModel> 
{
	public int compare(SortModel o1, SortModel o2) {
		SortModel modelTmp = new SortModel();
		if (o1.letter.equals("@")
				|| o2.letter.equals("#")) {
			return -1;
		} else if (o1.letter.equals("#")
				|| o2.letter.equals("@")) {
			return 1;
		} else {
			int compareTo = o1.letter.compareTo(o2.letter);
			modelTmp = o1;
			o1 = o2;
			o2 = modelTmp;
			return compareTo;
		}
	}
}
