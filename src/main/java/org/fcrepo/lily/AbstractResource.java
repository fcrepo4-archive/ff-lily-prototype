package org.fcrepo.lily;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.lilyproject.client.LilyClient;
import org.lilyproject.client.NoServersException;
import org.lilyproject.repository.api.Repository;
import org.lilyproject.repository.api.RepositoryException;
import org.lilyproject.util.zookeeper.ZkConnectException;

public abstract class AbstractResource {

	static public Repository repo = null;

	AbstractResource() throws IOException, InterruptedException, KeeperException, ZkConnectException, NoServersException, RepositoryException {
		if (repo == null) {
			final LilyClient client = new LilyClient("localhost:2181", 1000);
			repo = client.getRepository();
		}
	}
}
