package org.fcrepo.lily;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.lilyproject.client.LilyClient;
import org.lilyproject.repository.api.QName;
import org.lilyproject.repository.api.RecordType;
import org.lilyproject.repository.api.RecordTypeNotFoundException;
import org.lilyproject.repository.api.Repository;
import org.lilyproject.repository.api.RepositoryException;
import org.lilyproject.repository.api.TypeManager;
import org.lilyproject.repository.impl.valuetype.StringValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.lilyproject.tools.import_.cli.JsonImport;

public abstract class AbstractResource {

	static final String fedoraNamespace = "fedora";
	static final QName fedoraRecordTypeName = new QName(fedoraNamespace,
			"fedora");
	static final QName label = new QName(fedoraNamespace, "label");

	static public Repository repo = null;
	
	@Resource
	public LilyClient lilyClient = null;
	
	static public org.springframework.core.io.Resource schemaResource;

	final static private Logger logger = LoggerFactory
			.getLogger(AbstractResource.class);

	AbstractResource() {
	}

	AbstractResource(LilyClient cl) {
		lilyClient = cl;
	}

	@PostConstruct
	void initFedoraRecordType() throws IOException, Exception {
		//JsonImport.load(getRepo(), schemaResource.getInputStream(), true);
		logger.debug("Trying to retrieve Fedora RecordType");
		TypeManager tm = getRepo().getTypeManager();
		try {
			tm.getRecordTypeByName(fedoraRecordTypeName, 1L);
			logger.debug("Retrieved Fedora RecordType");
		} catch (RecordTypeNotFoundException e) {
			logger.debug("Creating Fedora RecordType");
			RecordType fedoraRecordType = tm.recordTypeBuilder()
					.name(fedoraRecordTypeName).fieldEntry().defineField()
					.name(label).type(new StringValueType()).createOrUpdate()
					.add().build();
			tm.createOrUpdateRecordType(fedoraRecordType);
			logger.debug("Created Fedora RecordType");
		}
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

}
