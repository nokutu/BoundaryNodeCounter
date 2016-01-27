package counter;

/**
 * Created by nokutu on 27/01/16.
 */
public class Query {

  private String query;

  public Query(String province) {
    StringBuilder sb = new StringBuilder();
    sb.append("<union>");
    sb.append("<query type=\"relation\">");
    sb.append("  <has-kv k=\"type\" v=\"boundary\"/>");
    sb.append("  <has-kv k=\"name\" v=\"" + province + "\"/>");
    sb.append("</query>");
    sb.append("<recurse type=\"relation-way\" role=\"outer\"/>");
    sb.append("<recurse type=\"way-node\"/>");
    sb.append("</union>");
    sb.append("<print mode=\"skeleton\"/>");
    query = sb.toString();
  }

  @Override
  public String toString() {
    return query;
  }
}
