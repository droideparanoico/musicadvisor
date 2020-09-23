package advisor.auth;

import advisor.config.Params;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class LocalServer {

    private static String accessServer;
    private static HttpServer httpServer;

    public LocalServer(String accessServer) {
        LocalServer.accessServer = accessServer;
    }

    public void startServer() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(8080), 0);
        httpServer.start();
    }

    public void getCode() throws InterruptedException {
        System.out.println("use this link to request the access code:");
        System.out.println(accessServer + Params.AUTHORIZE_PART
                + "?client_id=" + Params.CLIENT_ID
                + "&redirect_uri=" + Params.REDIRECT_URI
                + "&response_type=" + Params.RESPONSE_TYPE);
        System.out.println("waiting for code...");

        httpServer.createContext("/",
                exchange -> {
                    String code = exchange.getRequestURI().getQuery();
                    String result, answer;

                    if (code != null && code.contains("code")) {
                        Params.AUTH_CODE = code.substring(5);
                        result = "Got the code. Return back to your program.";
                        answer = "code received";
                    } else {
                        result = "Not found authorization code. Try again.";
                        answer = "code not received";
                    }

                    exchange.sendResponseHeaders(200, result.length());
                    exchange.getResponseBody().write(result.getBytes());
                    exchange.getResponseBody().close();

                    System.out.println(answer);
                }
        );
        while (Params.AUTH_CODE.equals("")) {
            Thread.sleep(10);
        }
    }

    public void stopServer() {
        httpServer.stop(10);
    }
}
