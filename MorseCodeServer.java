import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MorseCodeServer {

    private static final Map<Character, String> ENC = new HashMap<>();
    private static final Map<String, Character> DEC = new HashMap<>();

    static {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        String[] morse = {
                ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..", ".---",
                "-.-", ".-..", "--", "-.", "---", ".--.", "--.-", ".-.", "...", "-",
                "..-", "...-", ".--", "-..-", "-.--", "--..",
                ".----", "..---", "...--", "....-", ".....",
                "-....", "--...", "---..", "----.", "-----"
        };

        for (int i = 0; i < alphabet.length(); i++) {
            ENC.put(alphabet.charAt(i), morse[i]);
            DEC.put(morse[i], alphabet.charAt(i));
        }

        // Space between words -> slash
        ENC.put(' ', "/");
        DEC.put("/", ' ');
    }

   public static void main(String[] args) throws Exception {
    int port = 8081; // default
    if (args.length > 0) {
        port = Integer.parseInt(args[0]);
    }
    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/encode", exchange -> {
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            handlePreflight(exchange);
                return;
            }
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCors(exchange, 405, "Method Not Allowed");
                return;
            }
            String body = readBody(exchange);
            String result = encode(body);
            sendText(exchange, 200, result);
        });

        // Decode endpoint
        server.createContext("/decode", exchange -> {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                handlePreflight(exchange);
                return;
            }
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCors(exchange, 405, "Method Not Allowed");
                return;
            }
            String body = readBody(exchange);
            String result;
            try {
                result = decode(body);
            } catch (IllegalArgumentException ex) {
                result = "Error: " + ex.getMessage();
                sendText(exchange, 400, result);
                return;
            }
            sendText(exchange, 200, result);
        });

        server.setExecutor(null);
        System.out.println("MorseCodeServer running on http://localhost:" + port + "/");
        server.start();
    }

    private static String encode(String input) {
        if (input == null) return "";
        StringBuilder out = new StringBuilder();
        for (char ch : input.toUpperCase().toCharArray()) {
            if (ENC.containsKey(ch)) {
                out.append(ENC.get(ch)).append(' ');
            }
        }
        return out.toString().trim();
    }

    private static String decode(String morse) {
        if (morse == null || morse.trim().isEmpty()) return "";
        StringBuilder out = new StringBuilder();
        String[] tokens = morse.trim().replaceAll("\\s+", " ").split(" ");
        for (String token : tokens) {
            Character c = DEC.get(token);
            if (c != null) {
                out.append(c);
            } else {
                throw new IllegalArgumentException("Unknown Morse token: '" + token + "'");
            }
        }
        return out.toString();
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            byte[] buf = is.readAllBytes();
            return new String(buf, StandardCharsets.UTF_8).trim();
        }
    }

    private static void handlePreflight(HttpExchange exchange) throws IOException {
        Headers h = exchange.getResponseHeaders();
        addCors(h);
        h.add("Access-Control-Allow-Methods", "POST, OPTIONS");
        h.add("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
    }

    private static void sendText(HttpExchange exchange, int code, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        Headers h = exchange.getResponseHeaders();
        h.add("Content-Type", "text/plain; charset=utf-8");
        addCors(h);
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendCors(HttpExchange exchange, int code, String msg) throws IOException {
        byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
        Headers h = exchange.getResponseHeaders();
        addCors(h);
        h.add("Content-Type", "text/plain; charset=utf-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void addCors(Headers h) {
        h.add("Access-Control-Allow-Origin", "*");
    }
}
