package eu.planets_project.ifr.core.services.migration.jmagickconverter.impl;

//import java.io.BufferedInputStream;
import java.io.Serializable;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

import eu.planets_project.ifr.core.common.api.PlanetsBasicService;
import eu.planets_project.ifr.core.common.api.PlanetsException;
import eu.planets_project.ifr.core.common.logging.PlanetsLogger;
import eu.planets_project.ifr.core.common.services.PlanetsServices;
import eu.planets_project.ifr.core.common.services.migrate.BasicMigrateOneBinary;
import eu.planets_project.ifr.core.services.migration.jmagickconverter.api.PngToTiffConverterLocalInterface;
import eu.planets_project.ifr.core.services.migration.jmagickconverter.api.PngToTiffConverterRemoteInterface;
import eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.utils.GeneralImageConverter;
import eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.utils.MigrationResults;

/**
 * The purpose of this class is the image migration from PNG to TIFF.
 * Therefor it uses the GeneralImageConverterClass, passing a byte[] containing the
 * src-image data, the required src-format (in this case PNG which will be
 * checked before the migration to prevent undesired results) and the target
 * format (in this case TIFF).
 * 
 * This Class is a Webservice realised using an EJB3.0 Bean.
 *    
 * @author : Peter Melms
 * Email : peter.melms@uni-koeln.de 
 * Created : 27.05.2008
 */
@Stateless()
@Local(BasicMigrateOneBinary.class)
@Remote(BasicMigrateOneBinary.class)
@LocalBinding(jndiBinding = "planets/PngToTiffConverter")
@RemoteBinding(jndiBinding = "planets-project.eu/PngToTiffConverter")
@BindingType(value = "http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@WebService(
        name = "PngoTiffConverter", 
        serviceName = BasicMigrateOneBinary.NAME, 
        targetNamespace = PlanetsServices.NS )
@SOAPBinding(
        parameterStyle = SOAPBinding.ParameterStyle.BARE,
        style = SOAPBinding.Style.RPC)
public class PngToTiffConverter implements Serializable, BasicMigrateOneBinary {

    private static final long serialVersionUID = 5932270154349298856L;
    // Creating a PlanetsLogger...
    private final static String logConfigFile = "eu/planets_project/ifr/core/services/migration/jmagickconverter/logconfig/pngtotiffconverter-log4j.xml";
    private PlanetsLogger plogger = PlanetsLogger.getLogger(this.getClass());
    
    // a GeneralImageConverter instance to carry out the migration.
    private GeneralImageConverter converter = new GeneralImageConverter();

    /**
     * This method derives from implementing the PlanetsBasicService interface.
     * It receives a byte[] and hands it over to the GeneralImageConverter
     * instance, who carries out the migration. The results of this migration
     * are returned as a MigrationResults instance, containing the migrated
     * image as byte[], a message and a flag indicating success (or not).
     */
    /*
     * (non-Javadoc)
     * 
     * @see eu.planets_project.ifr.core.common.api.PlanetsBasicService#basicMigrateBinary(byte[])
     * 
     */
    @WebMethod(
            operationName = BasicMigrateOneBinary.NAME, 
            action = PlanetsServices.NS + "/" + BasicMigrateOneBinary.NAME)
    @WebResult(
            name = BasicMigrateOneBinary.NAME+"Result", 
            targetNamespace = PlanetsServices.NS + "/" + BasicMigrateOneBinary.NAME, 
            partName = BasicMigrateOneBinary.NAME + "Result")
    public byte[] basicMigrateOneBinary ( 
            @WebParam(name = "binary", targetNamespace = PlanetsServices.NS + "/" + BasicMigrateOneBinary.NAME, partName = "binary")
            byte[] binary ) throws PlanetsException {
		MigrationResults migrationResults = new MigrationResults();
		migrationResults = converter.convertImage(binary, "PNG", "TIFF", plogger);
		if (migrationResults.migrationWasSuccessful()) {
		    plogger.debug(migrationResults.getMessage());
		    return migrationResults.getByteArray();
		} else {
			plogger.warn(migrationResults.getMessage());		
		    throw new PlanetsException(migrationResults.getMessage());
		}
    }
}
