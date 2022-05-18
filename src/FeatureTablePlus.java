/**
 * FeatureTablePlus.java
 * Daniel McIntyre
 * CS7720
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Daniel
 * Data structure to represent constructed features in tf-idf form.
 */
public class FeatureTablePlus extends FeatureTable {

	private HashMap<Integer, Integer> maxFrequencies;
	
	/**
	 * 
	 */
	public FeatureTablePlus() {
		super();
		maxFrequencies = new HashMap<Integer, Integer>();
	}
	
	/**
	 * Constructs a feature table with the keys from keys.
	 * @param keys The keys used to construct a feature table with.
	 */
	public FeatureTablePlus(Set<String> keys) {
		super(keys);
		maxFrequencies = new HashMap<Integer, Integer>();
	}

	/* (non-Javadoc)
	 * @see FeatureTable#add(java.lang.String, int)
	 */
	public void add(String key, int docNum) {
		super.add(key, docNum);
		if (maxFrequencies.containsKey(docNum)) {
			int possibleMax = features.get(key).get(docNum).intValue();
			if (maxFrequencies.get(docNum) < possibleMax) {
				maxFrequencies.put(docNum, possibleMax);
			}
		}
		else {
			maxFrequencies.put(docNum, 1);
		}
	}
	
	/**
	 * Calculates all tf-idf values for features in the table.
	 */
	public void calculate() {
		int nDocs = maxFrequencies.size();
		for (Map.Entry<String, HashMap<Integer, Number>> entry : features.entrySet()) {
			int keyNDocs = entry.getValue().size();
			for (Map.Entry<Integer, Number> v_entry : entry.getValue().entrySet()) {
				int v_key = v_entry.getKey();
				int v_value = v_entry.getValue().intValue();
				double tf = (double)v_value / (double)maxFrequencies.get(v_key);
				double idf = Math.log((double)nDocs / (double)keyNDocs) / Math.log(2);
				entry.getValue().put(v_key, (tf / idf));
			}
		}
	}
}
