package eu.scapeproject.hrider.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.apache.hadoop.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import eu.scapeproject.hrider.service.storage.StorageService;
import eu.scapeproject.model.Identifier;
import eu.scapeproject.model.IntellectualEntity;
import eu.scapeproject.model.mets.SCAPEMarshaller;

@Path("entity")
public class IntellectualEntityResource {
	@Autowired
	@Qualifier("storageService")
	private StorageService storage;
	private SCAPEMarshaller marshaller;

	public IntellectualEntityResource() throws IOException {
		try {
			marshaller = SCAPEMarshaller.getInstance();
		} catch (JAXBException e) {
			throw new IOException(e);
		}
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public IntellectualEntity getEntityXML(@PathParam("id") String id) throws IOException {
		return storage.getEntity(id);
	}

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	public String createEntity(InputStream in) throws IOException {
		try {
			IntellectualEntity entity = marshaller.deserialize(IntellectualEntity.class, in);
			if (entity.getIdentifier() == null || entity.getIdentifier().getValue() == null) {
				entity = new IntellectualEntity.Builder(entity).identifier(new Identifier("IE-" + UUID.randomUUID())).build();
			}
			storage.saveOrUpdate(entity);
			return entity.getIdentifier().getValue();
		} finally {
			IOUtils.closeStream(in);
		}
	}
}
