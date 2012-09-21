package eu.scapeproject.hrider.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import eu.scapeproject.hrider.service.indexer.SearchResult;

@Path("/entity-list")
public class IntellectualEntityListResource {


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<SearchResult> listJSON() {
		return null;
	}
}
