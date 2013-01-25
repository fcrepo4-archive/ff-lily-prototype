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
	static public LilyClient client = null;

	AbstractResource() {
	}

	AbstractResource(LilyClient cl) {
		this.client = cl;
	}

	public static Repository getRepo() {
		if (repo != null)
			return repo;
		else {
			repo = client.getRepository();
			return repo;
		}
	}

	public static LilyClient getClient() {
		return client;
	}

	public static void setClient(LilyClient client) {
		AbstractResource.client = client;
	}

}
