package counter;

import org.openstreetmap.osmosis.core.Osmosis;
import org.openstreetmap.osmosis.core.OsmosisRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
  private Log log = new Log();

  private ArrayList<Region> regions;
  private Config config;

  public static void main(String[] args) {
    new Main().run();
  }

  private Main() {
    regions = new ArrayList<>();
    config = new Config();
  }

  private void run() {
    try {
      parseRegions();
      log.i("Regions file parsed");
      if (getIDs()) {
        log.i("Relations IDs succesfully obtained");
        regions.forEach((Region r) -> getProvince(r));
        regions.forEach((Region r) -> System.out.println(r.getStats()));
      }
    } catch (IOException | ParserConfigurationException | SAXException e) {
      e.printStackTrace();
    }
  }

  private void parseRegions() throws IOException {
    File f = new File("regions.txt");
    if (!f.exists()) {
      throw new IllegalStateException("Regions file does not exist");
    }
    BufferedReader br = new BufferedReader(new FileReader(f));
    String line;
    while ((line = br.readLine()) != null) {
      if (line.startsWith("#")) {
        // Ignore, it is a comment
      } else if (line.startsWith("$")) {
        // Config
        line = line.substring(1);
        String[] parts = line.split("=");
        config.applyConfig(parts[0].trim(), parts[1].trim());
      } else {
        String[] parts = line.split(";");
        regions.add(new Region(parts[0].trim(), Integer.parseInt(parts[1].trim()), Integer.parseInt(parts[2].trim()), Float.parseFloat(parts[3].trim())));
      }
    }
  }

  private boolean getIDs() throws ParserConfigurationException, SAXException, IOException {
    boolean ret = true;
    for (Region r : regions) {
      String queryString = new Query(r, config, true).toString();
      Document doc = apiQuery(queryString);
      if (doc.getElementsByTagName("relation").getLength() == 0) {
        queryString = new Query(r, config, false).toString();
        doc = apiQuery(queryString);
      }
      try {
        r.setRelationID(Long.parseLong(((Element) doc.getElementsByTagName("relation").item(0)).getAttribute("id")));
      } catch (NullPointerException e) {
        System.out.println("Error with: " + r.getName());
        ret = false;
        continue;
      }
      if (r.getRelationID() == 0) {
        throw new IllegalStateException("Region " + r.getName() + " is invalid");
      }
    }
    return ret;
  }


  private void getProvince(Region region) {
    File polygonFile = null;
    File provinceFile = null;
    try {
      polygonFile = new File("polygon.txt");
      if (!polygonFile.exists()) {
        polygonFile.createNewFile();
      }

      Process p = Runtime.getRuntime().exec("perl getbound.pl " + region.getRelationID());
      BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
      BufferedWriter bw = new BufferedWriter(new FileWriter(polygonFile));
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

      region.setNodes(c);
      System.out.println("Progress: " + (regions.indexOf(region) + 1) + "/" + regions.size());

    } catch (IOException e) {
      e.printStackTrace();
    } catch (OsmosisRuntimeException e) {
      log.e(e);
      log.e("You need perl, XML::Simple library and List::MoreUtils");
    } finally {
      if (polygonFile != null && polygonFile.exists()) {
        polygonFile.delete();
      }
      if (provinceFile != null && provinceFile.exists()) {
        provinceFile.delete();
      }
    }
  }

  private Document apiQuery(String query) throws IOException, ParserConfigurationException, SAXException {
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
}
