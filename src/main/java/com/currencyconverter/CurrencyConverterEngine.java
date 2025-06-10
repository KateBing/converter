package com.currencyconverter;

import java.util.Map;
import java.util.HashMap; // Added import
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyConverterEngine {

    public CurrencyConverterEngine() {
        // Constructor can be expanded if needed, e.g., to store rates if fetched once
    }

    public double convert(String fromCurrency, String toCurrency, double amount, Map<String, Double> rates) {
        if (amount <= 0) {
            return 0.0;
        }
        if (!rates.containsKey(fromCurrency) || !rates.containsKey(toCurrency)) {
            throw new IllegalArgumentException("Invalid currency code provided.");
        }

        double rateFrom = rates.get(fromCurrency); // Rate against EUR
        double rateTo = rates.get(toCurrency);     // Rate against EUR

        // Convert amount to EUR first (if it's not already EUR)
        double amountInEur;
        if (fromCurrency.equals("EUR")) {
            amountInEur = amount;
        } else {
            amountInEur = amount / rateFrom;
        }

        // Convert amount from EUR to target currency
        double convertedAmount;
        if (toCurrency.equals("EUR")) {
            convertedAmount = amountInEur;
        } else {
            convertedAmount = amountInEur * rateTo;
        }

        // Round to a reasonable number of decimal places, e.g., 2 for most currencies
        // Using BigDecimal for precision
        BigDecimal bd = new BigDecimal(convertedAmount);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double calculateFee(double amount) { // Assumes amount is the original amount to be converted
        // First, convert the original amount to EUR to determine the fee tier
        // This requires knowing the original currency and its rate to EUR.
        // For simplicity in this isolated method, let's assume the 'amount' parameter
        // for fee calculation is ALREADY THE EQUIVALENT IN EUR.
        // The main application flow will ensure this.

        double amountInEur = amount; // Directly use amount as amountInEur for this method

        if (amountInEur <= 0) {
            return 0.0;
        }

        double feePercentage;

        if (amountInEur < 100) {
            feePercentage = 0.05; // 5%
        } else {
            feePercentage = 0.05; // Start with 5%
            int hundredEuroBlocks = (int) (amountInEur / 100);
            for (int i = 0; i < hundredEuroBlocks; i++) {
                feePercentage *= (1 - 0.20); // Reduce by 20% of current percentage
            }
        }

        double feeAmount = amountInEur * feePercentage;

        // Round fee to 2 decimal places
        BigDecimal bd = new BigDecimal(feeAmount);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    // Main method for testing
    public static void main(String[] args) {
        CurrencyConverterEngine engine = new CurrencyConverterEngine();

        // Test fee calculation
        System.out.println("Fee for 50 EUR: " + engine.calculateFee(50));     // Expected: 2.5
        System.out.println("Fee for 100 EUR: " + engine.calculateFee(100));   // Expected: 4.0 (5% * 0.8 = 4%)
        System.out.println("Fee for 150 EUR: " + engine.calculateFee(150));   // Expected: 6.0 (150 * 0.04)
        System.out.println("Fee for 200 EUR: " + engine.calculateFee(200));   // Expected: 6.4 (200 * 0.032) (4% * 0.8 = 3.2%)
        System.out.println("Fee for 250 EUR: " + engine.calculateFee(250));   // Expected: 8.0 (250 * 0.032)
        System.out.println("Fee for 300 EUR: " + engine.calculateFee(300));   // Expected: 7.68 (300 * 0.0256) (3.2% * 0.8 = 2.56%)

        // Test conversion (requires dummy rates)
        Map<String, Double> testRates = new HashMap<>();
        testRates.put("USD", 1.1);
        testRates.put("GBP", 0.9);
        testRates.put("EUR", 1.0);

        System.out.println("Convert 100 EUR to USD: " + engine.convert("EUR", "USD", 100, testRates)); // Expected: 110.0
        System.out.println("Convert 100 USD to EUR: " + engine.convert("USD", "EUR", 100, testRates)); // Expected: 100 / 1.1 = 90.91
        System.out.println("Convert 100 USD to GBP: " + engine.convert("USD", "GBP", 100, testRates)); // Expected: (100 / 1.1) * 0.9 = 81.82
        System.out.println("Convert 0 USD to GBP: " + engine.convert("USD", "GBP", 0, testRates)); // Expected: 0.0
        try {
            System.out.println("Convert 100 XXX to GBP: " + engine.convert("XXX", "GBP", 100, testRates));
        } catch (IllegalArgumentException e) {
            System.out.println("Error (expected): " + e.getMessage());
        }
    }
}
