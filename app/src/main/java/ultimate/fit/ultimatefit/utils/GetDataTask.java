package ultimate.fit.ultimatefit.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.data.generated.values.CategoriesValuesBuilder;
import ultimate.fit.ultimatefit.data.generated.values.ExercisesValuesBuilder;

/**
 * Created by Pham on 12/24/2015.
 */
public class GetDataTask extends AsyncTask<String, Void, List<String>> {
    public static int totalPage = 1;
    private final String LOG_TAG = GetDataTask.class.getSimpleName();
    private final Context mContext;
    private int pageNum;
    private String result = "results";
    private String mUrlString = "";
    private String additionalUrl;
    private boolean isCalledFromFirstPage = false;

    //currentUpdatedTime is for page from 2 onward. If this value is set then we ignore lastUpdatedTime
    //lastUpdatedTime is the time getting from SharedPreference
    private long currentUpdatedTime = 1;
    private long lastUpdatedTime;

    public GetDataTask(Context context, String additionalUrl) {
        mContext = context;
        this.additionalUrl = additionalUrl;
        if (!isOnline()) {
            displayNoInternetDialog();
        }
    }

    public GetDataTask(Context context, String additionalUrl, int pageNum) {
        mContext = context;
        this.additionalUrl = additionalUrl;
        this.pageNum = pageNum;
        if (!isOnline()) {
            displayNoInternetDialog();
        }
    }

    public GetDataTask(Context context, String additionalUrl, int pageNum, long currentUpdatedTime, boolean isCalledFromFirstPage) {
        mContext = context;
        this.currentUpdatedTime = currentUpdatedTime;
        this.additionalUrl = additionalUrl;
        this.pageNum = pageNum;
        this.isCalledFromFirstPage = isCalledFromFirstPage;
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
            lastUpdatedTime = SharedPreferenceHelper.getInstance(mContext).getLong(SharedPreferenceHelper.Key.LAST_EXERCISE_MODIFIED_DATE_LONG);
            if (lastUpdatedTime == 0) {
                lastUpdatedTime = 1000000;
            }
            if (currentUpdatedTime == 1)
                mUrlString += "/" + lastUpdatedTime + "/" + pageNum;
            else mUrlString += "/" + currentUpdatedTime + "/" + pageNum;
        } else {
            long lastUpdated = SharedPreferenceHelper.getInstance(mContext).getLong(SharedPreferenceHelper.Key.LAST_CATEGORY_MODIFIED_DATE_LONG);
            if (lastUpdated == 0) {
                lastUpdated = 1000000;
            }
            mUrlString += "/" + lastUpdated;
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
                        JSONArray jsonArrayMain = new JSONArray(result);

                        int len = jsonArrayMain.length();
                        ContentValues[] categoryValues = new ContentValues[len];
                        for (int i = 0; i < len; i++) {
                            JSONObject json = jsonArrayMain.getJSONObject(i);
                            int categoryId = json.getInt("pk");
                            JSONObject jsonFields = json.getJSONObject("fields");
                            String name = jsonFields.getString("name");
                            String imagePath = jsonFields.getString("image");
                            categoryValues[i] = new CategoriesValuesBuilder().id(categoryId).categoryName(name).imagePath(imagePath).values();
                        }
                        mContext.getContentResolver().bulkInsert(UltimateFitProvider.Categories.CONTENT_URI, categoryValues);
                    } catch (Exception e) {
                        // TODO: handle exception
                        Log.e("log_tag", "Error Parsing Data " + e.toString());
                    }
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonObjectMain = new JSONObject(result);
                        JSONArray jsonArrayMain = jsonObjectMain.getJSONArray("array");
                        totalPage = jsonObjectMain.getInt("page");

                        int len = jsonArrayMain.length();
                        ContentValues[] exerciseValues = new ContentValues[len];
                        for (int i = 0; i < len; i++) {
                            JSONObject jsonMain = jsonArrayMain.getJSONObject(i);
                            JSONObject json = jsonMain.getJSONObject("exercise");
                            int categoryId = json.getInt("category");
                            String name = json.getString("name");
                            String imagePath = json.getString("image");
                            String image2Path = json.getString("image2");
                            String videoPath = json.getString("video");
                            String description = json.getString("description");
                            exerciseValues[i] = new ExercisesValuesBuilder().categoryId(categoryId).imagePath(imagePath).image2Path(image2Path)
                                    .description(description).exerciseName(name).videoPath(videoPath).oneRepMax(0.0).values();
                        }
                        mContext.getContentResolver().bulkInsert(UltimateFitProvider.Exercises.CONTENT_URI, exerciseValues);
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
        if (Objects.equals(additionalUrl, Config.EXERCISE_URL) && pageNum == 1) {
            DateTime dateTime = new DateTime(DateTimeZone.UTC);
            long lastUpdated = dateTime.getMillis();
            SharedPreferenceHelper.getInstance().put(SharedPreferenceHelper.Key.LAST_EXERCISE_MODIFIED_DATE_LONG, lastUpdated);
        } else if (Objects.equals(additionalUrl, Config.CATEGORY_URL)) {
            DateTime dateTime = new DateTime(DateTimeZone.UTC);
            long lastUpdated = dateTime.getMillis();
            SharedPreferenceHelper.getInstance().put(SharedPreferenceHelper.Key.LAST_CATEGORY_MODIFIED_DATE_LONG, lastUpdated);
        }
        if (Objects.equals(additionalUrl, Config.EXERCISE_URL) && totalPage > 1 && pageNum == 1) {
                GetDataTask getDataTask = new GetDataTask(mContext, Config.EXERCISE_URL, pageNum + 1, lastUpdatedTime, true);
                getDataTask.execute();
        }
        else if (Objects.equals(additionalUrl, Config.EXERCISE_URL) && totalPage > 1 && isCalledFromFirstPage) {
            if(pageNum != totalPage) {
                GetDataTask getDataTask = new GetDataTask(mContext, Config.EXERCISE_URL, pageNum + 1, currentUpdatedTime, true);
                getDataTask.execute();
            }
        }
        if (pageNum == totalPage) {
            Intent intent = new Intent(MainActivity.DATA_DOWNLOADED);
            mContext.sendBroadcast(intent);
        }
    }
}
