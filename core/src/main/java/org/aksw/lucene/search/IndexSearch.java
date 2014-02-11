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

package org.aksw.lucene.search;


import org.aksw.lucene.Search;
import org.aksw.lucene.bean.Place;
import org.aksw.lucene.field.IndexField;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndexSearch implements Search {

    private StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_43);

    @Override
    public List<Place> findByDescription(Integer hitsPerPage, String indexPath, String queryString) throws IOException, ParseException {

        List<Place> result = new ArrayList<Place>();

        Query query = null;

        query = new QueryParser(Version.LUCENE_43, IndexField.DESCRIPTION, analyzer).parse(queryString);

        File indexDir = new File(indexPath);


        IndexReader reader = IndexReader.open(FSDirectory.open(indexDir));

        IndexSearcher searcher = new IndexSearcher(reader);

        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);

        searcher.search(query, collector);

        ScoreDoc[] hits = collector.topDocs().scoreDocs;


        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document doc = searcher.doc(docId);

            List<String> docs = new ArrayList<String>();


            Place place = new Place();
            place.setName(doc.get(IndexField.DESCRIPTION));
            place.setLatitude(doc.get(IndexField.LATITUDE));
            place.setLongitude(doc.get(IndexField.LONGITUDE));
            place.setUrl(doc.get(IndexField.URL));
            place.setTypes(doc.get(IndexField.TYPES));
            place.setCity(doc.get(IndexField.CITY));

            result.add(place);

        }
        reader.close();

        return result;
    }

    @Override
    public List<Place> getDocsByDescription(Integer hitsPerPage, String indexPath, String queryString) throws IOException, ParseException {

        String street = queryString.split(",")[0].trim();
        String city   = queryString.split(",")[1].trim();
        List<Place> result = new ArrayList<Place>();
        File indexDir = new File(indexPath);
        IndexReader reader = IndexReader.open(FSDirectory.open(indexDir));
        IndexSearcher searcher = new IndexSearcher(reader);

        BooleanQuery bq = new BooleanQuery();
        bq.add(new TermQuery(new Term(IndexField.CITY, city.toLowerCase())), BooleanClause.Occur.MUST);
        bq.add(new TermQuery(new Term(IndexField.DESCRIPTION, street.toLowerCase())), BooleanClause.Occur.MUST);

        ScoreDoc[] hits = searcher.search(bq, Integer.MAX_VALUE).scoreDocs;

        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document doc = searcher.doc(docId);

            List<String> docs = new ArrayList<String>();


            Place place = new Place();
            place.setName(doc.get(IndexField.DESCRIPTION));
            place.setLatitude(doc.get(IndexField.LATITUDE));
            place.setLongitude(doc.get(IndexField.LONGITUDE));
            place.setUrl(doc.get(IndexField.URL));
            place.setTypes(doc.get(IndexField.TYPES));
            place.setCity(doc.get(IndexField.CITY));

            for(IndexableField f: doc.getFields(IndexField.DOCUMENT))
                docs.add(f.stringValue());
            place.setDocuments(docs);

            result.add(place);

        }
        reader.close();



        return result;

    }
}
