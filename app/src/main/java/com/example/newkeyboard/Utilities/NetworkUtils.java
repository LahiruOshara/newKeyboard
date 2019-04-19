package com.example.newkeyboard.Utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class NetworkUtils {

    private final static String apiURL="https://depression-score-j5cygkj4rq-uc.a.run.app/get-score"; /*GITHUB_BASE_URL ="https://api.github.com/search/repositories";*/

    private final static String PARAM_QUERY = "string";

    /*
     * The sort field. One of stars, forks, or updated.
     * Default: results are sorted by best match if no field is specified.
     */

    public static URL buildUrl(String keyBoardText) {
        Uri builtUri = Uri.parse(apiURL).buildUpon()
                //.appendQueryParameter(PARAM_QUERY, keyBoardText)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.d("url",url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        Log.d("network utils","sending");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Log.d("network utils","connected");
        urlConnection.setRequestMethod("POST");
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}