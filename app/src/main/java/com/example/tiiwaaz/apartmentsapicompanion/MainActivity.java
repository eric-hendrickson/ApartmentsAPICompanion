package com.example.tiiwaaz.apartmentsapicompanion;

        import android.app.ProgressDialog;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.widget.ListAdapter;
        import android.widget.ListView;
        import android.widget.SimpleAdapter;
        import android.widget.Toast;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get contacts JSON
    private static String url = "https://placesapartmentapp.herokuapp.com/apartments.json";

    ArrayList<HashMap<String, String>> apartmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apartmentList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        new GetApartments().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetApartments extends AsyncTask<Void, Void, Void> {

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
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
//                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray apartments = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < apartments.length(); i++) {
                        HashMap<String, String> map = new HashMap<String, String>();
                        JSONObject a = apartments.getJSONObject(i);

                        String id = a.getString("id");
                        String latitude = a.getString("latitude");
                        String longitude = a.getString("longitude");
                        String address1 = a.getString("address1");
                        String address2 = a.getString("address2");
                        String city = a.getString("city");
                        String zip = a.getString("zip");
                        String state = a.getString("state");
                        String country = a.getString("country");
                        String name = a.getString("name");
                        String phoneNumber = a.getString("phone_number");
                        String hours = a.getString("hours");

                        // tmp hash map for single contact
                        HashMap<String, String> apartment = new HashMap<>();

                        // adding each child node to HashMap key => value
                        apartment.put("id", id);
                        apartment.put("name", name);
                        apartment.put("address1", address1);
                        apartment.put("address2", address2);
                        apartment.put("city", city);
                        apartment.put("state", state);
                        apartment.put("zip", zip);

                        // adding contact to contact list
                        apartmentList.add(apartment);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

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
                    MainActivity.this, apartmentList,
                    R.layout.list_item, new String[]{"name", "address1", "address2"}, new int[]{R.id.name, R.id.address1, R.id.address2});

            lv.setAdapter(adapter);
        }

    }
}
