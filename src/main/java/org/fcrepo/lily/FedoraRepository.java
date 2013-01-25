package org.fcrepo.lily;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.zookeeper.KeeperException;
import org.lilyproject.client.NoServersException;
import org.lilyproject.repository.api.RepositoryException;
import org.lilyproject.util.zookeeper.ZkConnectException;

@Path("/fedora")
public class FedoraRepository extends AbstractResource {

	public FedoraRepository() throws IOException, InterruptedException,
			KeeperException, ZkConnectException, NoServersException,
			RepositoryException {
		super();
	}

	@Path("/describe")
	@GET
	public Response getDescription() {
		return Response.ok().entity(getRepo().hashCode()).build();
	}
}
