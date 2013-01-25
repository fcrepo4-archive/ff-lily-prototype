package org.fcrepo.lily;

import javax.annotation.PostConstruct;

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

public abstract class AbstractResource {

	static final String fedoraNamespace = "fedora";
	static final QName fedoraRecordTypeName = new QName(fedoraNamespace,
			"fedora");
	static final QName label = new QName(fedoraNamespace, "label");

	static public Repository repo = null;
	static public LilyClient client = null;

	final static private Logger logger = LoggerFactory
			.getLogger(AbstractResource.class);

	AbstractResource() {
	}

	AbstractResource(LilyClient cl) {
		client = cl;
	}

	@PostConstruct
	void initFedoraRecordType() throws RepositoryException,
			InterruptedException {
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
					.add().createOrUpdate();
			tm.createOrUpdateRecordType(fedoraRecordType);
			logger.debug("Created Fedora RecordType");
		}
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

	public static void setClient(LilyClient cl) {
		client = cl;
	}

}
