package eu.scapeproject.hrider.service.storage;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import eu.scapeproject.model.IntellectualEntity;
import eu.scapeproject.model.util.TestUtil;

public class HDFSStorageTest {

	private static HDFSStorageService storage;

	@BeforeClass
	public static void setup() throws IOException {
		Configuration miniConf = new Configuration();
		MiniDFSCluster cluster = new MiniDFSCluster(miniConf, 1, true, null);
		cluster.waitClusterUp();
		storage = new HDFSStorageService(cluster.getFileSystem());
	}

	@Test
	public void testSaveEntity() throws Exception {
		IntellectualEntity minimal = TestUtil.createMinimalEntity();
		storage.saveOrUpdate(minimal);
		String id = minimal.getIdentifier().getValue();
		assertTrue(storage.getSize(id) > 0);
	}
	
	@Test
	public void testGetEntity() throws Exception{
		IntellectualEntity minimal = TestUtil.createMinimalEntity();
		storage.saveOrUpdate(minimal);
		IntellectualEntity fetched = storage.getEntity(minimal.getIdentifier().getValue());
		assertTrue(minimal.getIdentifier().equals(fetched.getIdentifier()));
		assertTrue(minimal.getDescriptive().equals(fetched.getDescriptive()));
		assertTrue(minimal.getRepresentations().size() == fetched.getRepresentations().size());
	}
	
}
