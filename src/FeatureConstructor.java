/**
 * FeatureConstructor.java
 * Daniel McIntyre
 * CS7720
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import weka.core.stemmers.SnowballStemmer;

/**
 * @author Daniel
 * Base feature construction class that constructs features based on raw term frequency in the training
 * and testing documents.
 */
public class FeatureConstructor {
	
	private String inputFile;
	private String outputFile;
	protected FeatureTable trainingFT;
	protected FeatureTable testingFT;
	private int totalTrainDocs;
	private int totalTestDocs;
	
	/**
	 * @param inputPath Path name to the input file for constructing features.
	 * @param outputPath Path name to the output file of constructed features.
	 */
	FeatureConstructor(String inputPath, String outputPath) {
		inputFile = inputPath;
		outputFile = outputPath;
		trainingFT = new FeatureTable();
		testingFT = new FeatureTable();
		totalTrainDocs = 0;
		totalTestDocs = 0;
	}
	
	/**
	 * @return Path name to the most recent input file used for constructing features.
	 */
	public String getInputFile() {
		return inputFile;
	}
	
	/**
	 * Set a new input path name for constructing features.
	 * @param path New input path name.
	 */
	public void setInputFile(String path) {
		inputFile = path;
	}
	
	/**
	 * @return Path name to the most recent output file used for constructing features.
	 */
	public String getOutputFile() {
		return outputFile;
	}
	
	/**
	 * Set a new output path name for constructed features.
	 * @param path New output path name.
	 */
	public void setOutputFile(String path) {
		outputFile = path;
	}
	
	/**
	 * Sets up the testing feature table prior to actually reading in testing data.
	 */
	public void prepTesting() {
		testingFT = new FeatureTable(trainingFT.getKeys());
	}

	/**
	 * Constructs features from the input file path.
	 * @param cl Class name for the features being constructed.
	 * @param training Boolean flag to indicate whether data is training or testing data.
	 */
	public void constructFeatures(String cl, boolean training) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			SnowballStemmer stemmer = new SnowballStemmer();
			
			for (String line; (line = reader.readLine()) != null;) {
				if (line.isEmpty() || line == null) {
					continue;
				}
				
				if (training) {
					++totalTrainDocs;
					trainingFT.addClass(cl, totalTrainDocs);
				}
				else {
					++totalTestDocs;
					testingFT.addClass(cl, totalTestDocs);
				}
				
				String document[] = line.split("\\s+");
				for (String word : document) {
					word = word.replaceAll("[^A-Za-z0-9]", "");
					if (word.isEmpty() || word == null) {
						continue;
					}
					String stemmed = stemmer.stem(word);
					if (stemmed.isEmpty() || stemmed == null) {
						continue;
					}
					if (training) {
						trainingFT.add(stemmed, totalTrainDocs);
					}
					else if (trainingFT.containsKey(stemmed)) {
						testingFT.add(stemmed, totalTestDocs);
					}
				}
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("File not found: " + inputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Save feature table to the output file in ARFF format.
	 * @param relationName Name of the relation in the ARFF file.
	 * @param training Boolean flag to indicate whether to save the training or testing data.
	 */
	public void saveToARFF(String relationName, boolean training) {
		try {
			PrintWriter writer = new PrintWriter(outputFile);
			FeatureTable outputFT = training ? trainingFT : testingFT;
			int totalDocs = training ? totalTrainDocs : totalTestDocs;
			
			if (training) {
				trainingFT.pruneAttributes(3000);
			}
			
			writer.println("@RELATION\t" + relationName);
			writer.println();
			for (String key : outputFT.getKeys()) {
				writer.println("@ATTRIBUTE\t" + key + "\t\tNUMERIC");
			}
			
			writer.print("@ATTRIBUTE\tClass\t\t{");
			boolean first = true;
			Set<String> classes = new HashSet<String>();
			classes.addAll(outputFT.getClasses());
			for (String cl : classes) {
				if (first) {
					first = false;
					writer.print(cl);
				}
				else {
					writer.print(", " + cl);
				}
			}
			writer.println("}");
			writer.println();
			
			writer.println("@DATA");
			int classAttrNo = outputFT.featureCount();
			for (int i = 1; i <= totalDocs; i++) {
				TreeMap<Integer, Number> docMap = outputFT.getDocMap(i);
				writer.print("{");
				for (Map.Entry<Integer, Number> entry : docMap.entrySet()) {
					writer.print((entry.getKey() - 1) + " " + entry.getValue() + ", ");
				}
				writer.println(classAttrNo + " " + outputFT.getDocClass(i) + "}");
			}
			
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("File not found: " + outputFile);
		}
	}
}