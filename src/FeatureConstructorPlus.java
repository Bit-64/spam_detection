/**
 * FeatureConstructorPlus.java
 * Daniel McIntyre
 * CS7720
 */

/**
 * @author Daniel
 * An extended class that constructs features form text based on the tf-idf model.
 */
public class FeatureConstructorPlus extends FeatureConstructor {
	
	/**
	 * @param inputPath Path name to the input file for constructing features.
	 * @param outputPath Path name to the output file of constructed features.
	 */
	public FeatureConstructorPlus(String inputPath, String outputPath) {
		super(inputPath, outputPath);
		trainingFT = new FeatureTablePlus();
		testingFT = new FeatureTablePlus();
	}
	
	/* (non-Javadoc)
	 * @see FeatureConstructor#prepTesting()
	 */
	public void prepTesting() {
		testingFT = new FeatureTablePlus(trainingFT.getKeys());
	}

	/**
	 * Calculates the tf-idf values for training data.
	 */
	public void calculateTraining() {
		((FeatureTablePlus) trainingFT).calculate();
	}
	
	/**
	 * Calculates the tf-idf values for testing data.
	 */
	public void calculateTesting() {
		((FeatureTablePlus) testingFT).calculate();
	}
}
