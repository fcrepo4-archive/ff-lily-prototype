package org.fcrepo.lily;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.apache.cxf.helpers.IOUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.lilyproject.repository.api.Blob;
import org.lilyproject.repository.api.Link;
import org.lilyproject.repository.api.QName;
import org.lilyproject.repository.api.Record;
import org.lilyproject.repository.api.RecordId;
import org.lilyproject.repository.api.RepositoryException;

import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.io.CountingInputStream;

@Path("/objects/{pid}/datastreams")
public class FedoraDatastreams extends AbstractResource {

	static final QName datastreamId = new QName(fedoraNamespace, "datastreamId");
	static final QName datastreamContent = new QName(fedoraNamespace, "content");

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
	public Response addDatastream(
			@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid,
			@HeaderParam("Content-Type") @DefaultValue("application/octet-stream") final String mediaType,
			final InputStream content) throws RepositoryException,
			InterruptedException, IOException {

		// create datastream record
		Record dsRec = repo.recordBuilder()
				.recordType(fedoraDatastreamRecordTypeName)
				.id(pid + "/" + dsid).field(datastreamId, dsid)
				.createOrUpdate();

		// add link to object
		RecordId pidRecId = userRecordIdFactory.fromString(pid, idGenerator);
		Record objRec = repo.read(pidRecId, datastreams);
		List<Link> dses = (List<Link>) objRec.getField(datastreams);
		dses.add(new Link(dsRec.getId()));
		objRec.setField(datastreams, dses);
		repo.createOrUpdate(objRec);

		// craptacular initial binary store implementation
		// which buffers the entire payload, dammit
		CountingInputStream bufferedContent = new CountingInputStream(
				new BufferedInputStream(content));
		bufferedContent.mark(Integer.MAX_VALUE);
		while (bufferedContent.read() != -1)
			; // how lame _am_ I?!
		Blob contentBlob = new Blob(mediaType, bufferedContent.getCount(), dsid);
		bufferedContent.reset();
		OutputStream out = repo.getOutputStream(contentBlob);
		IOUtils.copy(bufferedContent, out);
		bufferedContent.close();
		out.close();
		dsRec.setField(datastreamContent, contentBlob);
		repo.createOrUpdate(dsRec);

		return Response.created(
				URI.create("objects/" + pid + "/datastreams/" + dsid)).build();
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

	@Path("/{dsid}/content")
	@GET
	public Response getDatastreamContent(@PathParam("pid") final String pid,
			@PathParam("dsid") final String dsid) throws RepositoryException,
			InterruptedException, JsonGenerationException,
			JsonMappingException, IOException {

		RecordId dsRecId = userRecordIdFactory.fromString(pid + "/" + dsid,
				idGenerator);

		return Response.ok(repo.getInputStream(dsRecId, datastreamContent))
				.build();
	}
}
