package com.mimirandoms.arep1;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HttpServer {
    static Socket clientSocket;
    static PrintWriter out;
    static BufferedReader in;
    private static List<String> nombres = new ArrayList<>();

    public static void main(String[] args) throws IOException, URISyntaxException {
        int port = 35000;
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("No se pudo escuchar en el puerto: " + port + ".");
            System.exit(1);
        }

        while (true) {
            clientSocket = null;
            try {
                System.out.println("Servidor listo para recibir conexiones...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Error al aceptar la conexión.");
                System.exit(1);
            }

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String httpRequestLine;
            String resourcePath = null;
            boolean isFirstLine = true;
            URI requestUri = null;

            while ((httpRequestLine = in.readLine()) != null) {
                if (isFirstLine) {
                    requestUri = new URI(httpRequestLine.split(" ")[1]);
                    resourcePath = requestUri.getPath();
                    isFirstLine = false;
                }
                if (!httpRequestLine.isEmpty()) {
                    System.out.println("HTTP request: " + httpRequestLine);
                }
                if (!in.ready()) break;
            }

            if (Objects.requireNonNull(resourcePath).equals("/")) resourcePath = "/index.html";

            if (resourcePath.startsWith("/app")) handleApiEndpoints(resourcePath, requestUri);
            else serveStaticFile(resourcePath);

            out.close();
            in.close();
            clientSocket.close();
        }
    }


    private static void handleApiEndpoints(String path, URI requestURI) {
        if (path.endsWith("/postInfo")){
            String query = requestURI.getQuery();
            if (query != null) {
                String[] values = query.split("=");
                if (values.length > 1 && values[0].equals("name")) {
                    nombres.add(values[1]);
                }
            }
            out.print("HTTP/1.1 200 OK\r\n");
            out.print("Content-Type: application/json\r\n");
            out.print("\r\n");
            out.print("{\"status\":\"ok\"}");
            out.flush();


        } else if (path.endsWith("/getInfo")){
            String ultimo = nombres.isEmpty() ? "" : nombres.get(nombres.size() - 1);
            out.print("HTTP/1.1 200 OK\r\n");
            out.print("Content-Type: application/json\r\n");
            out.print("\r\n");
            out.print("{\"name\":\"" + ultimo + "\"}");
            out.flush();
        }
    }


    private static void serveStaticFile(String path) throws IOException {
        String basePath = "page";
        File file = new File(basePath + path);

        if (file.exists() && !file.isDirectory()) {
            String contentType = getMimeType(path);

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: " + contentType);
            out.println();
            out.flush();

            if (path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js")) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        out.println(line);
                    }
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
    }

    private static String getMimeType(String path) {
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
