/**
 * 
 */
package eu.planets_project.tb.impl.services.wrappers;

import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.xml.ws.Service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import eu.planets_project.services.datatypes.DigitalObject;
import eu.planets_project.services.datatypes.Content;
import eu.planets_project.services.datatypes.Parameter;
import eu.planets_project.services.datatypes.ServiceDescription;
import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.ServiceReport.Status;
import eu.planets_project.services.datatypes.ServiceReport.Type;
import eu.planets_project.services.migrate.Migrate;
import eu.planets_project.services.migrate.MigrateResult;
import eu.planets_project.services.utils.ServiceUtils;
import eu.planets_project.tb.impl.services.util.PlanetsServiceExplorer;

/**
 * This is a wrapper class that upgrades all supported Migrate service
 * interfaces to the same level.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 * 
 */
@SuppressWarnings("deprecation")
public class MigrateWrapper implements Migrate {
    /** */
    private static final Log log = LogFactory.getLog(MigrateWrapper.class);

    PlanetsServiceExplorer pse = null;
    Service service = null;
    Migrate m = null;
    
    /**
     * @param wsdl The WSDL to wrap as a service.
     */
    public MigrateWrapper( URL wsdl ) {
        this.pse = new PlanetsServiceExplorer(wsdl);
        this.init();
    }

    /**
     * @param pse Construct based on a service explorer.
     */
    public MigrateWrapper(PlanetsServiceExplorer pse) {
        this.pse = pse;
        this.init();
    }

    /**
     * 
     */
    private void init() {
        service = Service.create(pse.getWsdlLocation(), pse.getQName());
        try {
            m = (Migrate) service.getPort(pse.getServiceClass());
        } catch( Exception e ) {
            log.error("Failed to instanciate service "+ pse.getQName() +" at "+pse.getWsdlLocation() + " : Exception - "+e);
            m = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.planets_project.services.migrate.Migrate#describe()
     */
    public ServiceDescription describe() {
        return m.describe();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.planets_project.services.migrate.Migrate#migrate(eu.planets_project
     * .services.datatypes.DigitalObject, java.net.URI, java.net.URI,
     * eu.planets_project.services.datatypes.Parameters)
     */
    public MigrateResult migrate(DigitalObject digitalObject, URI inputFormat,
            URI outputFormat, List<Parameter> parameters) {

        // Transform the DO into a single binary, if that is sane
        return m.migrate(digitalObject, inputFormat, outputFormat, parameters);
    }

}
