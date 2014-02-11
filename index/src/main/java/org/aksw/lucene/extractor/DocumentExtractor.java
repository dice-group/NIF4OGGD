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

package org.aksw.lucene.extractor;

import com.aliasi.chunk.Chunk;
import com.aliasi.dict.DictionaryEntry;
import com.aliasi.dict.ExactDictionaryChunker;
import com.aliasi.dict.MapDictionary;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.hp.hpl.jena.rdf.model.*;
import org.aksw.lucene.bean.Place;
import org.aksw.lucene.field.IndexField;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DocumentExtractor {

    private static final String NIF_ISSTRING ="http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#isString";
    private static final String NIF_ANCHOR = "http://persistence.uni-leipzig.org/nlp2rdf/ontologies/nif-core#anchorOf";
    private static File indexDirectory;
    private static Analyzer analyzer;
    private static ExactDictionaryChunker chunker = null;
    private static Log LOG = LogFactory.getLog(DocumentExtractor.class);
    private static StandardAnalyzer luceneAnalyzer = new StandardAnalyzer(Version.LUCENE_43);
    private static String city = "";
    private static String nifFilename = "";
    private static IndexWriter writer = null;

    public static void main(String args[]) {

        if (args.length < 3) {
            System.err.println("\nUsage: IndexProcessor <NIF(turtle)> <City> <Index Path>\n");
            System.exit(1);
        }

        nifFilename = args[0]; //Eg.: /home/spotlight/storage/nif/boris/output.ttl"
        city = args[1]; //Eg.: Berlin
        String indexPath = args[2]; // Eg.: /home/spotlight/storage/nif/nifoggd

        try {

            DocumentExtractor documentExtractor = new DocumentExtractor(new File(indexPath), luceneAnalyzer);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
            writer = new IndexWriter(FSDirectory.open(new File(indexPath)), config);
            setDictionary(documentExtractor.getPlaces(city));
            readNIF();
            writer.close();

        } catch (IOException e) {
            LOG.error("Parameters:");
            LOG.error("NIF(turtle): %s".format(nifFilename));
            LOG.error("City: %s".format(city));
            LOG.error("Index Path: %s".format(indexPath));
            e.printStackTrace();

        }

    }

    /**
     *
     * @throws FileNotFoundException
     */
    private static void readNIF() throws IOException {

        Model model = ModelFactory.createDefaultModel();
        InputStream is = new FileInputStream(nifFilename);
        model.read(is, null, "TURTLE");

        com.hp.hpl.jena.rdf.model.StmtIterator it =  model.listStatements();


        String title="";

        while (it.hasNext()) {

            Statement st =  it.nextStatement();
            Property property = st.getPredicate();
            RDFNode obj = st.getObject();

            if (property.toString().equals(NIF_ANCHOR))
            {
                title = obj.toString();
            }else if (property.toString().equals(NIF_ISSTRING)) {
                extractText(formatString(title.concat("\n\n").concat(obj.toString())));
                title="";
            }
        }

    }

    /**
     *
     * @param data
     * @return
     */
    private static String formatString(String data)
    {
        return data.replaceAll("\n","###").replaceAll("@de", "");
    }

    /**
     *
     * @param text
     */
    private static void extractText(String text) throws IOException {

        LOG.debug("Using ahocorasick to locate streets name in the document...");

        Iterator result = chunker.chunk(text.toLowerCase()).chunkSet().iterator();

        while (result.hasNext()) {
            Chunk c = (Chunk) result.next();
            updateDocument(text.toLowerCase().substring(c.start(), c.end()),text);
        }

    }

    /**
     *
     * @param street
     * @param document
     * @throws IOException
     */
    private static void updateDocument(String street, String document) throws IOException {

        IndexReader reader = IndexReader.open(FSDirectory.open(indexDirectory));
        IndexSearcher searcher = new IndexSearcher(reader);

        BooleanQuery bq = new BooleanQuery();
        bq.add(new TermQuery(new Term(IndexField.CITY, city.toLowerCase())), BooleanClause.Occur.MUST);
        bq.add(new TermQuery(new Term(IndexField.DESCRIPTION, street.toLowerCase())), BooleanClause.Occur.MUST);

        LOG.debug("Filtering using the following parameters...");
        LOG.debug("Street:%s".format(street));
        LOG.debug("City:%s".format(city));


        ScoreDoc[] hits = searcher.search(bq, Integer.MAX_VALUE).scoreDocs;

        if (hits.length != 0) {


            Document doc = searcher.doc(hits[0].doc);

            boolean hasDocument = false;

            for(IndexableField f: doc.getFields(IndexField.DOCUMENT)){
                hasDocument = f.stringValue().contains(document);
                if (hasDocument) break;

            }

            if (!hasDocument){
                FieldType fieldType = new FieldType();
                fieldType.setStoreTermVectors(true);
                fieldType.setStoreTermVectorPositions(true);
                fieldType.setIndexed(true);
                fieldType.setIndexOptions(FieldInfo.IndexOptions.DOCS_AND_FREQS);
                fieldType.setStored(true);
                doc.add(new Field(IndexField.DOCUMENT,document,fieldType));

                writer.updateDocument(new Term(IndexField.DESCRIPTION, street.toLowerCase()),doc);
                writer.commit();

                LOG.debug("commit done!");
            }
        }

    }

    /**
     *
     * @param places
     * @throws IOException
     */
    private static void setDictionary(List<Place> places) throws IOException {

        LOG.debug("Creating a dictionary...");

        MapDictionary dictionary = new MapDictionary<String>();
        for (Place p : places)
            dictionary.addEntry(new DictionaryEntry<String>(p.getName().toLowerCase().trim(), ""));
        chunker = new ExactDictionaryChunker(dictionary, IndoEuropeanTokenizerFactory.INSTANCE, false, false);

        LOG.debug("done!");

    }

    /**
     *
     * @param indexDirectory
     * @param analyzer
     */
    public DocumentExtractor(File indexDirectory, Analyzer analyzer) {
        this.indexDirectory = indexDirectory;
        this.analyzer = analyzer;
    }

    /**
     *  Filtering all streets by city
     * @param cityFilter
     * @return
     * @throws IOException
     */
    private List<Place> getPlaces(String cityFilter) throws IOException {

        List<Place> result = new ArrayList<Place>();

        LOG.debug("Reading streets by city...");
        LOG.debug("City:%s".format(city));

        IndexReader reader = IndexReader.open(FSDirectory.open(indexDirectory));
        IndexSearcher searcher = new IndexSearcher(reader);

        BooleanQuery bq = new BooleanQuery();
        bq.add(new TermQuery(new Term(IndexField.CITY, cityFilter.toLowerCase())), BooleanClause.Occur.MUST);

        ScoreDoc[] hits = searcher.search(bq, Integer.MAX_VALUE).scoreDocs;

        for (int i = 0; i < hits.length; i++) {

            Document doc = searcher.doc(hits[i].doc);

            String street = doc.get(IndexField.DESCRIPTION).toLowerCase();
            String city = doc.get(IndexField.CITY).toLowerCase();
            Place p = new Place();
            p.setName(street);
            p.setCity(city);
            result.add(p);

        }

        reader.close();

        return result;

    }
}

