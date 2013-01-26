package org.fcrepo.lily;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.lilyproject.repository.api.Link;
import org.lilyproject.repository.api.Record;
import org.lilyproject.repository.api.RecordId;
import org.lilyproject.repository.api.RepositoryException;

import com.google.common.collect.ImmutableSet;

@Path("/objects/{pid}/datastreams")
public class FedoraDatastreams extends AbstractResource {

	@Path("/")
	@GET
	public Response getDatastreams(@PathParam("pid") final String pid)
			throws RepositoryException, InterruptedException {
		RecordId pidRecId = userRecordIdFactory.fromString(pid, idGenerator);
		Record rec = repo.read(pidRecId, label, datastreams);
		List dses = (List) rec.getField(datastreams);
		ImmutableSet.Builder dsset = new ImmutableSet.Builder();
		for (final Object ds : dses) {
			dsset.add(ds);
		}
		return Response.ok(dsset.build().toString()).build();
	}

	@Path("/{dsid}")
	@POST
	public Response addDatastream(@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid, InputStream content)
			throws RepositoryException, InterruptedException {
		// create datastream record
		Record dsRec = repo.recordBuilder()
				.recordType(fedoraDatastreamRecordTypeName)
				.id(pid + "/" + dsid).field(label, dsid)
				.field(datastreamId, dsid).createOrUpdate();
		// add link to object
		RecordId pidRecId = userRecordIdFactory.fromString(pid, idGenerator);
		Record objRec = repo.read(pidRecId, label, datastreams);
		List<Link> dses = (List<Link>) objRec.getField(datastreams);
		dses.add(new Link(dsRec.getId()));
		objRec.setField(datastreams, dses);
		repo.createOrUpdate(objRec);
		return Response.created(URI.create(dsid)).build();
	}

}
