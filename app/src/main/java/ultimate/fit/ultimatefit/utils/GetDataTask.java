package ultimate.fit.ultimatefit.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.activity.MainActivity;
import ultimate.fit.ultimatefit.data.CategoryColumns;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.data.generated.values.CategoriesValuesBuilder;
import ultimate.fit.ultimatefit.data.generated.values.WorkoutsValuesBuilder;

/**
 * Created by Pham on 12/24/2015.
 */
public class GetDataTask extends AsyncTask<String, Void, List<String>> {
    private final String LOG_TAG = GetDataTask.class.getSimpleName();
    private final Context mContext;
    private String result = "results";
    private String mUrlString = "";
    private String additionalUrl;
    private int pageNum;
    private int totalPages = 0;

    public GetDataTask(Context context, String additionalUrl) {
        mContext = context;
        this.additionalUrl = additionalUrl;
        if (!isOnline()) {
            displayNoInternetDialog();
        }
    }

    private void displayNoInternetDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(mContext.getString(R.string.alert_title))
                    .setMessage(mContext.getString(R.string.alert_message))
                    .setCancelable(false)
                    .setNegativeButton(mContext.getString(R.string.alert_button), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            Log.d(MainActivity.class.getSimpleName(), "Show Dialog: " + e.getMessage());
        }
    }

    //Check whether there is an internet connection
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected List<String> doInBackground(String... strings) {
        mUrlString = Config.MAIN_URL;
        mUrlString = mUrlString + additionalUrl;
        if (Objects.equals(additionalUrl, Config.EXERCISE_URL)) {
            mUrlString += "/" + pageNum;
        }
        List<String> jsonData = new ArrayList<>();
        URL url;
        try {
            url = new URL(mUrlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            result = sb.toString();

            //parse json data
            parseJsonData(jsonData);
            Log.i(LOG_TAG, "URL Queried: " + mUrlString);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return jsonData;
    }

    private void parseJsonData(List<String> jsonArray) {

        if (Objects.equals(additionalUrl, Config.CATEGORY_URL)) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final JSONArray jsonArrayMain = new JSONArray(result);

                        final int len = jsonArrayMain.length();
                        ContentValues[] categoryValues = new ContentValues[len];
                        for (int i = 0; i < len; i++) {
                            JSONObject json = jsonArrayMain.getJSONObject(i);
                            int categoryId = json.getInt("pk");
                            JSONObject jsonFields = json.getJSONObject("fields");
                            String name = jsonFields.getString("name");
                            String imagePath = jsonFields.getString("image");
                            categoryValues[i] = new CategoriesValuesBuilder().categoryId(categoryId).categoryName(name).imagePath(imagePath).values();
                        }
                        mContext.getContentResolver().bulkInsert(UltimateFitProvider.Categories.CONTENT_URI, categoryValues);
                    } catch (Exception e) {
                        // TODO: handle exception
                        Log.e("log_tag", "Error Parsing Data " + e.toString());
                    }
                }
            }).start();
        }

    }

    @Override
    protected void onPostExecute(List<String> strings) {

    }
}
