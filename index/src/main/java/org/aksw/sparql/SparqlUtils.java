package org.aksw.sparql;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Properties;


public class SparqlUtils {

    /**
     *
     * @param query
     * @param encoding
     * @param defaultGraph
     * @return
     * @throws UnsupportedEncodingException
     */
    public String getUrlParameters(String query, String encoding, String defaultGraph) throws UnsupportedEncodingException {

        Properties parameters = new Properties();

        parameters.setProperty("query", URLEncoder.encode(query, encoding));
        parameters.setProperty("default-graph-uri", URLEncoder.encode(defaultGraph, encoding));
        parameters.setProperty("timeout", "30000");
        parameters.setProperty("debug", "on");

        Iterator iterator = parameters.keySet().iterator();

        int first = 0;

        StringBuilder buffer = new StringBuilder();

        while (iterator.hasNext()) {
            String name = (String) iterator.next();
            String value = parameters.getProperty(name);

            if (first != 0)
                buffer.append("&");

            buffer.append(name);
            buffer.append("=");
            buffer.append(value);

            first += 1;
        }

        return buffer.toString();

    }

    /**
     *
     * @param endpoint
     * @param urlParameters
     * @return
     * @throws IOException
     */
    public String getContent(String endpoint, String urlParameters) throws IOException {

        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Request-Method", "POST");
        connection.setRequestProperty("Accept-Charset", "utf-8");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
        connection.setRequestMethod("POST");
        connection.setInstanceFollowRedirects(false);
        connection.setUseCaches(false);

        connection.setConnectTimeout(90000);
        connection.setReadTimeout(1800000);

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.connect();

        OutputStreamWriter bufferWriter = new OutputStreamWriter(connection.getOutputStream());
        bufferWriter.write(urlParameters);
        bufferWriter.flush();

        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder buffer = new StringBuilder();
        String line = "";

        while ((line = bufferReader.readLine()) != null) {
            buffer.append(line);
        }

        bufferReader.close();
        bufferWriter.close();

        return buffer.toString();

    }
}

