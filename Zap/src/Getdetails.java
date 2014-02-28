import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.xml.internal.messaging.saaj.soap.impl.DetailImpl;

/**
 * Servlet implementation class Getdetails
 */
@WebServlet("/Getdetails")
public class Getdetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	HashMap<Integer, Double> details;
	HashMap<Double, ArrayList> i11=new HashMap<Double, ArrayList>();
	int[] data2=new int[1000];
	int p=0,q=0;
	ArrayList<Integer> data=new ArrayList<Integer>();
	ArrayList<Integer> key=new ArrayList<Integer>();
	ArrayList<Double> val=new ArrayList<Double>();
	/**
     * @see HttpServlet#HttpServlet()
     */
   public Getdetails() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//System.out.println("control passed to servlet");
		HashMap<Integer,Double> details=new HashMap<Integer,Double>();
		String quantity=request.getParameter("quantity");
		String price=request.getParameter("price");
		if ((quantity !=null && !"".equals(quantity) ))
		{
	        try 
	        {
	           q= Integer.parseInt(quantity);      
	           if(q<=0)
	           {
	        	   String printhtml;
	        	   printhtml="<h1>Number of Items Cannot be Zero</h1>";
	        	   response.getWriter().write(printhtml);
	        	   return;
	           }
	        }
	        catch (NumberFormatException e)
	        {
	           System.out.println("This is not a number");
	           System.out.println(e.getMessage());
	        }
	    }
		
		if ((price !=null && !"".equals(price) ))
		{
	        try 
	        {
	           p= Integer.parseInt(price);
	           if(p<=0)
	           {
	        	   String printhtml;
	        	   printhtml="<h1>Price Cannot be Zero</h1>";
	        	   response.getWriter().write(printhtml);
	        	   return;
	           }
	        }
	        catch (NumberFormatException e)
	        {
	           System.out.println("This is not a number");
	           System.out.println(e.getMessage());
	        }
	    }
		if(details.size()!=0)
		{
			details.clear();
		}
		if(i11.size()!=0)
		{
			i11.clear();
		}
		String urlString="http://api.zappos.com/Search?key=5b8384087156eb88dce1a1d321c945564f4d858e";
		//System.out.println(urlString);
		URL url = new URL(urlString);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setAllowUserInteraction(false);
		response.setContentType("json");
		InputStream urlStream= url.openStream();
		BufferedReader br= new BufferedReader(new InputStreamReader(urlStream));
		String strLine="";
		String outputLine=new String();
		while((strLine=br.readLine())!=null)
		{
			outputLine+=strLine;
		}
		//System.out.println(outputLine);
		try
		{
			JSONObject jsonObj = new JSONObject(outputLine);
			JSONArray jres=jsonObj.getJSONArray("results");
			for(int i=0;i<jres.length();i++)
			{
				
				String price1=(String)((JSONObject) jres.get(i)).get("price");
				price1=price1.substring(1);
				//System.out.println("after manipulation"+price1);
				Double pric=Double.parseDouble(price1);
				int id=((JSONObject)jres.get(i)).getInt("productId");
				details.put(id, pric);
				
			}	
			if(q>details.size())
			{
				String printhtml;
				printhtml="<h1>Zero Matches Found.Too many products</h1>";
				response.getWriter().write(printhtml);       // Write response body.
				return;
			}
			getItems(details,p,q);
			//System.out.println(outputLine);
			String printhtml = "<div><h1>Your Results</h1></div><table border='1'><th>Combined Sum of Products</th><th>Product Ids</th>";
			for (Map.Entry<Double, ArrayList> e : i11.entrySet())
			{
				
				if(e.getValue().size()!=q)
				{
					continue;
				}
				
				printhtml+="<tr><td>";
				printhtml+="$"+(new DecimalFormat("#0.00").format(e.getKey()));
				printhtml+="</td><td>";
				for(int z=0;z<e.getValue().size();z++)
				{
					printhtml+="<a href='http://www.zappos.com//product//"+e.getValue().get(z)+"'target=_blank'\'>"+e.getValue().get(z)+"</a> ";
				}
				printhtml+="</td></tr>";
			}
			if(printhtml.contains("<tr>"))
			{
			printhtml += "</table><br><br><p>Click on Product ID to view Details</p>";
			response.getWriter().write(printhtml); 
			return;
			// Write response body.
			}
			else
			{
				printhtml="<h1>Zero Matches Found.Change Search Criteria</h1>";
				response.getWriter().write(printhtml);       // Write response body.
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();// TODO: handle exception
		}
}

	public void getItems(HashMap<Integer,Double> det,int p1,int q1) 
	{	
		
		for (Map.Entry<Integer, Double> e : det.entrySet())
		{
			key.add(e.getKey());
			val.add(e.getValue());
		
		}
		combinationUtil(key.toArray(new Integer[key.size()]),key.size(), q1, 0, data2, 0,det,p1);
		return;
	}
	
	private void combinationUtil(Integer[] array,int n ,int r ,int index ,int[] data2, int i,HashMap<Integer,Double> det,int pric)
	{
		 	if (index == r)
		    {
		 		double sum=0;
		 		HashSet<Integer> temp=new HashSet<Integer>();
		 		ArrayList<Integer> t=new ArrayList<Integer>();
		 		for (int j=0; j<r; j++)
		 		{
		 			sum+=det.get(data2[j]);
		 		}
		 		if(sum>=0.9*pric && sum<=1.1*pric)
		 		{
		 			for(int j=0;j<r;j++)
			 		{
		 				temp.add(data2[j]);
			 		}
		 			if(temp.size()==q)
		 			{
		 				t.addAll(temp);
		 				i11.put(sum, t);
		 			}
			         return;
		 		}
		 		else
		 			return;
		    }
		   
		    if (i >= n)
		        return;
		    data2[index]=array[i];
		    combinationUtil(array, n, r, index+1, data2, i+1,det,pric);
		    combinationUtil(array, n, r, index, data2, i+1,det,pric);		
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
	}

}


