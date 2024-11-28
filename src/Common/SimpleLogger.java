package Common;

import java.util.logging.*;
import java.text.MessageFormat;

public class SimpleLogger {
    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());

        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }


        Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }


        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        consoleHandler.setFormatter(getFormatter());


        logger.addHandler(consoleHandler);
        logger.setUseParentHandlers(false);

        return logger;
    }

    private static Formatter getFormatter() {
        return new Formatter() {
            @Override
            public String format(LogRecord record) {
                String timestamp = java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String level = record.getLevel().toString();
                String className = record.getSourceClassName();
                String message = record.getMessage();
                if (record.getParameters() != null) {
                    message = MessageFormat.format(message, record.getParameters());
                }

                String color = getColorForLevel(level);

                return String.format("%s[%s] [%s] [%s] - %s%s\n", color, timestamp, level, className, message, "\u001B[0m");
            }

            // Método para obtener el color según el nivel
            private String getColorForLevel(String level) {
                return switch (level) {
                    case "SEVERE" -> "\u001B[31m"; // Rojo
                    case "WARNING" -> "\u001B[33m"; // Amarillo
                    case "INFO" -> "\u001B[32m"; // Verde
                    case "FINE", "FINER", "FINEST" -> "\u001B[34m"; // Azul
                    default -> "\u001B[37m"; // Blanco por defecto
                };
            }
        };
    }
}
