package counter;

/**
 * Created by nokutu on 28/01/16.
 */
public class Config {

  private String language = "en";

  public void applyConfig(String name, String value) {
    switch (name) {
      case "language":
        language = value;
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  public String getLanguage() {
    return language;
  }
}
