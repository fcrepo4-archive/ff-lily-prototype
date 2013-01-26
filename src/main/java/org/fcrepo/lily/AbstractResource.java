package org.fcrepo.lily;

import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.lilyproject.client.LilyClient;
import org.lilyproject.repository.api.QName;
import org.lilyproject.repository.api.Repository;
import org.lilyproject.repository.impl.id.IdGeneratorImpl;
import org.lilyproject.repository.impl.id.UserRecordIdFactory;
import org.lilyproject.tools.import_.cli.JsonImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractResource {

	static final String fedoraNamespace = "fedora";
	static final QName fedoraObjectRecordTypeName = new QName(fedoraNamespace,
			"object");
	static final QName fedoraDatastreamRecordTypeName = new QName(
			fedoraNamespace, "datastream");
	static final QName label = new QName(fedoraNamespace, "label");
	static final QName datastreams = new QName(fedoraNamespace, "datastreams");
	static final QName datastreamId = new QName(fedoraNamespace, "datastreamId");

	static public Repository repo;
	static public IdGeneratorImpl idGenerator;
	UserRecordIdFactory userRecordIdFactory;

	@Resource
	public LilyClient lilyClient = null;

	@Resource(name = "fedoraSchema")
	public org.springframework.core.io.Resource schemaResource;

	static protected final ObjectMapper mapper = new ObjectMapper();

	final static private Logger logger = LoggerFactory
			.getLogger(AbstractResource.class);

	AbstractResource() {
	}

	AbstractResource(LilyClient cl) {
		lilyClient = cl;
	}

	@PostConstruct
	void initialize() throws IOException, Exception {
		logger.debug("Loading Fedora schema");
		JsonImport.load(getRepo(), schemaResource.getInputStream(), true);
		
		repo = getRepo();
		
		idGenerator = (IdGeneratorImpl) repo.getIdGenerator();
		userRecordIdFactory = new UserRecordIdFactory();
		
		mapper.configure(SerializationConfig.Feature.AUTO_DETECT_IS_GETTERS,
				false);

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
