/**
 * FeatureTable.java
 * Daniel McIntyre
 * CS7720
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Daniel
 * Data structure to represent constructed features.
 */
public class FeatureTable {
	
	protected TreeMap<String, HashMap<Integer, Number>> features;
	private HashMap<Integer, String> classMap;
	private HashMap<String, Number> totalValues;
	
	/**
	 * 
	 */
	FeatureTable() {
		features = new TreeMap<String, HashMap<Integer, Number>>();
		classMap = new HashMap<Integer, String>();
		totalValues = new HashMap<String, Number>();
	}
	
	/**
	 * Constructs a feature table with the keys from keys.
	 * @param keys The keys used to construct a feature table with.
	 */
	FeatureTable(Set<String> keys) {
		features = new TreeMap<String, HashMap<Integer, Number>>();
		classMap = new HashMap<Integer, String>();
		totalValues = new HashMap<String, Number>();
		for (String key : keys) {
			features.put(key, new HashMap<Integer, Number>());
		}
	}
	
	/**
	 * Add a feature to the feature table.
	 * @param key The feature identifier.
	 * @param docNum Number of the document the feature is located in.
	 */
	public void add(String key, int docNum) {
		if (this.containsKey(key)) {
			HashMap<Integer, Number>feature = features.get(key);
			if (feature.containsKey(docNum)) {
				feature.put(docNum, feature.get(docNum).intValue()+1);
			}
			else {
				feature.put(docNum, 1);
			}
		}
		else {
			HashMap<Integer, Number> feature = new HashMap<Integer, Number>();
			feature.put(docNum, 1);
			features.put(key, feature);
			totalValues.put(key, 1);
		}
		
		if (totalValues.containsKey(key)) {
			totalValues.put(key, totalValues.get(key).intValue()+1);
		}
		else {
			totalValues.put(key, 1);
		}
	}
	
	/**
	 * Adds a class value to a specified document.
	 * @param cl Class name.
	 * @param docNum Number of the document.
	 */
	public void addClass(String cl, int docNum) {
		classMap.put(docNum, cl);
	}
	
	/**
	 * Check to see if the feature identifier exists in the feature table.
	 * @param key Feature identify to check.
	 * @return True if feature table contains key. False otherwise.
	 */
	public boolean containsKey(String key) {
		return features.containsKey(key);
	}
	
	/**
	 * Get a set of all feature identifiers in the feature table.
	 * @return All keys.
	 */
	public Set<String> getKeys() {
		return features.keySet();
	}
	
	/**
	 * Returns the class name for the specified document.
	 * @param docNum The number of the document.
	 * @return Class name of the specified document.
	 */
	public String getDocClass(int docNum) {
		return classMap.get(docNum);
	}
	
	/**
	 * Returns all classes in the feature table.
	 * @return All classes in the feature table.
	 */
	public Collection<String> getClasses() {
		return classMap.values();
	}
	
	/**
	 * Returns the number of features in the feature table.
	 * @return The number of features in the feature table.
	 */
	public int featureCount() {
		return features.size();
	}
	
	/**
	 * Prunes the number of attributes down to k, based on the weight/values of the features in the documents.
	 * @param k The number of attributes remaining in the vector space.
	 */
	public void pruneAttributes(int k) {
		TreeMap<String, Number> sorted = new TreeMap<String, Number>(new MapValueComparator(totalValues));
		sorted.putAll(totalValues);
		
		for (Map.Entry<String, Number> entry : sorted.entrySet()) {
			sorted.remove(entry.getKey());
			features.remove(entry.getKey());
			if (features.size() <= k) {
				break;
			}
		}
	}
	
	/**
	 * Get the weights/values of all features in the specified document.
	 * @param docNum The number of the document to get the weights/values.
	 * @return the tree map of the weights/values of the features in the specified document.
	 */
	public TreeMap<Integer, Number> getDocMap(int docNum) {
		int i = 1;
		TreeMap<Integer, Number> results = new TreeMap<Integer, Number>();
		for (HashMap<Integer, Number> value : features.values()) {
			if (value.containsKey(docNum)) {
				results.put(i, value.get(docNum));
			}
			++i;
		}
		return results;
	}
}
