package in.co.nebulax.magpie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends ListActivity implements OnItemClickListener{

	public ConnectionDetector cd;
	public Boolean isInternetPresent;
	
	private ProgressDialog pDialog;
	
	//datat JSONArray
	JSONArray data = null;
	
	//Hashmap for ListView
	ArrayList<HashMap<String, String>> deviceList;
	
	//Hashmap for adding varibles for StatusChange
	List<NameValuePair> statusChange;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		deviceList = new ArrayList<HashMap<String,String>>();
		
		ListView lv = getListView();
		lv.setOnItemClickListener(this);
			
		cd = new ConnectionDetector(getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();
		
		if(!isInternetPresent) {
			Toast.makeText(this,"Unable to establish a connection", Toast.LENGTH_LONG).show();
		} else {
			
			new DataHandler().execute(1);
		}
	//	Toast.makeText(this, "internet "+isInternetPresent,Toast.LENGTH_LONG).show();
		
	}
	
	//Async Task class to get JSON by making HTTP call
	 private class DataHandler extends AsyncTask<Integer, Void, Void> {
		 
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
           // Showing progress dialog
           pDialog = new ProgressDialog(MainActivity.this);
           pDialog.setMessage("Please wait...");
           pDialog.setCancelable(false);
           pDialog.show();

       }
       
       @Override
       protected Void doInBackground(Integer... arg0) {
         

           if(arg0[0] == 1){
        	   getData();
           }else{
        	   postData();
           }
           
           return null;
       }
       
       @Override
       protected void onPostExecute(Void result) {
           super.onPostExecute(result);
           // Dismiss the progress dialog
           if (pDialog.isShowing())
               pDialog.dismiss();
           /**
            * Updating parsed JSON data into ListView
            * */
           ListAdapter adapter = new SimpleAdapter(
                   MainActivity.this, deviceList,
                   R.layout.list_item, new String[] { Constants.TAG_DEVICE, Constants.TAG_NAME,
                           Constants.TAG_STATUS }, new int[] { R.id.device,
                           R.id.name, R.id.status });

           setListAdapter(adapter);
       }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub

		Toast.makeText(this, deviceList.get(position).get(Constants.TAG_DEVICE),
				Toast.LENGTH_SHORT).show();		
		statusChange = new ArrayList<NameValuePair>();
		statusChange.add(new BasicNameValuePair("deviceId", 
				deviceList.get(position).get(Constants.TAG_NAME)));
		statusChange.add(new BasicNameValuePair("newStatus",
				changeStatus(deviceList.get(position).get(Constants.TAG_STATUS))));
		
		new DataHandler().execute(2);
	}
	
	public void getData(){
		
		// Creating service handler class instance
        	ServiceHandler sh = new ServiceHandler();

    	// Making a request to url and getting response
           String jsonStr = sh.makeServiceCall(Constants.urlGet, ServiceHandler.GET);
           Log.v("jsonStr" , jsonStr);
           Log.d("Response: ", "> " + jsonStr);

           if (jsonStr != null) {
               try {
                  // JSONObject jsonObj = new JSONObject(jsonStr);                       
                   // Getting JSON Array node
                   //data = jsonObj.getJSONArray(jsonStr);
            	   
            	   data = new JSONArray(jsonStr);
            	   Log.v("jsonStr" , data.toString());
                   // looping through All Contacts
                   for (int i = 0; i < data.length(); i++) {
                       JSONObject c = data.getJSONObject(i);
                        
                       String device = c.getString(Constants.TAG_DEVICE);
                       String name = c.getString(Constants.TAG_NAME);
                       String status = c.getString(Constants.TAG_STATUS);
                      

                       // tmp hashmap for single contact
                       HashMap<String, String> object = new HashMap<String, String>();

                       // adding each child node to HashMap key => value
                       object.put(Constants.TAG_DEVICE,device);
                       object.put(Constants.TAG_NAME,name);
                       object.put(Constants.TAG_STATUS, status);
                      

                       // adding contact to contact list
                       deviceList.add(object);
                   }
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           } else {
               Log.e("ServiceHandler", "Couldn't get any data from the url");
           }   
	}
	
	public void postData() {

		// Creating service handler class instance
    	ServiceHandler sh = new ServiceHandler();
    	
    	// Making a request to url and getting response
        String jsonStr = sh.makeServiceCall(Constants.urlPost, ServiceHandler.GET , statusChange);
        Log.v("jsonStr" , jsonStr);
        Log.d("Response: ", "> " + jsonStr);
	}
	
	public String changeStatus(String s){
		
		if(s.equalsIgnoreCase("on")){
			return "off";
		}else {
			return "on";
		}
		
	}
	
	

}
