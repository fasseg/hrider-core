package eu.scapeproject.hrider.rest;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.view.Viewable;

import eu.scapeproject.hrider.service.storage.StorageService;

@Path("/admin")
@Component
public class HRiderAdminResource {
	@Autowired
	@Qualifier("storageService")
	private StorageService storage;
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response adminOverview() {
		return Response.ok(new Viewable("/admin-overview")).build();
	}

	@GET
	@Path("/stats")
	@Produces(MediaType.APPLICATION_JSON)
	public HRiderStats statsAsJSON() throws IOException {
		return new HRiderStats(storage.getNumObjects(), storage.getNumIndexed(), storage.getSize());
	}

	@POST
	@Path("/clear-index")
	public Response clearIndex() throws IOException {
		storage.clearIndex();
		return Response.ok().build();
	}

	@POST
	@Path("/clear-data")
	public Response clearData() throws IOException {
		storage.clearData();
		return Response.ok().build();
	}
}
