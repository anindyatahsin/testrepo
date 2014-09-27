// assumption: I am only using all products under boots category to illustrate my code my code

package zapposgiftgiving;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

import javax.json.*;



public class GiftCombination {
	
	private static final int RANGE = 10;
	private static final int REQ_SOL = 100;
	private static int SOL_COUNT = 0;
	private static int x = 0;
	private static ArrayList<Integer> products = new ArrayList<Integer>();
	private static ArrayList<Integer> styles = new ArrayList<Integer>();
	private static ArrayList<Double> prices = new ArrayList<Double>();
	private static int no_products;
	private static double dollar_sum, last_n = 0;
	private static Stack stack = new Stack();
	private static String key = "52ddafbe3ee659bad97fcce7c53592916a6bfd73"; 
	
	public static void main(String[] args){
		int i = 120;
		String prefix;
		// we can provide the search url as parameter
		if(args.length > 0){
			prefix = args[0];
		}else{
			prefix = "http://api.zappos.com/Search/term/boots";
		}
		Scanner scanIn = new Scanner(System.in);
		System.out.print("desired # of products:");
		no_products = scanIn.nextInt();
		System.out.print("desired dollar amount:");
		dollar_sum = scanIn.nextInt();
		System.out.println("Trying to find set of " + no_products + " gifts with budget " + dollar_sum);
		
		/** This part is embarrassingly parallel. We can very easily use multi-threading to parallelize this section **/
		while(true){
			String s = prefix + "?sort={%22price%22:%22desc%22}&limit=100&page="
					+ ++i + "&key="+key;
			if(!httpGet(s))
				break;
		}
		
		// no point searching from products whose value is greater than the dollar_sum
		int start_index = searchClosestItem(prices.size()-1, 0, dollar_sum); 
		// At least one of the product must have cost higher than the average 
		int end_index = searchClosestItem(prices.size()-1, 0, dollar_sum/no_products);
		
		
		// Find all the combinations that fall within $RANGE of the dollar_sum
		makeList(start_index, dollar_sum+RANGE/2, dollar_sum-RANGE/2, 0); 
		
	}
	
	public static void printStack(Stack stack1, double curr_sum){
		System.out.println("******************************* Option " + ++SOL_COUNT + " *******************************");
		System.out.println("ProductID\tStyleID\tPrice");
		for(int i = 0; i < stack1.size(); i++){
			int j = (int)stack1.get(i);
			System.out.println(products.get(j)+ "\t\t" + styles.get(j) + "\t\t" +prices.get(j));
		}
		System.out.println("Total = " + curr_sum);
		System.out.println("------------------------------------------------------------------------");
	}
	
	public static void makeList(int index, double max, double min, double curr_price){
		if(curr_price > max){
			return;
		}
		if(stack.size() == no_products){
			if(curr_price > min && curr_price < max){
				System.out.println(curr_price + " " + stack);
				printStack(stack,curr_price);
				if(SOL_COUNT == REQ_SOL){
					System.exit(0);
				}
			} 
			return;
		}
		
		for(int i = index; i < prices.size(); i++){
			stack.push(i);
			curr_price = curr_price + prices.get(i);
			makeList(i+1, max, min, curr_price);
			// backtracking
			int j = (int)stack.pop();
			curr_price = curr_price - prices.get(j);
		}
		
	}
	
	public static boolean httpGet(String urlStr) {
		try{
			URL url = new URL(urlStr);
			HttpURLConnection conn =
			      (HttpURLConnection) url.openConnection();
			
			if (conn.getResponseCode() != 200) {
				throw new IOException(conn.getResponseMessage());
			}
			
			InputStream is = conn.getInputStream();    
			JsonReader rdr = Json.createReader(is);
			JsonObject obj = rdr.readObject();
			JsonArray results = obj.getJsonArray("results");
			
			if(results.size() == 0) return false;
			for (JsonObject result : results.getValuesAs(JsonObject.class)) {
				//System.out.print(++x + " " + result.getString("productId", "") + " ");
				products.add(Integer.parseInt(result.getString("productId", "")));
				styles.add(Integer.parseInt(result.getString("styleId", "")));
				String price = result.getString("price", "");
				price = price.replace("$","");
				price = price.replace(",","");
				//System.out.println(price);
				prices.add(Double.parseDouble(price));
				
			}
			//System.out.println("-----------");
			conn.disconnect();
			//return json.toString();
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	// no need to search the items are higher priced than the budget
	// find the item that is closed to dollar_amount (binary search)
	
	public static int searchClosestItem(int upper, int lower, double key){
		// use binary search to find the position to begin or end search positions
		if(upper <= lower) 
			if(upper == -1)
				return lower;
			else return upper;
		
		int i = (upper + lower)/2;
		if(key == prices.get(i)){
			return i;
		}
		else if(key > prices.get(i)){
			upper = i - 1;
		}
		else{
			lower = i + 1;
		}
		return searchClosestItem(upper, lower, key);
	}
	
}
