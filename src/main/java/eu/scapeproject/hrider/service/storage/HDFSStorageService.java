package eu.scapeproject.hrider.service.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.xml.bind.JAXBException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.scapeproject.model.IntellectualEntity;
import eu.scapeproject.model.mets.SCAPEMarshaller;

public class HDFSStorageService implements StorageService {

	private final FileSystem hdfs;
	private final SCAPEMarshaller marshaller;

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

	public IntellectualEntity getEntity(String id) throws IOException {
		return marshaller.deserialize(IntellectualEntity.class, getEntityInputStream(id));
	}

	public void saveOrUpdate(IntellectualEntity entity) throws IOException {
		Path p = new Path(entity.getIdentifier().getValue());
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
		} finally {
			IOUtils.closeStream(xmlOut);
		}
	}

	public InputStream getEntityInputStream(String id) throws IOException {
		return hdfs.open(new Path(id));
	}

	public OutputStream getEntityOutputStream(String id) throws IOException {
		return hdfs.create(new Path(id), true);
	}

	public boolean exists(String id) throws IOException {
		return hdfs.exists(new Path(id));
	}

	public long getSize(String id) throws IOException {
		return hdfs.getFileStatus(new Path(id)).getLen();
	}

}
