package in.co.nebulax.magpie;

import java.util.ArrayList;
import java.util.HashMap;

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
           // Creating service handler class instance
           ServiceHandler sh = new ServiceHandler();

           if(arg0[0] == 1){
        	// Making a request to url and getting response
               String jsonStr = sh.makeServiceCall(Constants.urlGet, ServiceHandler.GET);

               Log.d("Response: ", "> " + jsonStr);

               if (jsonStr != null) {
                   try {
                       JSONObject jsonObj = new JSONObject(jsonStr);
                        
                       // Getting JSON Array node
                       data = jsonObj.getJSONArray(Constants.TAG_DATA);

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

           }else{
        	   Log.v("AsyncCheck" , "2 entered so nothing displayed");
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

}
