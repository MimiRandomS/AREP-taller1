package com.mimirandoms.arep1;
import java.io.*;
import java.net.*;
import java.util.*;


public class URLReader {
    public static void main(String[] args) throws Exception {
        String requisite = "http://www.rule34.xxx";
        URL site = new URL(requisite);
        URLConnection urlConnection = site.openConnection();
        Map<String, List<String>> headers = urlConnection.getHeaderFields();
        Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();

        System.out.println("-------message-header------");

        for (Map.Entry<String, List<String>> entry : entrySet) {
            String headerName = entry.getKey();
            if(headerName !=null){System.out.print(headerName + ":");}
            List<String> headerValues = entry.getValue();
            for (String value : headerValues) {
                System.out.print(value);
                }
            System.out.println("");
        }

        System.out.println("-------message-body------");
        
        try (BufferedReader reader
                       = new BufferedReader(new InputStreamReader(site.openStream()))) {
            String inputLine = null;
            while ((inputLine = reader.readLine()) != null) {
                System.out.println(inputLine);
            }
            } catch (IOException x) {
            System.err.println(x);
            }

        System.out.println("-------message-info------");

        System.out.println("Protocol: " + site.getProtocol());
        System.out.println("Authority: " + site.getAuthority());
        System.out.println("Host: " + site.getHost());
        System.out.println("Path: " + site.getPath());
        System.out.println("Port: " + site.getPort());
        System.out.println("Query: " + site.getQuery());
        System.out.println("file: " + site.getFile());
        System.out.println("Ref: " + site.getRef());



    }
}