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

package eu.planets_project.pp.plato.model.values;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import eu.planets_project.pp.plato.util.FloatFormatter;

@Entity
@DiscriminatorValue("N")
public class PositiveFloatValue extends Value  implements INumericValue {

    private static final long serialVersionUID = -1170922225142475324L;

    @Transient
    private FloatFormatter formatter;

    @Column(name = "float_value")
    private double value;
    
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        // also save invalid values, they are checked later with isChanged()
        this.value = value;
    }

    public double value() {
        return value;
    }

    @Override
    public String toString() {
        if (formatter == null) {
            formatter = new FloatFormatter();
        }
        return formatter.formatFloatPrecisly(value);
    }
    
    @Override
    public String getFormattedValue() {
        if (formatter == null) {
            formatter = new FloatFormatter();
        }
        return formatter.formatFloat(value);
    }
    
    @Override
    public void parse(String text) {
        setValue(Double.parseDouble(text));        
    }
    
}
