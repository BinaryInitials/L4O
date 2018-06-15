package moving.average;

import static common.UtilMethods.readFileSingleColumn;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import common.UtilMethods.XY;

public class MovingAverage {
	
	public static void main(String[] args) {
		if(args == null || args.length == 0)
			System.exit(0);
		
		File file = new File(args[0]);
		
		List<HashMap<XY, Double>> data = readFileSingleColumn(file);
		int movingAverageDays = args.length > 1 ? Integer.valueOf(args[1]) : 1;
		
		List<Double> movingAverages = new ArrayList<Double>();
		for(int i=0;i<=data.size()-movingAverageDays;i++){
			double sum=0;
			for(int j=0;j<movingAverageDays;j++)
				sum+=data.get(i+j).get(XY.Y);
			movingAverages.add(sum/movingAverageDays);
		}
		for(int i=0;i<movingAverages.size();i++)
			System.out.println(movingAverages.get(i));
	}

}
