package com.currencyconverter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class EcbApiHandler {

    private static final String ECB_RATES_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    public Map<String, Double> getExchangeRates() {
        Map<String, Double> rates = new HashMap<>();
        try {
            URL url = new URL(ECB_RATES_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/xml");

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(connection.getInputStream());
            doc.getDocumentElement().normalize();

            NodeList cubeNodes = doc.getElementsByTagName("Cube");

            for (int i = 0; i < cubeNodes.getLength(); i++) {
                Element cubeNode = (Element) cubeNodes.item(i);
                if (cubeNode.hasAttribute("currency") && cubeNode.hasAttribute("rate")) {
                    String currency = cubeNode.getAttribute("currency");
                    double rate = Double.parseDouble(cubeNode.getAttribute("rate"));
                    rates.put(currency, rate);
                }
            }
            connection.disconnect();
        } catch (Exception e) {
            // In a real app, log this exception properly
            e.printStackTrace();
            // Return empty or cached rates, or rethrow as custom exception
            return new HashMap<>(); // Return empty map on error for now
        }
        // Add EUR itself for conversion from/to EUR
        rates.put("EUR", 1.0);
        return rates;
    }

    // Optional: Main method for quick testing
    public static void main(String[] args) {
        EcbApiHandler handler = new EcbApiHandler();
        Map<String, Double> rates = handler.getExchangeRates();
        if (rates.isEmpty()) {
            System.out.println("Failed to fetch rates.");
        } else {
            System.out.println("Fetched Exchange Rates (against EUR):");
            for (Map.Entry<String, Double> entry : rates.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }
}
