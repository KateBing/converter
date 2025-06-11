package com.currencyconverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyConverterApp {

    private JFrame frame;
    private JComboBox<String> baseCurrencyComboBox;
    private JTextField amountTextField;
    private JComboBox<String> targetCurrencyComboBox;
    private JButton convertButton;
    private JLabel convertedAmountLabel;
    private JLabel feeAmountLabel;
    private JLabel errorLabel;

    private EcbApiHandler ecbApiHandler;
    private CurrencyConverterEngine converterEngine;
    private Map<String, Double> exchangeRates;

    // Define a common font
    private Font commonFont;
    private Font boldResultFont;
    private Font errorFont;

    public CurrencyConverterApp() {
        // Initialize fonts - "Segoe UI" is a modern Windows font, "SansSerif" is a logical default
        try {
            commonFont = new Font("Segoe UI", Font.PLAIN, 13); // Increased size slightly
            boldResultFont = new Font("Segoe UI", Font.BOLD, 13);
            errorFont = new Font("Segoe UI", Font.ITALIC, 12);
            // Test if Segoe UI is available by checking its family name, otherwise fallback
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            boolean segoeFound = false;
            for (String fontName : ge.getAvailableFontFamilyNames()) {
                if ("Segoe UI".equals(fontName)) {
                    segoeFound = true;
                    break;
                }
            }
            if (!segoeFound) {
                commonFont = new Font("SansSerif", Font.PLAIN, 13);
                boldResultFont = new Font("SansSerif", Font.BOLD, 13);
                errorFont = new Font("SansSerif", Font.ITALIC, 12);
            }
        } catch (Exception e) { // Fallback in case of any font loading issues
            commonFont = new Font("SansSerif", Font.PLAIN, 13);
            boldResultFont = new Font("SansSerif", Font.BOLD, 13);
            errorFont = new Font("SansSerif", Font.ITALIC, 12);
            System.err.println("Font loading error, using SansSerif: " + e.getMessage());
        }


        ecbApiHandler = new EcbApiHandler();
        converterEngine = new CurrencyConverterEngine();
        loadExchangeRates();
        initializeUI();
        populateCurrencies();
    }

    private void loadExchangeRates() {
        exchangeRates = ecbApiHandler.getExchangeRates();
        if (exchangeRates == null || exchangeRates.isEmpty()) {
            System.err.println("Critical: Failed to load exchange rates on startup.");
        }
    }

    private void initializeUI() {
        frame = new JFrame("Currency Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Preferred size for input components for some uniformity
        Dimension inputPreferredSize = new Dimension(150, 28); // width, height

        // Base Currency Row
        JLabel baseCurrencyTitleLabel = new JLabel("From Currency:");
        baseCurrencyTitleLabel.setFont(commonFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(baseCurrencyTitleLabel, gbc);

        baseCurrencyComboBox = new JComboBox<>();
        baseCurrencyComboBox.setFont(commonFont);
        baseCurrencyComboBox.setPreferredSize(inputPreferredSize);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        frame.add(baseCurrencyComboBox, gbc);


        // Amount Row
        JLabel amountTitleLabel = new JLabel("Amount:");
        amountTitleLabel.setFont(commonFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(amountTitleLabel, gbc);

        amountTextField = new JTextField(12); // Adjusted columns slightly
        amountTextField.setFont(commonFont);
        amountTextField.setPreferredSize(inputPreferredSize);
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(amountTextField, gbc);


        // Target Currency Row
        JLabel targetCurrencyTitleLabel = new JLabel("To Currency:");
        targetCurrencyTitleLabel.setFont(commonFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(targetCurrencyTitleLabel, gbc);

        targetCurrencyComboBox = new JComboBox<>();
        targetCurrencyComboBox.setFont(commonFont);
        targetCurrencyComboBox.setPreferredSize(inputPreferredSize);
        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(targetCurrencyComboBox, gbc);


        // Convert Button Row
        convertButton = new JButton("Convert");
        convertButton.setFont(commonFont);
        // convertButton.setPreferredSize(new Dimension(100, 30)); // Let L&F decide for button for now
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 8, 15, 8); // More vertical padding for button row
        frame.add(convertButton, gbc);
        gbc.insets = new Insets(8, 8, 8, 8); // Reset insets
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;


        // Converted Amount Row
        JLabel convertedAmountTitleLabel = new JLabel("Converted Amount:");
        convertedAmountTitleLabel.setFont(commonFont);
        gbc.gridx = 0;
        gbc.gridy = 4;
        frame.add(convertedAmountTitleLabel, gbc);

        convertedAmountLabel = new JLabel("0.00");
        convertedAmountLabel.setFont(boldResultFont);
        gbc.gridx = 1;
        gbc.gridy = 4;
        frame.add(convertedAmountLabel, gbc);


        // Fee Amount Row
        JLabel feeAmountTitleLabel = new JLabel("Conversion Fee:");
        feeAmountTitleLabel.setFont(commonFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        frame.add(feeAmountTitleLabel, gbc);

        feeAmountLabel = new JLabel("0.00 EUR");
        feeAmountLabel.setFont(boldResultFont);
        gbc.gridx = 1;
        gbc.gridy = 5;
        frame.add(feeAmountLabel, gbc);


        // Error Label Row
        errorLabel = new JLabel(" ");
        errorLabel.setFont(errorFont);
        errorLabel.setForeground(Color.RED);
        // errorLabel.setMinimumSize(new Dimension(0, commonFont.getSize() + 5)); // Ensure height for one line of text
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.weighty = 0.1; // Give a little vertical space for error label to sit in
        gbc.fill = GridBagConstraints.BOTH; // Allow it to take space
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(10, 8, 8, 8); // Top padding for error area
        frame.add(errorLabel, gbc);
        gbc.insets = new Insets(8, 8, 8, 8); // Reset
        gbc.gridwidth = 1;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;


        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performConversion();
            }
        });

        if (exchangeRates == null || exchangeRates.isEmpty()) {
            convertButton.setEnabled(false);
            baseCurrencyComboBox.setEnabled(false);
            targetCurrencyComboBox.setEnabled(false);
            amountTextField.setEnabled(false);
            errorLabel.setText("Error: Exchange rates not available. Please restart.");
        }

        frame.pack();
        frame.setMinimumSize(frame.getPreferredSize());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void populateCurrencies() {
        // ... (no changes to this method)
        if (exchangeRates == null || exchangeRates.isEmpty()) {
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
        // ... (no changes to this method logic)
        errorLabel.setText(" ");
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
             return;
        }

        double amountInput;
        try {
            amountInput = Double.parseDouble(amountStr);
            if (amountInput < 0) {
                errorLabel.setText("Amount cannot be negative.");
                return;
            }
            if (amountInput == 0) {
                 convertedAmountLabel.setText(String.format("0.00 %s", targetCurrency));
                 feeAmountLabel.setText("0.00 EUR");
                 return;
            }
        } catch (NumberFormatException ex) {
            errorLabel.setText("Invalid amount format. Please enter a number.");
            return;
        }

        try {
            double convertedValue = converterEngine.convert(baseCurrency, targetCurrency, amountInput, exchangeRates);
            convertedAmountLabel.setText(String.format("%.2f %s", convertedValue, targetCurrency));

            double amountInEur;
            if (baseCurrency.equals("EUR")) {
                amountInEur = amountInput;
            } else {
                BigDecimal originalAmountBd = BigDecimal.valueOf(amountInput);
                BigDecimal rateFromBd = BigDecimal.valueOf(exchangeRates.get(baseCurrency));
                if (rateFromBd.compareTo(BigDecimal.ZERO) == 0) {
                    errorLabel.setText("Error: Exchange rate for " + baseCurrency + " is zero.");
                    return;
                }
                BigDecimal amountInEurBd = originalAmountBd.divide(rateFromBd, 4, RoundingMode.HALF_UP);
                amountInEur = amountInEurBd.doubleValue();
            }

            double fee = converterEngine.calculateFee(amountInEur);
            feeAmountLabel.setText(String.format("%.2f EUR", fee));

        } catch (IllegalArgumentException ex) {
            errorLabel.setText("Error: " + ex.getMessage());
        } catch (Exception ex) {
            errorLabel.setText("An unexpected error occurred during conversion.");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // ... (Nimbus L&F setup remains the same)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Nimbus L&F not found, using default. Error: " + e.getMessage());
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CurrencyConverterApp();
            }
        });
    }
}
