package counter;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nokutu on 28/01/16.
 */
public class Log {

  private static final Level LEVEL = Level.FINE;
  private final Logger log;

  public Log() {
    log = Logger.getLogger("Main");
    log.setUseParentHandlers(false);
    log.setLevel(LEVEL);
    ConsoleHandler ch = new ConsoleHandler();
    ch.setLevel(LEVEL);
    log.addHandler(ch);
  }

  /**
   * Logs an error.
   *
   * @param message The message to be displayed in the log
   */
  public void e(Object message) {
    log.severe(message.toString());
  }

  /**
   * Logs a warning.
   *
   * @param message The message to be displayed in the log
   */
  public void w(Object message) {
    log.warning(message.toString());
  }

  /**
   * Logs a debug message.
   *
   * @param message The message to be displayed in the log
   */
  public void d(Object message) {
    log.fine(message.toString());
  }

  /**
   * Logs an information message.
   *
   * @param message The message to be displayed in the log
   */
  public void i(Object message) {
    log.info(message.toString());
  }

}