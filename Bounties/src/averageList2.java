import java.io.*;
import java.util.ArrayList;

public class averageList2{
	public static void main(String[] args) throws Exception{
		File[] folders = getFolders(args[0]);
		for(int qw = 0; qw<folders.length; qw++){
		if(folders[qw]  == null)
			continue;
		
		PrintWriter writer = new PrintWriter(folders[qw].getAbsolutePath() + "/final.txt", "UTF-8");
		PrintWriter writerGrid = new PrintWriter(folders[qw].getAbsolutePath() + "/finalGrid.txt", "UTF-8");
                PrintWriter writerSkipGrid = new PrintWriter(folders[qw].getAbsolutePath() + "/finalSkipGrid.txt", "UTF-8");
                PrintWriter writerAvgSkip = new PrintWriter(folders[qw].getAbsolutePath() + "/finalavgSkip.txt", "UTF-8");
		File[] file = getFiles(folders[qw].getAbsolutePath());
		System.out.println(folders[qw].getAbsolutePath());
		ArrayList<double[]> runs = new ArrayList<double[]>();
		for(int i = 0; i<file.length; i++){
			if(file[i] !=null){
				BufferedReader br = new BufferedReader(new FileReader(file[i]));
				String k = "";
				k = br.readLine();
				//System.out.println((k));
				String[] values = k.split(",");
				double numbers[] = makeNumArrayFromStrings(values);
				runs.add(numbers);
				
			}
		}
		ArrayList<Double> averages = new ArrayList<Double>();
		double totalRuns = runs.get(0).length;
		for(int i = 0; i<totalRuns; i++){
			averages.add((double)0);
			double aSum = 0;
			
			
			for(int a = 0; a<runs.size(); a++){
				if(i<runs.get(a).length)
				aSum += runs.get(a)[i];
				
			}
			averages.set(i,aSum / runs.size());
			writer.write(""+averages.get(i)+ "\n");
                        
                        if (i %100 == 0) {
                            // then write out to the every hundred average
                            writerAvgSkip.write(""+averages.get(i)+ " ");
                        }
                        
			//System.out.println(i + "  "  + averages.get(i));
		}
		for(int i = 0; i<totalRuns; i++){
			//averages.add((double)0);
			double aSum = 0;
			
			for(int a = 0; a<runs.size(); a++){
				//aSum += runs.get(a)[i];
				writerGrid.write(""+runs.get(a)[i]+ " ");
			}
			writerGrid.write("\n");
			
		}
                
                for(int i = 0; i<totalRuns; i= i+ 100){ // every hundred timesteps
			//averages.add((double)0);
			
			for(int a = 0; a<runs.size(); a++){
				//aSum += runs.get(a)[i];
				writerSkipGrid.write(""+runs.get(a)[i]+ " ");
			}
			writerSkipGrid.write("\n");
			
		}
                
                
                
                
		writer.close();
		writerGrid.close();
                writerSkipGrid.close();
                writerAvgSkip.close();
		}
	}
	public static double[] makeNumArrayFromStrings(String[] values){
		double[] numbers = new double[values.length];
		for(int i = 0; i<values.length; i++){
		//	System.out.println(i + " " + numbers[i]);
			numbers[i] = Double.parseDouble(values[i]);
		}
		return numbers;
	}
	public static File[] getFiles(String name){
		File folder = new File(name);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile() && !listOfFiles[i].getName().contains("final")) {
			System.out.println("File " + listOfFiles[i].getName());
		  } else  {
			
			System.out.println("Directory " + listOfFiles[i].getName());
			listOfFiles[i] = null;
		  }
		}
		return listOfFiles;
	}
	public static File[] getFolders(String name){
		File folder = new File(name);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
		  if (listOfFiles[i].isFile()) {
			System.out.println("File " + listOfFiles[i].getName());
			listOfFiles[i] = null;
		  } else  {
			
			System.out.println("Directory " + listOfFiles[i].getName());
			
		  }
		}
		return listOfFiles;
	}
}