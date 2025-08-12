
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ConversorMonedas {
    private static final String API_KEY = "f9fe4ff58d068508bc97ec67";
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + "f9fe4ff58d068508bc97ec67" + "/latest/";

    private static JsonObject getRates(String baseCurrency) {
        try {
            String urlStr = API_URL + baseCurrency.toUpperCase();
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JsonObject jsonObj = JsonParser.parseString(response.toString()).getAsJsonObject();

            if (!jsonObj.has("conversion_rates")) {
                System.out.println("⚠ Error: No se encontraron tasas para la moneda base " + baseCurrency);
                return null;
            }

            return jsonObj.getAsJsonObject("conversion_rates");
        } catch (Exception e) {
            System.out.println("⚠ Error al conectar con la API: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Consultar tasas de cambio");
            System.out.println("2. Convertir monto");
            System.out.println("3. Listar monedas populares");
            System.out.println("0. Salir");
            System.out.print("Selecciona una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> {
                    System.out.print("Moneda base (ej: USD, EUR, MXN): ");
                    String base = scanner.nextLine();
                    JsonObject rates = getRates(base);
                    if (rates != null) {
                        rates.entrySet().forEach(entry ->
                                System.out.println(entry.getKey() + ": " + entry.getValue()));
                    }
                }
                case 2 -> {
                    System.out.print("Monto: ");
                    double monto = scanner.nextDouble();
                    scanner.nextLine();
                    System.out.print("Moneda base (ej: USD, EUR, MXN): ");
                    String base = scanner.nextLine();
                    System.out.print("Moneda destino: ");
                    String destino = scanner.nextLine();

                    JsonObject rates = getRates(base);
                    if (rates != null && rates.has(destino.toUpperCase())) {
                        double tasa = rates.get(destino.toUpperCase()).getAsDouble();
                        System.out.println("Resultado: " + (monto * tasa) + " " + destino.toUpperCase());
                    } else {
                        System.out.println("⚠ No se encontró la moneda destino.");
                    }
                }
                case 3 -> {
                    System.out.print("Moneda base (ej: USD, EUR, MXN): ");
                    String base = scanner.nextLine();
                    JsonObject rates = getRates(base);
                    if (rates != null) {
                        String[] populares = {"USD", "EUR", "MXN", "GBP", "JPY", "BRL"};
                        for (String moneda : populares) {
                            if (rates.has(moneda)) {
                                System.out.println(moneda + ": " + rates.get(moneda).getAsDouble());
                            }
                        }
                    }
                }
                case 0 -> System.out.println("👋 Saliendo...");
                default -> System.out.println("⚠ Opción inválida.");
            }
        } while (opcion != 0);
    }
}

