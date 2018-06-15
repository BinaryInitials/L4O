package curve.fit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import Jama.Matrix;

import static common.UtilMethods.*;

public class CurveFit {

	public static void main(String[] args) {
		if(args == null || args.length == 0)
			System.exit(0);
		
		File file = new File(args[0]);
		List<HashMap<XY, Double>> data = readFile(file);
		int degree = args.length > 1 ? Integer.valueOf(args[1]) : 1;
		
		double[] matrixY = new double[data.size()];
		double[][] matrixX = new double[data.size()][degree+1];

		//Creating a Vandermonde Matrix
		for(int row = 0;row<data.size(); row++){
			for(int col=0;col<degree+1;col++)
				matrixX[row][col] = Math.pow(data.get(row).get(XY.X),col);
			matrixY[row] = data.get(row).get(XY.Y);
		}
		List<Double> coefs = getCoefficients(matrixX, matrixY, data.size());
		double r2 = calculateR2(data, coefs);
		Collections.reverse(coefs);
		char a = 'A';
		for(double c : coefs)
			System.out.println(a++ + ":\t" + c);
		System.out.println("R2:\t" + String.format("%.5f", r2));
	}
	
	public static List<Double> getCoefficients(double[][] x, double[] y, int length){
		List<Double> coefficients = new ArrayList<Double>();
		Matrix a = new Matrix(x);
		Matrix b = new Matrix(y, length);
		Matrix c = (a.transpose().times(a)).inverse().times(a.transpose()).times(b);
		double[][] coef = c.getArray();
		for(double[] coefOne : coef)
			coefficients.add(coefOne[0]);
		return coefficients;
	}
	
	public static double calculateR2(List<HashMap<XY, Double>> data, List<Double> coefs){
		return 1 - calculateResiduals(data, coefs)/calculateSST(data);
	}
	
	public static double calculateResiduals(List<HashMap<XY, Double>> data, List<Double> coefs){
		double error = 0.0;
		List<Double> yhat = calculateYHat(data, coefs);
		for(int i=0;i<data.size();i++)
			error += (data.get(i).get(XY.Y)- yhat.get(i))*(data.get(i).get(XY.Y)- yhat.get(i));
		return error;
	}
	
	public static double calculateSST(List<HashMap<XY, Double>> data){
		double average = calculateAverageY(data), sum = 0.0;
		for(HashMap<XY, Double> datum : data)
			sum += (average-datum.get(XY.Y))*(average-datum.get(XY.Y));
		return sum;
	}
	
	public static double calculateAverageY(List<HashMap<XY, Double>> data){
		double average = 0;
		for(HashMap<XY, Double> datum : data)
			average+= datum.get(XY.Y);
		return average/(0.0+data.size());
	}
	
	public static List<Double> calculateYHat(List<HashMap<XY, Double>> data, List<Double> coefs){
		List<Double> yHat = new ArrayList<Double>();
		for(HashMap<XY, Double> datum : data){
			double sum = 0;
			int i=0;
			for(double coef : coefs)
				sum+= coef*Math.pow(datum.get(XY.X), i++);
			yHat.add(sum);
		}
		return yHat;
	}
}