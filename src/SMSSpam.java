/**
 * SMSSpam.java
 * Daniel McIntyre
 * CS7720
 */

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFrame;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

/**
 * @author Daniel
 *
 */
public class SMSSpam {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please choose the type of feature construction. (1 for raw frequency count or 2 for tf-idf. Default is 1)");
		String co = scanner.next();
		System.out.println("Please enter the number of features to select. Enter 0 to ignore feature selection.");
		int k = scanner.nextInt();
		
		if (!co.equals("2")) {
			co = "1";
			
			// Construct Training Set 1
			FeatureConstructor fc = new FeatureConstructor("data/hamtrain.txt", "output/TrainingFeatures_1.arff");
			fc.constructFeatures("Ham", true);
			fc.setInputFile("data/spamtrain.txt");
			fc.constructFeatures("Spam", true);
			fc.saveToARFF("HamSpamTraining1", true);
			
			// Construct Testing Set 1
			fc.prepTesting();
			fc.setOutputFile("output/TestingFeatures_1.arff");
			fc.setInputFile("data/hamtest.txt");
			fc.constructFeatures("Ham", false);
			fc.setInputFile("data/spamtest.txt");
			fc.constructFeatures("Spam", false);
			fc.saveToARFF("HamSpamTesting1", false);
		}
		else {
			// Construct Training Set 2
			FeatureConstructorPlus fcp = new FeatureConstructorPlus("data/hamtrain.txt", "output/TrainingFeatures_2.arff");
			fcp.constructFeatures("Ham", true);
			fcp.setInputFile("data/spamtrain.txt");
			fcp.constructFeatures("Spam", true);
			fcp.calculateTraining();
			fcp.saveToARFF("HamSpamTraining2", true);
			
			// Construct Testing Set 2
			fcp.prepTesting();
			fcp.setOutputFile("output/TestingFeatures_2.arff");
			fcp.setInputFile("data/hamtest.txt");
			fcp.constructFeatures("Ham", false);
			fcp.setInputFile("data/spamtest.txt");
			fcp.constructFeatures("Spam", false);
			fcp.calculateTesting();
			fcp.saveToARFF("HamSpamTesting2", false);
		}
		
		ArffLoader loader = new ArffLoader();
		try {
			// Loading data into Weka
			loader.setSource(new File("output/TrainingFeatures_" + co + ".arff"));
			Instances trainingData = loader.getDataSet();
			trainingData.setClassIndex(trainingData.numAttributes() - 1);
			
			loader.setSource(new File("output/TestingFeatures_" + co + ".arff"));
			Instances testingData = loader.getDataSet();
			testingData.setClassIndex(testingData.numAttributes() - 1);
			
			// Randomize Data
			trainingData.randomize(new Random());
			testingData.randomize(new Random());
			
			String sk = "";
			if (k != 0) {
				sk = String.valueOf(k);
				
				// Feature Selection
				InfoGainAttributeEval eval = new InfoGainAttributeEval();
				Ranker ranker = new Ranker();
				ranker.setNumToSelect(k);
				
				AttributeSelection filter = new AttributeSelection();
				filter.setEvaluator(eval);
				filter.setSearch(ranker);
				filter.setInputFormat(trainingData);
				trainingData = Filter.useFilter(trainingData, filter);
				testingData = Filter.useFilter(testingData, filter);
				
				// Save post-feature selection data to file
				ArffSaver saver = new ArffSaver();
				saver.setInstances(trainingData);
				saver.setFile(new File("./output/TrainingFeatures_"+co+"_"+k+".arff"));
				saver.writeBatch();
				
				saver.setInstances(testingData);
				saver.setFile(new File("./output/TestingFeatures_"+co+"_"+k+".arff"));
				saver.writeBatch();
			}
			
			System.out.println("Which type of classification? (J48, NB, SMO)");
			String cl = scanner.next();
			Evaluation cEval = null;
			
			if (cl.equals("J48")) {
				// Classification -- J48
				String options[] = new String[2];
				options[0] = "-C";
				options[1] = "0.25";
				String file = "./output/TestingFeatures_"+co+"_"+sk+"_J48.txt";
				
				PrintWriter writer = new PrintWriter(file);
				J48 tree = new J48();
				tree.setOptions(options);
				tree.buildClassifier(trainingData);
				cEval = new Evaluation(trainingData);
				cEval.evaluateModel(tree, testingData);
				writer.println(cEval.toSummaryString("J48 Results\n", true));
				writer.println(cEval.toClassDetailsString());
				writer.println(cEval.toMatrixString());
				writer.close();
			}
			else if (cl.equals("NB")) {	
				// Classification -- Naive Bayes
				String file = "./output/TestingFeatures_"+co+"_"+sk+"_NBC.txt";
				
				PrintWriter writer = new PrintWriter(file);
				NaiveBayes nbc = new NaiveBayes();
				nbc.buildClassifier(trainingData);
				cEval = new Evaluation(trainingData);
				cEval.evaluateModel(nbc, testingData);
				writer.println(cEval.toSummaryString("Naive Bayes Results\n", true));
				writer.println(cEval.toClassDetailsString());
				writer.println(cEval.toMatrixString());
				writer.close();
			}
			else if (cl.equals("SMO")) {
				// Classification -- SMO
				String optionsSMO[] = new String[1];
				optionsSMO[0] = "-M";
				String file = "./output/TestingFeatures_"+co+"_"+sk+"_SMO.txt";
				
				PrintWriter writer = new PrintWriter(file);
				SMO smo = new SMO();
				smo.buildClassifier(trainingData);
				cEval = new Evaluation(trainingData);
				cEval.evaluateModel(smo, testingData);
				writer.println(cEval.toSummaryString("SMO Results\n", true));
				writer.println(cEval.toClassDetailsString());
				writer.println(cEval.toMatrixString());
				writer.close();
			}
			
			if (cEval != null) {
				ThresholdCurve tc = new ThresholdCurve();
				int classIndex = 0;
				Instances curve = tc.getCurve(cEval.predictions(), classIndex);
				PlotData2D plotData = new PlotData2D(curve);
				plotData.setPlotName(curve.relationName());
				plotData.addInstanceNumberAttribute();
				ThresholdVisualizePanel tvp = new ThresholdVisualizePanel();
				tvp.setROCString("(Area under ROC = " + Utils.doubleToString(ThresholdCurve.getROCArea(curve),4)+")");
				tvp.setName(curve.relationName());
				tvp.addPlot(plotData);
				final JFrame jf = new JFrame("Weka ROC: " + tvp.getName());
				jf.setSize(500,400);
				jf.getContentPane().setLayout(new BorderLayout());
				jf.getContentPane().add(tvp, BorderLayout.CENTER);
				jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				jf.setVisible(true);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		scanner.close();
	}

}
