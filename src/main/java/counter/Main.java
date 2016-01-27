package counter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nokutu on 27/01/16.
 */
public class Main {

  private final static String HOSTNAME = "http://www.overpass-api.de/api/interpreter";

  public static void main(String[] args) {
    getProvince("Principado de Asturias");
  }

  private static void readPBF(File file) {

  }

  private static void getProvince(String province) {
    List<float[]> nodes = null;
    FileWriter fw = null;
    File f = null;
    try {
      nodes = getNodes(province);
      f = new File("polygon.txt");
      if (!f.exists()) {
        f.createNewFile();
      }
      fw = new FileWriter(f);
      fw.write(generateOsmosisPolygon(nodes));
      fw.close();

      Process p = Runtime.getRuntime().exec("./osmosis-latest/bin/osmosis --read-pbf spain-latest.osm.pbt --bounding-polygon polygon.txt --write-xml " + province +".xml");
      BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line = null;
      p.waitFor();
      while((line = br.readLine()) != null) {
        System.out.println(line);
      }

    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      if (fw != null) {
        try {
          fw.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        if (f != null && f.exists()) {
          f.delete();
        }
      }
    }
  }


  private static List<float[]> getNodes(String province) throws IOException {
    List<float[]> ret = new ArrayList<float[]>();

    String queryString = new Query(province).toString();
    URL osm = new URL(HOSTNAME);
    HttpURLConnection connection = (HttpURLConnection) osm.openConnection();
    connection.setDoInput(true);
    connection.setDoOutput(true);
    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

    DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
    printout.writeBytes("data=" + URLEncoder.encode(queryString, "utf-8"));
    printout.flush();
    printout.close();

    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    String line;
    while ((line = br.readLine()) != null) {
      if (line.contains("node")) {
        String[] parts = line.split("\"");
        ret.add(new float[]{Float.parseFloat(parts[3]), Float.parseFloat(parts[5])});
      }
    }
    br.close();
    return ret;
  }

  private static String generateOsmosisPolygon(List<float[]> nodes) {
    String ret = "";
    ret += "polygon\n";
    ret += "1\n";
    for (float[] node : nodes) {
      ret += node[0] + " " + node[1] + "\n";
    }
    ret += "END\n";
    ret += "END";

    return ret;
  }

}
