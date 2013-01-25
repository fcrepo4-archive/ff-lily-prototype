package org.fcrepo.lily;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.lilyproject.client.LilyClient;
import org.lilyproject.repository.api.QName;
import org.lilyproject.repository.api.Repository;
import org.lilyproject.tools.import_.cli.JsonImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractResource {

	static final String fedoraNamespace = "fedora";
	static final QName fedoraObjectRecordTypeName = new QName(fedoraNamespace,
			"object");
	static final QName label = new QName(fedoraNamespace, "label");

	static public Repository repo = null;

	@Resource
	public LilyClient lilyClient = null;

	@Resource(name = "fedoraSchema")
	public org.springframework.core.io.Resource schemaResource;

	final static private Logger logger = LoggerFactory
			.getLogger(AbstractResource.class);

	AbstractResource() {
	}

	AbstractResource(LilyClient cl) {
		lilyClient = cl;
	}

	@PostConstruct
	void loadSchema() throws IOException, Exception {
		logger.debug("Loading Fedora schema");
		JsonImport.load(getRepo(), schemaResource.getInputStream(), true);
	}

	public Repository getRepo() {
		if (repo != null)
			return repo;
		else {
			repo = lilyClient.getRepository();
			return repo;
		}
	}

	public LilyClient getClient() {
		return lilyClient;
	}

	public void setClient(LilyClient cl) {
		lilyClient = cl;
	}

	public org.springframework.core.io.Resource getSchemaResource() {
		return schemaResource;
	}

	public void setSchemaResource(
			org.springframework.core.io.Resource schemaResource) {
		this.schemaResource = schemaResource;
	}

}
