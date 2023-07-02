package server;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client;
    private final String serverUrl;
    private String apiToken;


    public KVTaskClient(String url) {
        serverUrl = url;
        client = HttpClient.newHttpClient();
        try {
            URI register = URI.create(serverUrl + "register/");
            HttpRequest request = HttpRequest.newBuilder().uri(register).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                apiToken = response.body();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка во время инициализации KVTaskClient().");
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        KVServer kvs = new KVServer();
        kvs.start();
        KVTaskClient kvc = new KVTaskClient("http://localhost:8078/");
        System.out.println("API_TOKEN: " + kvc.apiToken);

        kvc.put("10", "\"object\":10");
        System.out.println("Loaded value:" + kvc.load("10"));
        kvc.put("20", "\"object\":20");
        System.out.println("Loaded value:" + kvc.load("20"));
        kvc.put("10", "\"object\":30");
        System.out.println("Loaded value:" + kvc.load("10"));

        kvs.stop();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        //запрос POST /save/<ключ>?API_TOKEN=
        URI saveKey = URI.create(serverUrl + "save/" + key + "?API_TOKEN=" + apiToken);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().POST(body).uri(saveKey).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String load(String key) throws IOException, InterruptedException {
        //запрос GET /load/<ключ>?API_TOKEN=
        URI loadKey = URI.create(serverUrl + "load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(loadKey).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
