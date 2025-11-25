import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.HttpURLConnection;

public class FileServer implements HttpHandler 
{
    public static void main (String[] args) {
        // Set the default port to 8080 and the root folder to the public folder
        int port = 8080;
        String rootFolder = "./public";

        // Parse command line arguments
        for (String arg : args) {
            if (arg.startsWith("-p")) {
                // If the argument starts with -p, set the port to the number following the -p
                port = Integer.parseInt(arg.substring(2));
            }
            else if (arg.equals("-h") || arg.equals("--help")) {
                // If the argument is -h or --help, print the usage and exit
                System.out.println("Usage: java FileServer [-pPORT] [ROOT_FOLDER]");
                System.exit(0);
            }
            else if (new File(arg).isDirectory()) {
                // If the argument is a directory, set the root folder to the argument
                rootFolder = arg;
            }
        }

        // Create and start a new FileServer to handle file requests
        try {
            final HttpServer server = buildFileServer(port, rootFolder);
            server.start();
        }
        catch (IOException e) {
            System.err.println(new Date() + " Error starting server: " + e);
            System.exit(1); 
        }

        System.out.println(new Date() + " Server listening at http://localhost:" + port);
    }

    public void handle(HttpExchange exchange) throws IOException {
        final String requestMethod = exchange.getRequestMethod();
        String requestPath = exchange.getRequestURI().getPath();

        // Log the request
        System.out.println(new Date() + " " + requestMethod + " request for: " + requestPath);

        // Attempt to serve a file from the public folder for any GET request
        if ("GET".equals(requestMethod)) {

            // If the request is for a folder, serve the index.html file it contains
            if (requestPath.endsWith("/")) {
                requestPath += "index.html";
            }


            // Serve the requested file
            handleFileRequest(exchange, requestPath);
        }
        else {
            // If the request is not a GET request, return a 405 Method Not Allowed error
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, 0);
            exchange.getResponseBody().close();
        }
    }
}