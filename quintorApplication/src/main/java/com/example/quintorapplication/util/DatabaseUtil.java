package com.example.quintorapplication.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DatabaseUtil {
    private String url;

    public DatabaseUtil() {
        this.url = "";
    }

    /**
     * Send post request to API
     * Could be made dynamic by adding url/object with headers
     * Also needs header for auth WIP
     * @param endpoint
     * @param body
     * @return
     * @throws Exception
     */
    public String postApiRequest(String endpoint, String body, String header) throws Exception {
        this.setUrl("http://localhost:8081/" + endpoint);
        HttpURLConnection connection = (HttpURLConnection) new URL(getUrl()).openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        OutputStream os = connection.getOutputStream();
        String params = header + "=" + body;
        os.write(params.getBytes()); //StandardCharsets.UTF_8


        return this.getResponse(connection);
    }

    /**
     * Upload MT940 file to parser with mode json/xml
     * @param file
     * @param mode
     * @return
     * @throws IOException
     */
    public String uploadMT940FileToParser(File file, String mode) throws IOException {
        if (mode.equals("JSON")) {
            this.setUrl("http://localhost:8080/MT940toJSON"); //http://localhost:8080/ must be link&port of parser
        } else {
            this.setUrl("http://localhost:8080/MT940toXML");
        }

        URL parser = new URL(getUrl());

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
     * @param httpURLConnection
     * @return
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

    /**
     * Getters & Setters
     */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
