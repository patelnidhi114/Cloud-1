package model;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import java.util.List;
import java.util.Scanner;
import org.apache.spark.mllib.fpm.AssociationRules;
import org.apache.spark.mllib.fpm.FPGrowth;
import org.apache.spark.mllib.fpm.FPGrowthModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import model.data;

public class main {
	   
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		SparkConf conf = new SparkConf();
		conf.setMaster("local").setAppName("word count");
		JavaSparkContext jsc = new JavaSparkContext(conf);
		
		//Input Dataset Path 
		JavaRDD<String> file = jsc.textFile("C:\\Users\\NIDHI\\New_dataset.csv");
		JavaRDD<List<String>> transaction = file.map(line -> Arrays.asList(line.split(",")));
		
		FPGrowth fpg = new FPGrowth().setMinSupport(0.01).setNumPartitions(1);
		FPGrowthModel<String> model = fpg.run(transaction);
		
		for (FPGrowth.FreqItemset<String> itemset: model.freqItemsets().toJavaRDD().collect()) {
			// To print frequency of each itemset as per given support  
			System.out.println(itemset.javaItems() + "," + itemset.freq());
		}
		List<data> items =new ArrayList<data>();
		double minConfidence = 0.2;
		for (AssociationRules.Rule<String> rule : model.generateAssociationRules(minConfidence).toJavaRDD().collect()) {
			//System.out.println(rule.javaAntecedent() + " ====> " + rule.javaConsequent() + ", " + rule.confidence()*100);
			data dataobj = new data(rule.javaAntecedent(),rule.javaConsequent(),rule.confidence()*100);
		    items.add(dataobj);
		}		
		Scanner reader = new Scanner(System.in);
		//recommender
		/*System.out.print("Enter the item you want to search :");
		String input = reader.nextLine();*/
		JSONArray array = new JSONArray();
		JSONArray value = new JSONArray();
		JSONArray key = new JSONArray();
		JSONObject obj  = new JSONObject();
		PrintWriter outputfile = new PrintWriter("C:\\Users\\NIDHI\\dataset121.json");
			
		for(data dataobj : items){
			//System.out.println(dataobj.getAntecedent() + "====>" +dataobj.getConsequent()+"====>" + dataobj.getConfidence());
			key.add(dataobj.getAntecedent());
			value.add(dataobj.getConsequent());
			obj.put(dataobj.getAntecedent(), dataobj.getConsequent());
			/*if(dataobj.getAntecedent().contains(input)) {
				System.out.println(dataobj.getAntecedent() +"===>"+dataobj.getConsequent() + "===>" + dataobj.getConfidence());
				//value.add(dataobj.getConsequent());
				//obj.put(dataobj.getAntecedent(), value);
			}
			else {
				//System.out.println(input + " has no enough support or is not in the list.");
				//break;
			}*/	
		}		
		array.add(obj);
		outputfile.print(array.toJSONString());	
		outputfile.close();
		outputfile.flush();
	}	
}
