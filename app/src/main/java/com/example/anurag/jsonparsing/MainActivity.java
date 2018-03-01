package com.example.anurag.jsonparsing;

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

    private String MARK = MainActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView lv;

    // URL to get marks JSON
    private static String url = "https://api.jsonbin.io/b/5a977642859c4e1c4d5da15d";

    ArrayList<HashMap<String, String>> marksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        marksList = new ArrayList<>();

        lv = (ListView) findViewById(R.id.list);

        new GetMarks().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetMarks extends AsyncTask<Void, Void, Void> {

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

            Log.e(MARK, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    // Getting JSON Array node
                    JSONArray marks = new JSONArray(jsonStr);

                    // looping through All Marks
                    for (int i = 0; i < marks.length(); i++) {
                        JSONObject c = marks.getJSONObject(i);

                        String STUDENT_NAME = c.getString("STUDENT_NAME");
                        String STUDENT_MARKS = c.getString("STUDENT_MARKS");

                        // tmp hash map for single marks
                        HashMap<String, String> markes = new HashMap<>();

                        // adding each child node to HashMap key => value
                        markes.put("STUDENT_NAME", STUDENT_NAME);
                        markes.put("STUDENT_MARKS", STUDENT_MARKS);

                        // adding marks to marks list
                        marksList.add(markes);
                    }
                } catch (final JSONException e) {
                    Log.e(MARK, "Json parsing error: " + e.getMessage());
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
                Log.e(MARK, "Couldn't get json from server.");
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
                    MainActivity.this, marksList,
                    R.layout.list_item, new String[]{"STUDENT_NAME", "STUDENT_MARKS"},
                    new int[]{R.id.name,
                    R.id.marks});

            lv.setAdapter(adapter);
        }

    }
}