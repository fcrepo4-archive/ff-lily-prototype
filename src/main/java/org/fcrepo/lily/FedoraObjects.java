package org.fcrepo.lily;

import java.net.URI;
import java.util.ArrayList;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.lilyproject.repository.api.Link;
import org.lilyproject.repository.api.Record;
import org.lilyproject.repository.api.RecordId;
import org.lilyproject.repository.api.Repository;
import org.lilyproject.repository.api.RepositoryException;

@Path("/objects")
public class FedoraObjects extends AbstractResource {

	Repository repo = getRepo();

	@Path("/{pid}")
	@GET
	public Response getObject(@PathParam("pid") final String pid)
			throws RepositoryException, InterruptedException {
		RecordId pidRecId = userRecordIdFactory.fromString(pid, idGenerator);
		Record rec = repo.read(pidRecId, label);
		return Response.ok((String) rec.getField(label)).build();
	}

	@Path("/{pid}")
	@POST
	public Response addObject(@PathParam("pid") final String pid,
			@QueryParam("label") @DefaultValue("test") final String objLabel)
			throws RepositoryException, InterruptedException {

		repo.recordBuilder().recordType(fedoraObjectRecordTypeName).id(pid)
				.field(datastreams, new ArrayList<Link>())
				.field(label, objLabel).createOrUpdate();
		return Response.created(URI.create(pid)).build();
	}
}
