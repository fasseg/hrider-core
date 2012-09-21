package eu.scapeproject.hrider.service.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.annotation.PreDestroy;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.tools.DistCp;
import org.apache.lucene.util.fst.FST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import eu.scapeproject.hrider.service.indexer.IndexerService;
import eu.scapeproject.model.File;
import eu.scapeproject.model.IntellectualEntity;
import eu.scapeproject.model.Representation;
import eu.scapeproject.model.mets.SCAPEMarshaller;

public class HDFSStorageService implements StorageService {

	@Autowired
	@Qualifier("indexerService")
	private IndexerService indexer;

	private final FileSystem hdfs;
	private final SCAPEMarshaller marshaller;

	private static final String DATA_PATH = "hrider-data";
	private static final String OBJECT_PATH = "hrider-objects";

	private static final Logger LOG = LoggerFactory.getLogger(HDFSStorageService.class);

	public HDFSStorageService(FileSystem hdfs) throws IOException {
		this.hdfs = hdfs;
		try {
			this.marshaller = SCAPEMarshaller.getInstance();
		} catch (JAXBException e) {
			LOG.error("Unable to create HDFS storage service", e);
			throw new IOException(e);
		}
	}

	public HDFSStorageService(String hdfsUri) throws IOException {
		LOG.debug("connecting to " + hdfsUri);
		Configuration hdfsConf = new Configuration();
		this.hdfs = FileSystem.get(URI.create(hdfsUri), hdfsConf);
		try {
			this.marshaller = SCAPEMarshaller.getInstance();
		} catch (JAXBException e) {
			LOG.error("Unable to create HDFS storage service", e);
			throw new IOException(e);
		}
	}

	@Override
	public String getEntityXML(String id) throws IOException {
		return IOUtils.toString(getEntityInputStream(id));
	}

	@Override
	public IntellectualEntity getEntity(String id) throws IOException {
		return marshaller.deserialize(IntellectualEntity.class, getEntityInputStream(id));
	}

	@Override
	public void saveOrUpdate(IntellectualEntity entity) throws IOException {
		LOG.debug("storing entity " + entity.getIdentifier().getValue());
		Path p = new Path(OBJECT_PATH + "/" + entity.getIdentifier().getValue());
		OutputStream xmlOut = null;
		try {
			if (hdfs.exists(p)) {
				// update
				xmlOut = hdfs.create(p, true);
				marshaller.serialize(entity, xmlOut);
			} else {
				// create
				xmlOut = hdfs.create(p);
				marshaller.serialize(entity, xmlOut);
			}
			this.updateDataStreams(entity);
			indexer.addOrUpdateEntity(entity);
		} finally {
			IOUtils.closeQuietly(xmlOut);
		}
	}

	private void updateDataStreams(IntellectualEntity entity) throws IOException {
		for (Representation r : entity.getRepresentations()) {
			for (File f : r.getFiles()) {
				LOG.debug("fetching file from " + f.getUri());
				OutputStream sink = null;
				InputStream src = null;
				try {
					sink = hdfs.create(new Path(DATA_PATH + "/" + f.getIdentifier().getValue()));
					src = f.getUri().toURL().openStream();
					IOUtils.copy(src, sink);
				} catch (IOException e) {
					LOG.error("unable to ingest ", f.getIdentifier().getValue(), e);
				} finally {
					IOUtils.closeQuietly(sink);
					IOUtils.closeQuietly(src);
				}
			}
		}
	}

	@Override
	public InputStream getEntityInputStream(String id) throws IOException {
		return hdfs.open(new Path(OBJECT_PATH + "/" + id));
	}

	@Override
	public OutputStream getEntityOutputStream(String id) throws IOException {
		return hdfs.create(new Path(OBJECT_PATH + "/" + id), true);
	}

	@Override
	public boolean exists(String id) throws IOException {
		return hdfs.exists(new Path(OBJECT_PATH + "/" + id));
	}

	@Override
	public long getSize(String id) throws IOException {
		return hdfs.getFileStatus(new Path(OBJECT_PATH + "/" + id)).getLen();
	}

	@Override
	public int getNumIndexed() throws IOException {
		return indexer.getNumIndexed();
	}

	@Override
	public int getNumObjects() throws IOException {
		FileStatus[] fsStatus = hdfs.listStatus(new Path(OBJECT_PATH));
		return fsStatus.length;
	}

	@Override
	public long getSize() throws IOException {
		FileStatus[] fsStatus = hdfs.listStatus(new Path(OBJECT_PATH));
		long size = 0l;
		for (FileStatus st : fsStatus) {
			size += st.getLen();
		}
		return size;
	}

	@PreDestroy
	@Override
	public void shutdown() throws IOException {
		LOG.debug("shutting down storage...");
		hdfs.close();
	}

	@Override
	public void clearData() throws IOException {
		hdfs.delete(new Path(OBJECT_PATH), true);
		hdfs.delete(new Path(DATA_PATH), true);
	}

	@Override
	public void clearIndex() throws IOException {
		indexer.clearIndex();
	}

}
