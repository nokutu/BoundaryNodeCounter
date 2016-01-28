package counter;

/**
 * Created by nokutu on 27/01/16.
 */
public class Query {

  private String query;

  public Query(Region r, Config c, boolean useLanguageCode) {
    StringBuilder sb = new StringBuilder();
    sb.append("<query type=\"relation\">");
    sb.append("  <has-kv k=\"type\" v=\"boundary\"/>");
    String language = c.getLanguage();
    if (language == null || !useLanguageCode) {
      sb.append("  <has-kv k=\"name\" v=\"").append(r.getName()).append("\"/>");
    } else {
      sb.append("  <has-kv k=\"name:").append(language).append("\" v=\"").append(r.getName()).append("\"/>");
    }
    sb.append("  <has-kv k=\"admin_level\" v=\"").append(r.getAdmin_level()).append("\"/>");

    sb.append("</query>");
    sb.append("<print mode=\"skeleton\"/>");
    query = sb.toString();
  }

  @Override
  public String toString() {
    return query;
  }
}
