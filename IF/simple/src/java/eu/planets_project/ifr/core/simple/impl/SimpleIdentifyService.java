/**
 * 
 */
package eu.planets_project.ifr.core.simple.impl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.ifr.core.simple.impl.util.FileTypeResolver;
import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.identify.Identify;
import eu.planets_project.services.identify.IdentifyResult;
import eu.planets_project.services.migrate.Migrate;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@Local(Migrate.class)
@Remote(Migrate.class)
@Stateless

@WebService(name = SimpleIdentifyService.NAME, 
        serviceName = Identify.NAME, 
        targetNamespace = PlanetsServices.NS,
        endpointInterface = "eu.planets_project.services.identify.Identify" )
        
public class SimpleIdentifyService implements Identify {

    private static Log log = LogFactory.getLog(SimpleIdentifyService.class);

    /* (non-Javadoc)
     * @see eu.planets_project.services.identify.Identify#describe()
     */
    public ServiceDescription describe() {
        ServiceDescription mds = new ServiceDescription("A Simple DigitalObject Identification Service.", "");
        mds.setDescription("A simple identification service that can determine the mime-type of simple (single-file) digital objects. Only looks at the file extension, so can only work on by-reference objects.");
        mds.setAuthor("Andrew Jackson <Andrew.Jackson@bl.uk>");
        mds.setClassname(this.getClass().getCanonicalName());
        mds.setType(Identify.class.getCanonicalName());
        return mds;
    }

    /* (non-Javadoc)
     * @see eu.planets_project.services.identify.Identify#identify(eu.planets_project.services.datatypes.DigitalObject)
     */
    public IdentifyResult identify(DigitalObject dob) {
        // Initialise the result:
        
        // Use this resolver:
        FileTypeResolver ftr;
        try {
            ftr = FileTypeResolver.instantiate();
        } catch (Exception e) {
            return this.returnWithErrorMessage("Could not instanciate the file type resolver.");
        }
        // Can only cope if the object is 'simple':
        if( dob.getContent() == null ) {
            return this.returnWithErrorMessage("The Content of the DigitalObject should not be NULL.");
        }
        if( dob.getContent().size() != 1 ) {
            return this.returnWithErrorMessage("Cannot identify digital objects with anything other than 1 Content item.");
        }
        // If this is an embedded binary:
        if( dob.getContent().get(0).isByValue() ) {
            return this.returnWithErrorMessage("Cannot identify digital objects that are passed by value.");
        } else {
            // URL, can deal with this:
            String type = ftr.getMIMEType( dob.getContent().get(0).getReference() );
            ServiceReport rep = new ServiceReport();
            rep.setErrorState(0);
            URI typeURI = null;
            try {
                typeURI = new URI("planets:fmt/mime/"+type);
            } catch (URISyntaxException e) {
                return this.returnWithErrorMessage("Failed to parse type identifier '"+type+"' as a URI.");
            }
            return new IdentifyResult(typeURI, rep);
        }
    }
    
    private IdentifyResult returnWithErrorMessage(String message) {
        ServiceReport rep = new ServiceReport();
        URI type = null;
        log.error(message);
        rep.setErrorState(1);
        rep.setError("message");
        return new IdentifyResult(type, rep);
    }

}
