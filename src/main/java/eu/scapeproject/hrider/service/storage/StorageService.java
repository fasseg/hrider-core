package eu.scapeproject.hrider.service.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import eu.scapeproject.model.IntellectualEntity;

public interface StorageService {
	
	public void saveOrUpdate(IntellectualEntity entity) throws IOException;
	public IntellectualEntity getEntity(String id) throws IOException;
	public String getEntityXML(String id) throws IOException;
	public long getSize(String id) throws IOException;
	public boolean exists(String id) throws IOException;
	
	
	// stream handling methods
	public InputStream getEntityInputStream(String id) throws IOException;
	public OutputStream getEntityOutputStream(String id) throws IOException;
	
	// statistics methods
	public int getNumObjects() throws IOException;
	public int getNumIndexed() throws IOException;
	public long getSize() throws IOException;
	public void shutdown() throws IOException;
	
	// admin methods
	public void clearIndex() throws IOException;
	public void clearData() throws IOException;

}
