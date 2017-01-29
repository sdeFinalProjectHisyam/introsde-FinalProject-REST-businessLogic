package introsde.rest.businessLogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;


import org.json.*;



 // @Stateless // will work only inside a Java EE application
 // @LocalBean // will work only inside a Java EE application
@Path("/businessLogic")
public class BusinessLogic {

//Getting person's details
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getPersonDetails")
    public Response getPersonDetails() throws ClientProtocolException, IOException {
        
        //String ENDPOINT = "http://10.218.204.124:5900/introsde/storage/getPersonDetails";
    	String ENDPOINT = "http://sdestoragehisyam.herokuapp.com/introsde/storage/getPersonDetails";
        
    	DefaultHttpClient client = new DefaultHttpClient();
     	HttpGet request = new HttpGet(ENDPOINT);
     	HttpResponse response = client.execute(request);
    	
     	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

     	StringBuffer result = new StringBuffer();
     	String line = "";
     	while ((line = rd.readLine()) != null) {
     	    result.append(line);
     	}
    	
     	JSONObject o = new JSONObject(result.toString());
    	
     	if(response.getStatusLine().getStatusCode() == 200){
     		return Response.ok(o.toString()).build();
         }
    	
     	return Response.status(204).build();
    	
     }

    //Getting person's goals
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getGoals")
    public Response getGoals() throws ClientProtocolException, IOException {
        
        //String ENDPOINT = "http://10.218.204.124:5900/introsde/storage/getGoals";
    	String ENDPOINT = "http://sdestoragehisyam.herokuapp.com/introsde/storage/getGoals";
    	
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(ENDPOINT);

        HttpResponse response = client.execute(request);
        
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));


        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        JSONArray list = new JSONArray(result.toString());
        if(response.getStatusLine().getStatusCode() == 200){
            return Response.ok(list.toString()).build();
        }

        return Response.status(204).build();
    }

     //Comparing the goals and life status measures
     @GET
     @Produces(MediaType.APPLICATION_JSON)
     @Consumes(MediaType.APPLICATION_JSON)
     @Path("/compare/{measure}")
     public Response getResultComparison(@PathParam("measure") String measure) throws ClientProtocolException, IOException {

         int lifeStatusValue = -1;
         int goalValue = -1;
         String comparison = "";

// a) Getting the goals
         //String ENDPOINT = "http://10.218.204.124:5900/introsde/storage/getGoals/";
         String ENDPOINT = "http://sdestoragehisyam.herokuapp.com/introsde/storage/getGoals";
        
         DefaultHttpClient client = new DefaultHttpClient();
         HttpGet request = new HttpGet(ENDPOINT);
         HttpResponse response = client.execute(request);

         BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

         StringBuffer result = new StringBuffer();
         String line = "";
         while ((line = rd.readLine()) != null) {
             result.append(line);
         }

         JSONArray o = new JSONArray(result.toString());

         for (int i = 0; i < o.length(); i++) {
             if ((o.getJSONObject(i).getString("type")).equals(measure)) {
                 goalValue = o.getJSONObject(i).getInt("value");


             }
         }


         // b) Getting lifestatus measures

         //String ENDPOINT2 = "http://10.218.204.124:5900/introsde/storage/getPersonDetails";
         String ENDPOINT2 = "http://sdestoragehisyam.herokuapp.com/introsde/storage/getPersonDetails";
         DefaultHttpClient client2 = new DefaultHttpClient();
         HttpGet request2 = new HttpGet(ENDPOINT2);
         HttpResponse response2 = client2.execute(request2);


         BufferedReader rd2 = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));


         StringBuffer result2 = new StringBuffer();
         String line2 = "";
         while ((line2 = rd2.readLine()) != null) {
             result2.append(line2);
         }



         JSONObject o2 = new JSONObject(result2.toString());



         for(int i = 0; i < o2.getJSONArray("lifeStatus").length(); i++){
             if(o2.getJSONArray("lifeStatus").getJSONObject(i).getString("measure").equals(measure)){
                 lifeStatusValue = o2.getJSONArray("lifeStatus").getJSONObject(i).getInt("value");
             }
         }

         // c) Comparing the measures of goals and lifestatus

         if(lifeStatusValue == -1 || goalValue == -1){

             return Response.status(404).build();
         }

         if(lifeStatusValue >= goalValue){
             comparison = "ok";
         }else{
             comparison = "notOk";
         }

         String textXml = "";
        textXml = "<comparisonInfo>";
        textXml += "<measure>"+measure+"</measure>";
        textXml += "<lifeStatusValue>"+lifeStatusValue+"</lifeStatusValue>";
        textXml += "<goalValue>"+goalValue+"</goalValue>";
        textXml += "<result>"+comparison+"</result>";
        textXml += "</comparisonInfo>";
        
        JSONObject xmlJSONObj = XML.toJSONObject(textXml);
        String jsonPrettyPrintString = xmlJSONObj.toString(4);
        
        return Response.ok(jsonPrettyPrintString).build();
   
    }

 }