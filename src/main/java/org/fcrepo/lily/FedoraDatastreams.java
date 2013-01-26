package org.fcrepo.lily;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.SerializationConfig;
import org.lilyproject.repository.api.Link;
import org.lilyproject.repository.api.QName;
import org.lilyproject.repository.api.Record;
import org.lilyproject.repository.api.RecordId;
import org.lilyproject.repository.api.RepositoryException;

import com.google.common.collect.ImmutableSet.Builder;

@Path("/objects/{pid}/datastreams")
public class FedoraDatastreams extends AbstractResource {

	@Path("/")
	@GET
	public Response getDatastreams(@PathParam("pid") final String pid)
			throws RepositoryException, InterruptedException,
			JsonGenerationException, JsonMappingException, IOException {
		RecordId pidRecId = userRecordIdFactory.fromString(pid, idGenerator);
		Record objRec = repo.read(pidRecId, label, datastreams);
		List<Link> dses = (List<Link>) objRec.getField(datastreams);
		Builder<Map<QName, Object>> dsset = new Builder<Map<QName, Object>>();
		for (final Link ds : dses) {
			dsset.add(repo.read(ds.resolve(objRec, idGenerator)).getFields());
		}

		return Response.ok(
				mapper.writerWithType(Set.class).writeValueAsString(
						dsset.build())).build();
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
		Record objRec = repo.read(pidRecId, datastreams);
		List<Link> dses = (List<Link>) objRec.getField(datastreams);
		dses.add(new Link(dsRec.getId()));
		objRec.setField(datastreams, dses);
		repo.createOrUpdate(objRec);
		return Response.created(URI.create(dsid)).build();
	}

	@Path("/{dsid}")
	@GET
	public Response getDatastream(@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid) throws RepositoryException,
			InterruptedException, JsonGenerationException,
			JsonMappingException, IOException {

		return Response.ok(
				mapper.writerWithType(Map.class).writeValueAsString(
						repo.read(
								userRecordIdFactory.fromString(
										pid + "/" + dsid, idGenerator))
								.getFields())).build();
	}
}
