package counter;

/**
 * Created by nokutu on 28/01/16.
 */
public class Region {

  private String name;
  private int population;
  private float surface;
  private int admin_level;

  private long relationID;
  private long nodes;

  public Region(String name, int admin_level, int population, float surface) {
    this.name = name;
    this.admin_level = admin_level;
    this.population = population;
    this.surface = surface;
  }

  public void setRelationID(long id) {
    relationID = id;
  }

  public void setNodes(long nodes) {
    this.nodes = nodes;
  }

  public String getName() {
    return name;
  }

  public long getRelationID() {
    return relationID;
  }

  public int getAdmin_level() {
    return admin_level;
  }

  public String getStats() {
    StringBuilder sb = new StringBuilder();
    sb.append(name).append(": ").append(nodes).append(" nodes; ").append(nodes / (float) population).append(" nodes/person; ").append(nodes / surface).append(" nodes/km2");
    return sb.toString();
  }
}
