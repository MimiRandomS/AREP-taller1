package com.mimirandoms.arep1;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

public class HttpServer {
    public static void main(String[] args) throws IOException, URISyntaxException {
        int port = 35000;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port + ".");
            System.exit(1);
        }

        while (true) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            String path = null;
            String basePath = "page";
            boolean firstline = true;

            while ((inputLine = in.readLine()) != null) {
                if (firstline) {
                    URI requri = new URI(inputLine.split(" ")[1]);
                    path = requri.getPath();
                    firstline = false;
                }
                if (!inputLine.isEmpty()) System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
            if (Objects.requireNonNull(path).equals("/")) path = "/index.html";
            File file = new File(basePath + path);
            if (file.exists() && !file.isDirectory()) {
                String contentType = getFileExtension(path);
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: " + contentType);
                out.println();
                out.flush();

                if (path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js")) {
                    try (BufferedReader br = new BufferedReader(new FileReader(basePath + path))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            System.out.println(line);
                            out.println(line);
                        }

                    } catch (IOException e) {
                        System.err.println("Error leyendo archivo: " + basePath + path);
                        e.printStackTrace(System.err);
                    }
                } else {
                    byte[] fileBytes = Files.readAllBytes(file.toPath());
                    clientSocket.getOutputStream().write(fileBytes);
                    clientSocket.getOutputStream().flush();
                }
            } else {
                out.println("HTTP/1.1 404 Not Found\r\n");
                out.println("<h1>404 - Not Found</h1>");
            }
            out.close();
            in.close();
            clientSocket.close();
        }
    }

    private static String getFileExtension(String path) {
        String contentType = "text/html";
        if (path.endsWith(".css")) contentType = "text/css";
        else if (path.endsWith(".js")) contentType = "application/javascript";
        else if (path.endsWith(".jpeg")) contentType = "image/jpeg";
        else if (path.endsWith(".png")) contentType = "image/png";
        else if (path.endsWith(".ico")) contentType = "image/x-icon";
        else if (path.endsWith(".mp3")) contentType = "audio/mpeg";
        else if (path.endsWith(".mp4")) contentType = "video/mp4";
        return contentType;
    }
}
