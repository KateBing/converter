package com.currencyconverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.math.BigDecimal; // For precise EUR equivalent calculation for fee
import java.math.RoundingMode; // For precise EUR equivalent calculation for fee


public class CurrencyConverterApp {

    private JFrame frame;
    private JComboBox<String> baseCurrencyComboBox;
    private JTextField amountTextField;
    private JComboBox<String> targetCurrencyComboBox;
    private JButton convertButton;
    private JLabel convertedAmountLabel;
    private JLabel feeAmountLabel;
    private JLabel errorLabel; // For displaying error messages

    private EcbApiHandler ecbApiHandler;
    private CurrencyConverterEngine converterEngine;
    private Map<String, Double> exchangeRates;

    public CurrencyConverterApp() {
        ecbApiHandler = new EcbApiHandler();
        converterEngine = new CurrencyConverterEngine();
        // Fetch rates once on startup.
        loadExchangeRates();

        initializeUI();
        populateCurrencies(); // This should be called after rates are loaded
    }

    private void loadExchangeRates() {
        exchangeRates = ecbApiHandler.getExchangeRates();
        if (exchangeRates == null || exchangeRates.isEmpty()) {
            // This case should ideally be handled more robustly in initializeUI or by disabling components
            System.err.println("Critical: Failed to load exchange rates on startup.");
            // Potentially show a dialog and exit, or disable functionality
        }
    }

    private void initializeUI() {
        frame = new JFrame("Currency Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Base Currency Row
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(new JLabel("From Currency:"), gbc);

        baseCurrencyComboBox = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        frame.add(baseCurrencyComboBox, gbc);
        gbc.weightx = 0.0;

        // Amount Row
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(new JLabel("Amount:"), gbc);

        amountTextField = new JTextField(10);
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(amountTextField, gbc);

        // Target Currency Row
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(new JLabel("To Currency:"), gbc);

        targetCurrencyComboBox = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(targetCurrencyComboBox, gbc);

        // Convert Button Row
        convertButton = new JButton("Convert");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(convertButton, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 1;

        // Converted Amount Row
        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(new JLabel("Converted Amount:"), gbc);

        convertedAmountLabel = new JLabel("0.00");
        gbc.gridx = 1;
        gbc.gridy = 4;
        frame.add(convertedAmountLabel, gbc);

        // Fee Amount Row
        gbc.gridx = 0;
        gbc.gridy = 5;
        frame.add(new JLabel("Conversion Fee:"), gbc);

        feeAmountLabel = new JLabel("0.00 EUR"); // Explicitly state fee is in EUR
        gbc.gridx = 1;
        gbc.gridy = 5;
        frame.add(feeAmountLabel, gbc);

        // Error Label Row
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        frame.add(errorLabel, gbc);
        gbc.gridwidth = 1;

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        });

        // Disable button if rates are not loaded
        if (exchangeRates == null || exchangeRates.isEmpty()) {
            convertButton.setEnabled(false);
            baseCurrencyComboBox.setEnabled(false);
            targetCurrencyComboBox.setEnabled(false);
            amountTextField.setEnabled(false);
            errorLabel.setText("Error: Exchange rates not available. Please restart.");
        }


        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void populateCurrencies() {
        if (exchangeRates == null || exchangeRates.isEmpty()) {
            // Error already handled in initializeUI by disabling components
            // and setting errorLabel text if rates failed to load at startup.
            return;
        }

        Set<String> currencyCodesSet = exchangeRates.keySet();
        ArrayList<String> currencyCodes = new ArrayList<>(currencyCodesSet);
        Collections.sort(currencyCodes);

        for (String code : currencyCodes) {
            baseCurrencyComboBox.addItem(code);
            targetCurrencyComboBox.addItem(code);
        }

        if (currencyCodes.contains("EUR")) {
            baseCurrencyComboBox.setSelectedItem("EUR");
        }
        if (currencyCodes.contains("USD")) {
            targetCurrencyComboBox.setSelectedItem("USD");
        }
    }

    private void performConversion() {
        errorLabel.setText(""); // Clear previous errors
        convertedAmountLabel.setText("0.00");
        feeAmountLabel.setText("0.00 EUR");

        String baseCurrency = (String) baseCurrencyComboBox.getSelectedItem();
        String targetCurrency = (String) targetCurrencyComboBox.getSelectedItem();
        String amountStr = amountTextField.getText().trim();

        if (baseCurrency == null || targetCurrency == null || amountStr.isEmpty()) {
            errorLabel.setText("Please select currencies and enter an amount.");
            return;
        }

        if (exchangeRates == null || exchangeRates.isEmpty()) {
             errorLabel.setText("Exchange rates not available. Cannot perform conversion.");
             // Attempt to reload rates - might be too disruptive or complex for this simple app's flow
             // For now, we rely on initial load.
             return;
        }

        double amountInput;
        try {
            amountInput = Double.parseDouble(amountStr);
            if (amountInput <= 0) { // Allow 0, but fee and converted amount will be 0.
                errorLabel.setText("Amount must be positive.");
                if (amountInput == 0) { // If amount is 0, result is 0, fee is 0
                     convertedAmountLabel.setText(String.format("0.00 %s", targetCurrency));
                     feeAmountLabel.setText("0.00 EUR");
                }
                return;
            }
        } catch (NumberFormatException ex) {
            errorLabel.setText("Invalid amount format. Please enter a number.");
            return;
        }

        try {
            // Perform conversion
            double convertedValue = converterEngine.convert(baseCurrency, targetCurrency, amountInput, exchangeRates);
            convertedAmountLabel.setText(String.format("%.2f %s", convertedValue, targetCurrency));

            // Calculate fee
            // The fee is based on the *actual amount in Euro's*.
            double amountInEur;
            if (baseCurrency.equals("EUR")) {
                amountInEur = amountInput;
            } else {
                // Convert input amount to EUR for fee calculation
                // Use BigDecimal for precision to avoid floating point issues before fee calc
                BigDecimal originalAmountBd = BigDecimal.valueOf(amountInput);
                BigDecimal rateFromBd = BigDecimal.valueOf(exchangeRates.get(baseCurrency));
                if (rateFromBd.compareTo(BigDecimal.ZERO) == 0) {
                    errorLabel.setText("Error: Exchange rate for " + baseCurrency + " is zero.");
                    return;
                }
                // amountInEur = amountInput / exchangeRates.get(baseCurrency)
                BigDecimal amountInEurBd = originalAmountBd.divide(rateFromBd, 4, RoundingMode.HALF_UP); // 4 decimal places for intermediate EUR calc
                amountInEur = amountInEurBd.doubleValue();
            }

            double fee = converterEngine.calculateFee(amountInEur);
            feeAmountLabel.setText(String.format("%.2f EUR", fee));

        } catch (IllegalArgumentException ex) {
            errorLabel.setText("Error: " + ex.getMessage());
        } catch (Exception ex) {
            errorLabel.setText("An unexpected error occurred during conversion.");
            ex.printStackTrace(); // Log for debugging
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CurrencyConverterApp();
            }
        });
    }
}
