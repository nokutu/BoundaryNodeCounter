package counter;

/**
 * Created by nokutu on 27/01/16.
 */
public class Query {

  private String query;

  public Query(String province) {
    StringBuilder sb = new StringBuilder();
    sb.append("<query type=\"relation\">\n");
    sb.append("  <has-kv k=\"type\" v=\"boundary\"/>\n");
    sb.append("  <has-kv k=\"name\" v=\"" + province + "\"/>\n");
    sb.append("</query>\n");
    sb.append("<recurse type=\"relation-way\" role=\"outer\"/>\n");
    sb.append("<recurse type=\"way-node\"/>\n");
    sb.append("<print mode=\"skeleton\"/>");
    query = sb.toString();
  }

  @Override
  public String toString() {
    return query;
  }
}
