package common.util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

/**
 * The SimpleLogger class provides a simple logging utility that supports customized logging to the console.
 * It ensures that loggers are reused for each class, and it allows for color-coded log levels in the console output.
 */
public class SimpleLogger {
    private static SimpleLogger instance;  // Singleton instance of SimpleLogger
    private final Map<Class<?>, Logger> loggers = new HashMap<>();  // Map to store loggers by class

    /**
     * Returns the singleton instance of the SimpleLogger.
     *
     * @return The SimpleLogger instance.
     */
    public static SimpleLogger getInstance() {
        if (instance == null) {
            instance = new SimpleLogger();  // Create the instance if it doesn't exist
        }
        return instance;
    }

    /**
     * Returns a Logger instance for the specified class. If the logger for the class does not exist,
     * it creates and configures a new logger.
     *
     * @param clazz The class for which to retrieve the logger.
     * @return The Logger instance for the specified class.
     */
    public Logger getLogger(Class<?> clazz) {
        // Reuse the logger if it already exists
        Logger logger = loggers.get(clazz);
        if (logger == null) {
            logger = Logger.getLogger(clazz.getSimpleName());

            // Remove existing handlers from the logger
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }

            // Remove handlers from the root logger
            Logger rootLogger = Logger.getLogger("");
            for (Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }

            // Set up a new handler for console output
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);  // Set level to ALL to capture all log levels
            consoleHandler.setFormatter(getFormatter());  // Set custom formatter

            // Add the handler and disable parent handlers
            logger.addHandler(consoleHandler);
            logger.setUseParentHandlers(false);

            // Store the logger for reuse
            loggers.put(clazz, logger);
        }

        return logger;
    }

    /**
     * Creates and returns a custom formatter for log messages, which includes timestamp, log level,
     * logger name, and the log message itself. The formatter also supports color coding based on the log level.
     *
     * @return A Formatter instance used for log message formatting.
     */
    private Formatter getFormatter() {
        return new Formatter() {
            @Override
            public String format(LogRecord record) {
                // Get current timestamp formatted as yyyy-MM-dd HH:mm:ss
                String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String level = record.getLevel().toString();  // Get log level
                String name = record.getLoggerName();  // Get logger name
                String message = record.getMessage();  // Get log message
                if (record.getParameters() != null) {
                    message = MessageFormat.format(message, record.getParameters());  // Format the message with parameters
                }

                String color = getColorForLevel(level);  // Get color for log level

                // Format the log message with the timestamp, level, name, message, and color
                return String.format("%s[%s] [%s] [%s] - %s%s\n", color, timestamp, level, name, message, "\u001B[0m");
            }

            /**
             * Returns the ANSI color code for the log level.
             *
             * @param level The log level (e.g., SEVERE, INFO, WARNING, etc.).
             * @return The ANSI color code corresponding to the log level.
             */
            private String getColorForLevel(String level) {
                return switch (level) {
                    case "SEVERE" -> "\u001B[31m"; // Red for SEVERE
                    case "WARNING" -> "\u001B[33m"; // Yellow for WARNING
                    case "INFO" -> "\u001B[32m"; // Green for INFO
                    case "FINE", "FINER", "FINEST" -> "\u001B[34m"; // Blue for FINE levels
                    default -> "\u001B[37m"; // Default color (white) for other levels
                };
            }
        };
    }
}

