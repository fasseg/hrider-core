package eu.scapeproject.hrider.service.indexer;

import java.io.IOException;

import eu.scapeproject.model.IntellectualEntity;

public interface IndexerService {
	public void addOrUpdateEntity(IntellectualEntity e) throws IOException;
	public void removeEntity(IntellectualEntity e) throws IOException;
	public int getNumIndexed() throws IOException;
	public void shutdown() throws IOException;
	void clearIndex() throws IOException;
}
