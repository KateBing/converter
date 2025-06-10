# Currency Converter Application

This application allows users to select a base currency from a list of currencies provided by the European Central Bank (ECB), enter an amount, select a target currency, and see the converted amount. It also calculates and displays a conversion fee based on the Euro equivalent of the input amount. The exchange rates are fetched live from the ECB's daily XML feed.

## Project Structure

- `src/main/java/com/currencyconverter/`: Contains the Java source code.
  - `CurrencyConverterApp.java`: The main application class with the Swing GUI.
  - `EcbApiHandler.java`: Handles fetching and parsing exchange rates from the ECB.
  - `CurrencyConverterEngine.java`: Performs the currency conversion and fee calculation.
- `README.md`: This file.

## Prerequisites

- Java Development Kit (JDK) 8 or higher installed.
- Access to the internet (for fetching live exchange rates).
- A graphical environment to display the Swing GUI.

## Compilation

It is recommended to compile the source files using UTF-8 encoding, especially if your system's default encoding might cause issues with special characters (though this project primarily uses standard characters).

1.  **Open a Command Prompt or Terminal.**

2.  **Navigate to the project's root directory.**
    If your project is located at `C:\Users\kate_\Documents\GitHub\converter`, you would use:
    ```sh
    cd C:\Users\kate_\Documents\GitHub\converter
    ```

3.  **Create a directory for compiled classes (optional but good practice).**
    Let's call it `out`:
    ```sh
    mkdir out
    ```

4.  **Compile the Java files.**
    You will need to provide the full path to your JDK's `javac` executable if it's not in your system's PATH. Replace `C:\path\to\your\jdk\bin\javac.exe` with the actual path on your system.
    If the path to `javac.exe` contains spaces, ensure it is enclosed in quotes. For PowerShell users, if the path is quoted, you must precede it with the call operator `&`.

    Commands:

    *   For Command Prompt (`cmd.exe`) or other shells like `bash` (use quotes if path has spaces):
        ```sh
        "C:\path\to\your\jdk\bin\javac.exe" -encoding UTF-8 -d out src\main\java\com\currencyconverter\*.java
        ```
    *   For PowerShell (use `&` if path is quoted and has spaces):
        ```sh
        & "C:\path\to\your\jdk\bin\javac.exe" -encoding UTF-8 -d out src\main\java\com\currencyconverter\*.java
        ```
    If `javac` is in your PATH (no need for full path, quotes, or `&` unless the `javac` command itself is an alias with spaces):
    ```sh
    javac -encoding UTF-8 -d out src\main\java\com\currencyconverter\*.java
    ```

    *Breakdown of the command:*
    - `"C:\path\to\your\jdk\bin\javac.exe"` or `& "C:\path\to\your\jdk\bin\javac.exe"`: Full path to the Java compiler. **Replace this with your actual path if not using PATH.**
    - `-encoding UTF-8`: Specifies the character encoding for the source files.
    - `-d out`: Specifies the output directory (`out`) for the compiled `.class` files.
    - `src\main\java\com\currencyconverter\*.java`: Specifies all Java source files within the package directory. The path is relative to the project root (`C:\Users\kate_\Documents\GitHub\converter`).

## Execution

After successful compilation, you can run the application.

1.  **Ensure you are still in the project's root directory** in your Command Prompt or Terminal (e.g., `C:\Users\kate_\Documents\GitHub\converter`).

2.  **Run the application.**
    You will need to provide the full path to your JDK's `java` executable if it's not in your system's PATH. Replace `C:\path\to\your\jdk\bin\java.exe` with the actual path.
    If the path to `java.exe` contains spaces, ensure it is enclosed in quotes. For PowerShell users, if the path is quoted, you must precede it with the call operator `&`.

    The command needs to specify the classpath (where to find the compiled classes) and the main class to execute.

    Commands:

    *   For Command Prompt (`cmd.exe`) or other shells like `bash` (use quotes if path has spaces):
        ```sh
        "C:\path\to\your\jdk\bin\java.exe" -cp out com.currencyconverter.CurrencyConverterApp
        ```
    *   For PowerShell (use `&` if path is quoted and has spaces):
        ```sh
        & "C:\path\to\your\jdk\bin\java.exe" -cp out com.currencyconverter.CurrencyConverterApp
        ```
    If `java` is in your PATH (no need for full path, quotes, or `&` unless the `java` command itself is an alias with spaces):
    ```sh
    java -cp out com.currencyconverter.CurrencyConverterApp
    ```

    *Breakdown of the command:*
    - `"C:\path\to\your\jdk\bin\java.exe"` or `& "C:\path\to\your\jdk\bin\java.exe"`: Full path to the Java runtime. **Replace this with your actual path if not using PATH.**
    - `-cp out`: Sets the classpath to the `out` directory where your compiled classes are. `cp` is short for `classpath`.
    - `com.currencyconverter.CurrencyConverterApp`: The fully qualified name of the main class to run.

3.  The Currency Converter application window should appear.

## Notes

-   **JDK Path:** If you don't want to type the full path to `javac` and `java` every time, you can add your JDK's `bin` directory (e.g., `C:\Program Files\Java\jdk-11\bin` or similar) to your system's `PATH` environment variable.
-   **GUI Requirement:** This is a Swing-based GUI application. It needs a desktop environment to run. It will not run in a headless environment (e.g., a server without a display).
-   **Internet Connection:** The application fetches live exchange rates from the European Central Bank. An active internet connection is required for the rates to load and for conversions to be accurate. If rates cannot be fetched, the application will show an error and functionality will be limited.
