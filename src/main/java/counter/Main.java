package counter;

import org.openstreetmap.osmosis.core.Osmosis;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;

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

      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(connection.getInputStream());

      String id = ((Element) doc.getElementsByTagName("relation").item(0)).getAttribute("id");
      Process p = Runtime.getRuntime().exec("perl getbound.pl " + id);
      p.waitFor();

      Osmosis.run(new String[]{"--read-pbf", "spain-latest.osm.pbf", "--bounding-polygon", "polygon.poly", "--write-xml", "test.osm"});

    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
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
          //f.delete();
        }
      }
    }
  }


  private static String generateOsmosisPolygon(List<float[]> nodes) {
    String ret = "";
    ret += "polygon\n";
    ret += "1\n";
    Random r = new Random();
    for (float[] node : nodes) {
      if (r.nextFloat() > 0f) {
        ret += node[0] + " " + node[1] + "\n";
      }
    }
    ret += "END\n";
    ret += "END";

    return ret;
  }

}
