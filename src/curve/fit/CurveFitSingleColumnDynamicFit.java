package curve.fit;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import Jama.Matrix;

import static common.UtilMethods.*;

public class CurveFitSingleColumnDynamicFit {

	public static void main(String[] args) {
		if(args == null || args.length == 0)
			System.exit(0);
		
		File file = new File(args[0]);
		List<HashMap<XY, Double>> data = readFileSingleColumn(file);
		
		int degree = args.length > 1 ? Integer.valueOf(args[1]) : 1;
		int limit = args.length > 2 ? Integer.valueOf(args[2]) : 20;
		if(data.size() < limit){
			System.exit(0);
		}
		
		double maxR = 0;
		int n=0;
		
		List<Double> coefs = new ArrayList<Double>();
		for(int i=limit;i<Math.min(300,data.size());i++){
			List<HashMap<XY, Double>> subData = data.subList(data.size()-i,data.size());
			List<Double> tempcoefs = getCoefficients(subData, degree);
			double r2 = calculateR2(subData, tempcoefs);
			if(r2 < 1.0 && r2 > maxR){
				maxR = r2;
				coefs = getCoefficients(subData, degree);
				n = i;
			}
		}
		if(coefs.size() != 0){
			Collections.reverse(coefs);
			char a = 'A';
			for(double c : coefs)
				System.out.println(a++ + ":\t" + c);
			System.out.println("R2:\t" + String.format("%.5f", maxR));
			System.out.println("N:\t" + n);
			if(coefs.size() > 2){
				double C = coefs.get(coefs.size()-1);
				double B = coefs.get(coefs.size()-2);
				double A = coefs.get(coefs.size()-3);
				double tStep = -B/(2*A);
				double pFlat = C - B*B/(4*A);
				System.out.println((A > 0 ? "MIN:\t" : "MAX:\t") + String.format("%.2f", pFlat) + "\tIN " + String.format("%.2f", tStep) + " sec");
			}
			if(coefs.size() > 3){
				double D = coefs.get(coefs.size()-1);
				double C = coefs.get(coefs.size()-2);
				double B = coefs.get(coefs.size()-3);
				double A = coefs.get(coefs.size()-4);
				
				double determinant = B*B-3*A*C;
				if(determinant > 0){
					double t1 = -B/(3*A) + Math.sqrt(determinant)/(3*A);
					double t2 = -B/(3*A) - Math.sqrt(determinant)/(3*A);
					double p1 = A*t1*t1*t1 + B*t1*t1 + C*t1 + D;
					double p2 = A*t2*t2*t2 + B*t2*t2 + C*t2 + D;
					if(p1>p2){
						if(t1 > t2){
							System.out.println("MIN: " + String.format("%.2f",p2) + "\t @ " + t2);
							System.out.println("MAX: " + String.format("%.2f",p1) + "\t @ " + t1);
						}else{
							System.out.println("MAX: " + String.format("%.2f",p1) + "\t @ " + t1);
							System.out.println("MIN: " + String.format("%.2f",p2) + "\t @ " + t2);
						}
					}else{
						if(t1 > t2){
							System.out.println("MAX: " + String.format("%.2f",p2) + "\t @ " + t2);
							System.out.println("MIN: " + String.format("%.2f",p1) + "\t @ " + t1);
						}else {
							System.out.println("MIN: " + String.format("%.2f",p1) + "\t @ " + t1);
							System.out.println("MAX: " + String.format("%.2f",p2) + "\t @ " + t2);
						}
					}
				}
			}
		}
	}
	
	public static List<Double> getCoefficients(List<HashMap<XY, Double>> data, int degree){
		double[] matrixY = new double[data.size()];
		double[][] matrixX = new double[data.size()][degree+1];
		int length = data.size();
		//Creating a Vandermonde Matrix
		for(int row = 0;row<data.size(); row++){
			for(int col=0;col<degree+1;col++)
				matrixX[row][col] = Math.pow(data.get(row).get(XY.X),col);
			matrixY[row] = data.get(row).get(XY.Y);
		}
		List<Double> coefficients = new ArrayList<Double>();
		Matrix a = new Matrix(matrixX);
		Matrix b = new Matrix(matrixY, length);
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