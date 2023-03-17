package com.example.quintorapplication.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DatabaseUtil {
    private String url = "";

    public String uploadFileToParser(File file, String mode) throws IOException {
        if (mode == "JSON") {
            this.url = "http://localhost:8080/MT940toJSON";
        } else {
            this.url = "http://localhost:8080/MT940toXML";
        }

        URL api = new URL(this.url);

        HttpURLConnection httpURLConnection = (HttpURLConnection) api.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setDoOutput(true);

        OutputStream os = httpURLConnection.getOutputStream();

        String params = "file=" + file;

        os.write(params.getBytes());
        os.flush();
        os.close();

        return this.getResponse(httpURLConnection);
    }

    private String getResponse(HttpURLConnection httpURLConnection) throws IOException {
        if (httpURLConnection != null) {
            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                StringBuffer response = new StringBuffer();

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            }
        }

        return null;
    }
}
