/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package at.tuwien.minimee.admin;

import javax.ejb.Local;

/**
 * interface for {@link MiniMeeAdminAction}
 * @author Christoph Becker
 */
@Local
public interface IMiniMeeAdmin  {
    public void destroy();
    public String reloadRegistry();
    public String verifySetup();
    public String benchmark();
    public String reloadRegistryFromPath();
}
