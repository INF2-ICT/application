package com.example.quintorapplication.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


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
     * Function to send a GET request to API
     * @param endpoint endpoint of api
     * @return api message
     * @throws Exception
     */
    public String getApiRequest(String endpoint) throws Exception {
        String ApiUrl = "http://localhost:8083/" + endpoint;

        //Set connection
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(ApiUrl).openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("Accept", "application/json");

        int responseCode = httpURLConnection.getResponseCode();
        StringBuilder responseText = new StringBuilder();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader((httpURLConnection.getInputStream())));

            String output;

            while ((output = br.readLine()) != null) {
                responseText.append(output);
            }

            br.close();
        }

        httpURLConnection.disconnect();

        return responseText.toString();
    }

    /**
     * Upload MT940 file to parser with mode json/xml
     * @param file Uploaded MT940 file
     * @param endpoint xml / json endpoint
     * @return MT940 as string in XML/JSON format
     * @throws IOException When connection error
     */
    public String uploadMT940FileToParser(File file, String endpoint) throws IOException {
        // Prepare the request
        String apiUrl = "http://localhost:8082/" + endpoint; // Replace with your actual API endpoint URL
        String boundary = UUID.randomUUID().toString();
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        // Write the file data to the request body
        try (OutputStream outputStream = connection.getOutputStream()) {
            writeFormFieldForMultipartFile(outputStream, "file", file, boundary);
        }

        // Get the response from the API
        int responseCode = connection.getResponseCode();
        String responseMessage = connection.getResponseMessage();
        String responseBody = null;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream inputStream = connection.getInputStream()) {
                StringBuilder responseBuilder = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                }
                responseBody = responseBuilder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Process the response if needed
        //System.out.println("Response code: " + responseCode);
        //System.out.println("Response message: " + responseMessage);
        //System.out.println("Response body: " + responseBody);

        return responseBody;
    }

    private void writeFormFieldForMultipartFile(OutputStream outputStream, String fieldName, File file, String boundary) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("--").append(boundary).append("\r\n");
        builder.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"; filename=\"").append(file.getName()).append("\"\r\n");
        builder.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(file.getName())).append("\r\n");
        builder.append("\r\n");

        outputStream.write(builder.toString().getBytes());

        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.write("\r\n".getBytes());
        }

        outputStream.write(("--" + boundary + "--\r\n").getBytes());
    }

    public String deleteApiRequest(String endpoint, HashMap<String, String> headerParams) throws IOException {
        String apiUrl = "http://localhost:8083/" + endpoint;

        // Set connection
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        OutputStream os = connection.getOutputStream();

        // Loop over HashMap with header parameters
        for (Map.Entry<String, String> entry : headerParams.entrySet()) {
            String param = entry.getKey();
            String value = entry.getValue();

            String header = param + "=" + value;
            os.write(header.getBytes());
        }

        return getResponse(connection);
    }

    /**
     * Send STRING put request to API
     *
     * @param endpoint   endpoint of API
     * @param queryParams Map with query parameters
     * @return String output of API
     * @throws Exception if httpURLConnection failed
     */
    public String putApiRequest(String endpoint, HashMap<String, String> queryParams) throws Exception {
        String apiUrl = "http://localhost:8083/" + endpoint;

        // Build query parameters
        StringBuilder queryString = new StringBuilder();
        if (queryParams != null && !queryParams.isEmpty()) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                queryString.append("&").append(key).append("=").append(URLEncoder.encode(value, "UTF-8"));
            }
        }

        // Set connection
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl + "?" + queryString.substring(1)).openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        return getResponse(connection);
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
