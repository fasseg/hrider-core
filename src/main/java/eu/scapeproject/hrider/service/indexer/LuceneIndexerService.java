package eu.scapeproject.hrider.service.indexer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;

import javax.annotation.PreDestroy;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexNotFoundException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scapeproject.model.IntellectualEntity;
import eu.scapeproject.model.metadata.dc.DCMetadata;

public class LuceneIndexerService implements IndexerService {

	public static final String LUCENE_FIELD_TITLE = "title";
	public static final String LUCENE_FIELD_ID = "id";

	private static final Logger LOG = LoggerFactory.getLogger(LuceneIndexerService.class);

	private StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
	private IndexWriterConfig luceneConfig = new IndexWriterConfig(Version.LUCENE_36, analyzer);
	private IndexWriter writer;

	private Directory indexStore;

	public LuceneIndexerService(String directory) throws IOException {
		File dir = new File(directory);
		if (dir.exists() && (dir.isFile() || !dir.canWrite())) {
			throw new IOException("Unable to access directory at " + directory);
		} else if (!dir.exists()) {
			dir.mkdir();
		}
		indexStore = FSDirectory.open(dir);
		writer = new IndexWriter(indexStore, luceneConfig);
	}

	@Override
	public void addOrUpdateEntity(IntellectualEntity e) throws IOException {
		LOG.debug("indexing entity " + e.getIdentifier().getValue());
		Document doc = new Document();
		DCMetadata dc = (DCMetadata) e.getDescriptive();
		doc.add(new Field(LUCENE_FIELD_ID, e.getIdentifier().getValue(),Field.Store.YES,Field.Index.NOT_ANALYZED));
		doc.add(new Field(LUCENE_FIELD_TITLE, dc.getTitle().get(0),Field.Store.YES,Field.Index.ANALYZED));
		writer.updateDocument(new Term(LUCENE_FIELD_ID, e.getIdentifier().getValue()), doc);
		writer.commit();
	}
	
	@Override
	public void clearIndex() throws IOException {
		writer.deleteAll();
		writer.commit();
	}

	@Override
	public void removeEntity(IntellectualEntity e) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getNumIndexed() throws IOException {
		IndexReader reader;
		try {
			reader = IndexReader.open(indexStore);
		} catch (IndexNotFoundException e) {
			// no index exists yet
			return 0;
		}
		int num = reader.numDocs();
		reader.close();
		return num;
	}

	@PreDestroy
	@Override
	public void shutdown() throws IOException {
		LOG.debug("closing index writer...");
		writer.close();
		indexStore.close();
	}
}
