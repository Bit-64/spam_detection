/**
 * MapValueComparator.java
 * Daniel McIntyre
 * CS7720
 */

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;

/**
 * @author Daniel
 *
 */
public class MapValueComparator implements Comparator<String> {

	Map<String, Number> base;
	
	/**
	 * 
	 */
	public MapValueComparator(Map<String, Number> base) {
		this.base = base;
	}

	@Override
	public int compare(String o1, String o2) {
		if (new BigDecimal(base.get(o1).toString()).compareTo(new BigDecimal(base.get(o2).toString())) <= 0) {
            return -1;
        } else {
            return 1;
        }
	}

}
