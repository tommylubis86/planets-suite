/**
 * Just for testing Web services
 */
package eu.planets_project.ifr.core.wdt.impl;

import eu.planets_project.ifr.core.wdt.api.Monitor;
import eu.planets_project.ifr.core.wdt.api.BeanObject;

import javax.ejb.Remote;
import javax.ejb.Local;
import javax.ejb.Stateless;

import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;

/**
 * @author Rainer Schmidt
 *
 */
@javax.jws.WebService(name="WdtMonitor", targetNamespace="http://planets-project.eu/ifr/core/wdt/monitor", serviceName="WdtMonitor")
@Stateless(mappedName="planets/LocalWdtMonitor")
@Local(Monitor.class)
@Remote(Monitor.class)
@LocalBinding(jndiBinding="planets/LocalWdtMonitor")
@RemoteBinding(jndiBinding="planets-project.eu/LocalWdtMonitor")
@BindingType(value="http://schemas.xmlsoap.org/wsdl/soap/http?mtom=true")
@javax.jws.soap.SOAPBinding(style = SOAPBinding.Style.RPC)
public class MonitorImpl implements Monitor {


	@javax.jws.WebMethod()
	@javax.jws.WebResult(name="outBean")
	public BeanObject monitor(@javax.jws.WebParam(name="inBean") BeanObject obj) {
		long id = obj.getId();
		id = id + 1L;
		obj.setId(id);
		return obj;
	}	
}