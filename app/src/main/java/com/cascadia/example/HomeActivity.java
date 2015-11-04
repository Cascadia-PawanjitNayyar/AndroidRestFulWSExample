package com.cascadia.example;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Iterator;

/**
 * 
 * Home Screen Activity
 */
public class HomeActivity extends Activity {
	private final static String bookURI = "bookmarked/book/getallbooks";

	// Progress Dialog Object
	private ProgressDialog prgDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Displays Home Screen
		setContentView(R.layout.home);
		// Instantiate Progress Dialog object
		prgDialog = new ProgressDialog(this);
		// Set Progress Dialog Text
		prgDialog.setMessage("Please wait...");
		// Set Cancelable as False
		prgDialog.setCancelable(false);

	}

	@Override
	public void onBackPressed() {
		// prevent user from going back to login/register activity
		//super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// displays SettingsActivity when running on a phone
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Intent preferencesIntent = new Intent(this, SettingsActivity.class);
		preferencesIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(preferencesIntent);
		return super.onOptionsItemSelected(item);
	}

	public void onClicked(View view){
		// Instantiate Http Request Param Object
		RequestParams params = new RequestParams();
		invokeWS(params);
	}

	/**
	 * Method that performs RESTful webservice invocations
	 *
	 * @param params
	 */
	private void invokeWS(RequestParams params){
		// Show Progress Dialog
		prgDialog.show();
		// Make RESTful webservice call using AsyncHttpClient object
		AsyncHttpClient client = new AsyncHttpClient();
		String hostAddress = "http://" + Utility.getHostAddress(getApplicationContext()) + "/";

		client.get(hostAddress + bookURI, params, new AsyncHttpResponseHandler() {
			// When the response returned by REST has Http response code '200'
			@Override
			public void onSuccess(String response) {
				// Hide Progress Dialog
				prgDialog.hide();
				try {
					// JSON Object
					JSONArray jsonArray = new JSONArray(response);
					System.out.println("GetAllBooks returned: " + jsonArray.length() + " books");
					//try {
						// temporarily show raw data in textview
						TextView textView = (TextView) findViewById(R.id.bookTextView);

						// temporarily just dump the data to the textview
						textView.setText(jsonArray.toString());

						//getArray(jsonArray);
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
					// When the JSON response has status boolean value assigned with true
					if (response.length() > 0) {
						// Display successfully registered message using Toast
						Toast.makeText(getApplicationContext(), "You successfully queried all books", Toast.LENGTH_LONG).show();
					}
					// Else display error message
					/* else {
						System.out.println(obj.getString("error_msg"));
						Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
					} */
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
					e.printStackTrace();

				}
			}

			// When the response returned by REST has Http response code other than '200'
			@Override
			public void onFailure(int statusCode, Throwable error,
								  String content) {
				// Hide Progress Dialog
				prgDialog.hide();
				// When Http response code is '404'
				if (statusCode == 404) {
					Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
				}
				// When Http response code is '500'
				else if (statusCode == 500) {
					Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
				}
				// When Http response code other than 404, 500
				else {
					Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
				}
			}
		});
	}


	private void parseJson(JSONObject jsonObject) throws ParseException {

		//Set<Object> set = jsonObject.keySet();
		Iterator<Object> iterator = jsonObject.keys();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			try {
				if (jsonObject.get(obj.toString()) instanceof JSONArray) {
                    System.out.println(obj.toString());
                    //getArray(jsonObject.get(obj.toString()));
                } else {
                    if (jsonObject.get(obj.toString()) instanceof JSONObject) {
                        parseJson((JSONObject) jsonObject.get(obj.toString()));
                    } else {
                        System.out.println(obj.toString() + "\t"
                                + jsonObject.get(obj.toString()));
                    }
                }
			} catch (JSONException e) {
				System.out.println("JSONException in parseJSON. " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	//private void getArray(Object object2) throws ParseException {
	private void getArray(JSONArray jsonArr) throws ParseException {

		try {
			//JSONArray jsonArr = (JSONArray) object2;

			for (int k = 0; k < jsonArr.length(); k++) {

				try {
					if (jsonArr.getJSONObject(k) instanceof JSONObject) {
						System.out.println("BOOK " + k + ":");
						parseJson((JSONObject) jsonArr.get(k));
					} else {
						System.out.println("BOOK " + k + ":");
						System.out.print(jsonArr.get(k));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		} catch (Exception ex) {
			System.out.println("Exception in getArray:" + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void onAddClicked(View view) {
		Intent bookIntent = new Intent(this, BookActivity.class);
		bookIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(bookIntent);

	}
}
