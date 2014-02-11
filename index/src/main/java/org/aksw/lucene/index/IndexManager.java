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


package org.aksw.lucene.index;

import org.aksw.lucene.bean.Place;
import org.aksw.lucene.field.IndexField;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class IndexManager {

    private File indexDirectory;

    private Analyzer analyzer;

    public IndexManager(File indexDirectory, Analyzer analyzer) {
        this.indexDirectory = indexDirectory;
        this.analyzer = analyzer;
    }

    /**
     *
     * @param config
     * @param places
     * @throws IOException
     */
    private void processIndex(IndexWriterConfig config, List<Place> places) throws IOException {

        IndexWriter writer = new IndexWriter(FSDirectory.open(indexDirectory), config);

        for (Place place : places) {
            addDoc(writer, place.getUrl(), place.getTypes(), place.getName(), place.getLongitude(), place.getLatitude(), place.getCity());
        }

        writer.close();

    }

    /**
     * Create an index using a list of places
     *
     * @param places
     */
    public void createIndex(List<Place> places) throws IOException {

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        processIndex(config, places);

    }

    /**
     * Add a new document to the index
     * @param writer
     * @param url
     * @param types
     * @param name
     * @param longitude
     * @param latitude
     * @param city
     * @throws IOException
     */
    private static void addDoc(IndexWriter writer, String url, String types, String name, String longitude, String latitude, String city) throws IOException {
        Document doc = new Document();
        doc.add(new StringField(IndexField.URL, url, Field.Store.YES));
        doc.add(new TextField(IndexField.TYPES, types, Field.Store.YES));
        doc.add(new TextField(IndexField.DESCRIPTION, name, Field.Store.YES));
        doc.add(new TextField(IndexField.CITY, city, Field.Store.YES));
        doc.add(new StringField(IndexField.LONGITUDE, longitude, Field.Store.YES));
        doc.add(new StringField(IndexField.LATITUDE, latitude, Field.Store.YES));
        writer.addDocument(doc);
    }

    /**
     * Update an index using a list of places
     *
     * @param places
     */
    public void appendIndex(List<Place> places) throws IOException {

        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
        processIndex(config, places);

    }


}