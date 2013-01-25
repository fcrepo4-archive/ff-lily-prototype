###Fedora tries its luck with Lily.


####Configuration:

You must have a running Lily repository to run this. Lily is not embedded!

Lily should be available at Zookeeper address "localhost:2181", or you can change the Spring config in WEB-INF/rest.xml.

####Adding resources:

The only methods currently implemented are half-assed POST/GET for /objects/{dsid} and quarter-assed GET for /fedora/describe.

To add a new JAXRS resource, create it as a subclass of AbstractResource, then add it to the Spring config in WEB-INF/rest.xml, like:

	<jaxrs:serviceBeans>
            <bean id="fedoraRepository" class="org.fcrepo.lily.FedoraRepository"/>
            ...
	</jaxrs:serviceBeans>

In your JAXRS resource, you can use getRepo() to acquire the Lily Repository object. See Lily documentation for what you can do with that.

####Schema:

To change the schema in use, modify src/main/resources/org/fcrepo/lily/fedoraSchema.json.

This schema file is in Lily format, as documented here:

http://docs.ngdata.com/lily-docs-trunk/g2/435-lily.html

It is not currently configured to load any sample records, but could be altered to do so.
