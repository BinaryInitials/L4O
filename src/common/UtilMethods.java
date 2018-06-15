package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UtilMethods {
	
	public static enum XY {
		X,
		Y
	}
	public static List<HashMap<XY, Double>> readFileSingleColumn(File file){
		List<HashMap<XY, Double>> data = new ArrayList<HashMap<XY,Double>>();
		
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(file));
			String line = "";
			List<String> lines = new ArrayList<String>();
			while((line = buffer.readLine()) != null)
				lines.add(line);
			buffer.close();
			Collections.reverse(lines);
			int x=-1;
			for(String aline : lines){
				if(aline.isEmpty())
					continue;
				HashMap<XY, Double> dataPoint = new HashMap<XY, Double>();
				dataPoint.put(XY.X, Double.valueOf(x--));
				dataPoint.put(XY.Y, Double.valueOf(aline));
				data.add(dataPoint);
			}
			Collections.reverse(data);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return data;
	}
	
	public static List<HashMap<XY, Double>> readFile(File file){
		List<HashMap<XY, Double>> data = new ArrayList<HashMap<XY,Double>>();
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(file));
			String line = "";
			while((line = buffer.readLine()) != null){
				String[] parts = line.split("\t|,");
				HashMap<XY, Double> dataPoint = new HashMap<XY, Double>();
				dataPoint.put(XY.X, Double.valueOf(parts[0]));
				dataPoint.put(XY.Y, Double.valueOf(parts[1]));
				data.add(dataPoint);
 			}
			buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
//			e.printStackTrace();
		}
		return data;
	}

}
