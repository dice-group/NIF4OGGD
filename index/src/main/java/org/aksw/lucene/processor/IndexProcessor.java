/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aksw.lucene.processor;

import com.google.gson.*;
import org.aksw.lucene.bean.Place;
import org.aksw.lucene.bean.parser.SparqlQueryResult;
import org.aksw.lucene.index.IndexManager;
import org.aksw.sparql.SparqlUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class IndexProcessor {

    private static Log LOG = LogFactory.getLog(IndexProcessor.class);
    private static StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);

    /**
     *
     * @param args
     */
    public static void main(String args[]) {

        if (args.length < 5) {
            System.err.println("\nUsage: IndexProcessor <Sparql endpoint> <Query file> <City> <Index Path> <Create or Append?> \n");
            System.exit(1);
        }

        String endPoint = args[0];
        String queryFile = args[1];
        String city = args[2];
        String indexPath = args[3];
        String action = args[4];

        try {

            String query = FileUtils.readFileToString(new File(queryFile)) ;
            IndexManager im = new IndexManager(new File(indexPath), analyzer);
            List<Place> places = sparqlResultToPlace(getResult(executeQuery(endPoint, query)), city);

            if ("create".equalsIgnoreCase(action)) {
                im.createIndex(places);
            } else {
                im.appendIndex(places);
            }

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

    /**
     * @param endPoint
     * @param query
     * @return
     */
    public static String executeQuery(String endPoint, String query) {

        SparqlUtils sparqlUtils = new SparqlUtils();
        String result = "";
        try {

            result = sparqlUtils.getContent(endPoint, sparqlUtils.getUrlParameters(query, "UTF-8", ""));

        } catch (IOException e) {

            LOG.error("Parameters:");
            LOG.error("Sparql endpoint: %s".format(endPoint));
            LOG.error("Query: %s".format(query));
            e.printStackTrace();

        }

        return result;
    }

    /**
     * @param json
     * @return
     */
    public static List<SparqlQueryResult> getResult(String json) {
        List<SparqlQueryResult> result = new ArrayList<SparqlQueryResult>();

        if (json == null || json.isEmpty()) return result;

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject validJSON = parser.parse(json).getAsJsonObject();
        JsonObject results = validJSON.get("results").getAsJsonObject();
        JsonArray bindings = results.getAsJsonArray("bindings");


        for (JsonElement obj : bindings) {
            SparqlQueryResult jsonElement = gson.fromJson(obj, SparqlQueryResult.class);
            result.add(jsonElement);
        }


        return result;
    }

    /**
     * @param lst
     * @param city
     * @return
     */
    public static List<Place> sparqlResultToPlace(List<SparqlQueryResult> lst, String city) {
        List<Place> places = new ArrayList<Place>();
        Map<String, Place> placeMap = new HashMap<String, Place>();


        for (SparqlQueryResult result : lst) {

            Place key = placeMap.get(result.getStreetLabel().getValue());

            if (key != null) {

                key.setLatitude(key.getLatitude().concat("#").concat(result.getLatitude().getValue()));
                key.setLongitude(key.getLongitude().concat("#").concat(result.getLongitude().getValue()));

            } else {

                Place place = new Place();
                place.setCity(city);
                place.setName(result.getStreetLabel().getValue());
                place.setLatitude(result.getLatitude().getValue());
                place.setLongitude(result.getLongitude().getValue());
                place.setUrl(result.getUrl().getValue());
                place.setTypes("DBpedia:Street");
                placeMap.put(result.getStreetLabel().getValue(), place);

            }
        }

        Iterator it = placeMap.keySet().iterator();

        while (it.hasNext()) {
            String key = (String) it.next();
            places.add(placeMap.get(key));
        }

        return places;
    }


}
