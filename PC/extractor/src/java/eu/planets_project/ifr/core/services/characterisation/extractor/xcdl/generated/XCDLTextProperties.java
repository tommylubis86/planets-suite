//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB)
// Reference Implementation, vhudson-jaxb-ri-2.1-661
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source
// schema.
// Generated on: 2009.01.16 at 04:26:00 PM CET
//

package eu.planets_project.ifr.core.services.characterisation.extractor.xcdl.generated;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for XCDLTextProperties.
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name=&quot;XCDLTextProperties&quot;&gt;
 *   &lt;restriction base=&quot;{http://www.w3.org/2001/XMLSchema}string&quot;&gt;
 *     &lt;enumeration value=&quot;font&quot;/&gt;
 *     &lt;enumeration value=&quot;fontChange&quot;/&gt;
 *     &lt;enumeration value=&quot;fontSize&quot;/&gt;
 *     &lt;enumeration value=&quot;numberOfPages&quot;/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 */
@XmlType(name = "XCDLTextProperties")
@XmlEnum
public enum XCDLTextProperties {

    @XmlEnumValue("font")
    FONT("font"), @XmlEnumValue("fontChange")
    FONT_CHANGE("fontChange"), @XmlEnumValue("fontSize")
    FONT_SIZE("fontSize"), @XmlEnumValue("numberOfPages")
    NUMBER_OF_PAGES("numberOfPages");
    private final String value;

    XCDLTextProperties(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static XCDLTextProperties fromValue(String v) {
        for (XCDLTextProperties c : XCDLTextProperties.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
