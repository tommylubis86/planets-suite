package eu.planets_project.ifr.core.servreg.api;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.namespace.QName;

import eu.planets_project.services.PlanetsServices;
import eu.planets_project.services.datatypes.ServiceDescription;

/**
 * Registry interface based on the new ServiceDescription objects, supporting query by example.
 * @see ServiceDescription
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
@WebService
public interface ServiceRegistry {

    /** The interface name. */
    String NAME = "ServiceRegistry";
    /** The qualified name. */
    QName QNAME = new QName(PlanetsServices.NS, ServiceRegistry.NAME);

    /**
     * @param serviceDescription The service description to register
     * @return A response message
     */
    @WebMethod(exclude = true)
    Response register(ServiceDescription serviceDescription);

    /**
     * Query by example registry lookup.
     * @param example The sample service description
     * @return The services for which all non-null values correspond to the values of the given sample object
     */
    @WebMethod
    @WebResult
    List<ServiceDescription> query(ServiceDescription example);

    /**
     * Query by example registry lookup with a specified lookup strategy.
     * @param example The sample service description
     * @param mode The matching strategy to use when matching against the given sample
     * @return The services for which all non-null values correspond to the values of the given sample object, based on
     *         the supplied matching strategy
     */
    @WebMethod
    @WebResult
    /*
     * This method does not overload query(...) as overloading is not supported in recent versions of the WSDL (1.2,
     * 2.0)
     */
    List<ServiceDescription> queryWithMode(ServiceDescription example, MatchingMode mode);

    /**
     * Clears the registry of all entries.
     * @return A response message
     */
    @WebMethod(exclude = true)
    Response clear();

    /**
     * @param example The sample of the service descriptions to delete
     * @return A response message
     */
    @WebMethod(exclude = true)
    Response delete(ServiceDescription example);
}
