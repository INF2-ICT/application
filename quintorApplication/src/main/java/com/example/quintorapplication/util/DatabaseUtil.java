package com.example.quintorapplication.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUtil {

    /**
     * Send STRING post request to API
     * @param endpoint endpoint of API
     * @param headerBody Map with Header - Body Strings. For example "json", "{test: "test"}"
     * @return String output of API
     * @throws Exception if httpURLConnection failed
     */
    public String postApiRequest(String endpoint, HashMap<String, String> headerBody) throws Exception {
        String ApiUrl = "http://localhost:8083/" + endpoint;

        //Set connection
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(ApiUrl).openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setDoOutput(true);

        OutputStream os = httpURLConnection.getOutputStream();

        //Loop over HashMap with param & body
        for(Map.Entry<String, String> entry : headerBody.entrySet())
        {
            String param = entry.getKey();
            String body = entry.getValue();

            String params = param + "=" + body;
            os.write(params.getBytes()); //StandardCharsets.UTF_8
        }

        return this.getResponse(httpURLConnection);
    }

    /**
     * Upload MT940 file to parser with mode json/xml
     * @param file Uploaded MT940 file
     * @param endpoint xml / json endpoint
     * @return MT940 as string in XML/JSON format
     * @throws IOException When connection error
     */
    public String uploadMT940FileToParser(File file, String endpoint) throws IOException {
        String parserUrl = "http://localhost:8082/" + endpoint;

        URL parser = new URL(parserUrl);

        HttpURLConnection httpURLConnection = (HttpURLConnection) parser.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setDoOutput(true);

        String params = "file=" + file;

        OutputStream os = httpURLConnection.getOutputStream();
        os.write(params.getBytes());
        os.flush();
        os.close();

        return this.getResponse(httpURLConnection);
    }

    /**
     * Gets response from httpURLConnection
     * @param httpURLConnection The connection made with http
     * @return String of returned message
     * @throws IOException
     */
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
