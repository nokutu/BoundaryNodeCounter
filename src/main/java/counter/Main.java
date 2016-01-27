package counter;

import org.openstreetmap.osmosis.core.Osmosis;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
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


  private static void getProvince(String province) {
    File polygonFile = null;
    File provinceFile = null;
    try {
      String queryString = new Query(province).toString();
      Document doc = apiQuery(queryString);

      polygonFile = new File("polygon.txt");
      if (!polygonFile.exists()) {
        polygonFile.createNewFile();
      }
      BufferedWriter bw = new BufferedWriter(new FileWriter(polygonFile));

      String id = ((Element) doc.getElementsByTagName("relation").item(0)).getAttribute("id");
      Process p = Runtime.getRuntime().exec("perl getbound.pl " + id);
      BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line;
      while ((line = br.readLine()) != null) {
        bw.write(line + "\n");
      }
      br.close();
      bw.close();

      Osmosis.run(new String[]{"-q", "--read-pbf", "spain-latest.osm.pbf", "--bounding-polygon", "polygon.poly", "--write-xml", "province.osm"});

      provinceFile = new File("province.osm");
      long c = 0;
      br = new BufferedReader(new FileReader(provinceFile));
      while ((line = br.readLine()) != null) {
        if (line.contains("node")) {
          c++;
        }
      }
      br.close();

      System.out.println(province + ": " + c);

    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } finally {
      if (polygonFile != null && polygonFile.exists()) {
        polygonFile.delete();
      }
      if (provinceFile != null && provinceFile.exists()) {
        provinceFile.delete();
      }
    }
  }

  private static Document apiQuery(String query) throws IOException, ParserConfigurationException, SAXException {
    URL osm = new URL(HOSTNAME);
    HttpURLConnection connection = (HttpURLConnection) osm.openConnection();
    connection.setDoInput(true);
    connection.setDoOutput(true);
    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

    DataOutputStream printout = new DataOutputStream(connection.getOutputStream());
    printout.writeBytes("data=" + URLEncoder.encode(query, "utf-8"));
    printout.flush();
    printout.close();

    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    return dBuilder.parse(connection.getInputStream());
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
