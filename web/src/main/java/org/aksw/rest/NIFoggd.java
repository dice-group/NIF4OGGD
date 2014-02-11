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


package org.aksw.rest;

import com.google.gson.Gson;
import org.aksw.lucene.Search;
import org.aksw.lucene.bean.Place;
import org.aksw.lucene.search.IndexSearch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.queryparser.classic.ParseException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@ApplicationPath("http://localhost:8080/rest")
@Path("/nifoggd")
@Consumes("text/plain")
public class NIFoggd {

    private static String index;

    private static Log LOG = LogFactory.getLog(NIFoggd.class);


    private void init() {

        if (index == null) {

            try {

                Context context = (Context) new InitialContext().lookup("java:comp/env");
                index = (String) context.lookup("LUCENE_INDEX");

            } catch (NamingException e) {

                index = "/tmp/lucene";

                LOG.error("Please, check your web.xml file. I can not retrieve the Lucene index path from the env variable ");
                LOG.error("E.g:");
                LOG.error("    <env-entry>\n" +
                        "        <env-entry-name>LUCENE_INDEX</env-entry-name>\n" +
                        "        <env-entry-value>/tmp/lucene/</env-entry-value>\n" +
                        "        <env-entry-type>java.lang.String</env-entry-type>\n" +
                        "    </env-entry>");

                index = "/tmp/lucene/";

            }
        }


    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJSON(@QueryParam("text") String text,
                            @QueryParam("maxRows") Integer maxRows,
                            @QueryParam("docs") String docs) {

        init();

        Gson gson = new Gson();

        String response = "";

        Search search = new IndexSearch();

        try {

            List<Place> list = docs==null?search.findByDescription(maxRows, index,text.concat("*")):search.getDocsByDescription(maxRows, index,text) ;

            response = gson.toJson(list);

        } catch (ParseException parser) {

            LOG.error("It was not possible process the query using %s as parameter.".format(text));

        } catch (IOException io) {

            LOG.error("It was not possible process to retrieve the lucene index. Please check the env variable in web.xml and try again ");
        }


        return Response.ok().entity(response).header("Access-Control-Allow-Origin", "*").build();

    }


}
