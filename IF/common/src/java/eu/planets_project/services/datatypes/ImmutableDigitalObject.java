package eu.planets_project.services.datatypes;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import eu.planets_project.services.PlanetsServices;

/**
 * Representation of an immutable, comparable concrete digital object, to be
 * passed through web services and serializable with JAXB. As the other planets
 * data types, it uses XmlAccessType.FIELD instead of getters and setters. This
 * allows for proper encapsulation on the API side while remaining
 * JAXB-serializable.
 * <p/>
 * This class is immutable in practice; its instances can therefore be shared
 * freely and concurrently.
 * <p/>
 * A corresponding XML schema can be generated from this class by running this
 * class as a Java application, see {@link #main(String[])}.
 * @author <a href="mailto:fabian.steeg@uni-koeln.de">Fabian Steeg</a>
 * @see DigitalObjectTests
 */
@XmlRootElement( name = "digitalObject", namespace = PlanetsServices.OBJECTS_NS )
@XmlType( namespace = PlanetsServices.OBJECTS_NS )
@XmlAccessorType( value = XmlAccessType.FIELD )
/*
 * NOTE: This class is intentionally NOT PUBLIC. Clients should use a
 * DigitalObject.Builder to instantiate digital objects.
 */
final class ImmutableDigitalObject implements
        Comparable<ImmutableDigitalObject>, DigitalObject, Serializable {

    /** Generated UID. */
    private static final long serialVersionUID = -893249048201058999L;

    /** @see {@link #getTitle()} */
    @XmlAttribute( required = true )
    private String title;

    /** @see {@link #getFormat()} */
    @XmlAttribute
    private URI format;

    /** @see {@link #getPermanentUri()} */
    @XmlAttribute
    private URI permanentUri;

    /** @see {@link #getManifestationOf()} */
    @XmlAttribute
    private URI manifestationOf;

    /** @see {@link #getMetadata()} */
    @XmlElement( namespace = PlanetsServices.OBJECTS_NS )
    private List<Metadata> metadata;

    /** @see {@link #getContained()} */
    @XmlElement( namespace = PlanetsServices.OBJECTS_NS )
    private List<DigitalObject> contained;

    /** @see {@link #getContent()} */
    @XmlElement( namespace = PlanetsServices.OBJECTS_NS, required = true )
    private ImmutableContent content;

    /** @see {@link #getEvents()} */
    @XmlElement( namespace = PlanetsServices.OBJECTS_NS )
    private List<Event> events;

    /** @see {@link #getFragments()} */
    @XmlElement( namespace = PlanetsServices.OBJECTS_NS )
    private List<Fragment> fragments;

    /**
     * @param builder The builder to construct a digital object from
     */
    ImmutableDigitalObject(final Builder builder) {
        permanentUri = builder.getPermanentUri();
        /*
         * FIXME: We cast here to allow using the concrete content
         * implementation class in this digital object implementation in order
         * to avoid having to make the content interface serializable (which
         * this digital object implementation is, but not digital objects in
         * general). While this is safe in our current setup, it is not ideal,
         * because this digital object implementation can't be combined with a
         * different content implementation. We currently need Java
         * Serialization support since GUI components we use require it.
         */
        content = (ImmutableContent) builder.getContent();
        contained = builder.getContained();
        events = builder.getEvents();
        fragments = builder.getFragments();
        manifestationOf = builder.getManifestationOf();
        title = builder.getTitle();
        metadata = builder.getMetadata();
        format = builder.getFormat();
    }

    /**
     * No-args constructor for JAXB serialization. Should not be called by an
     * API client. Clients should use:
     * <p/>
     * {@code new DigitalObject.Builder(required args...)optional
     * args...build();}
     */
    @SuppressWarnings( "unused" )
    private ImmutableDigitalObject() {}

    /**
     * @param xml The XML representation of a digital object (as created from
     *            calling toXml)
     * @return A digital object instance created from the given XML
     */
    static ImmutableDigitalObject of(final String xml) {
        try {
            /* Unmarshall with JAXB: */
            JAXBContext context = JAXBContext
                    .newInstance(ImmutableDigitalObject.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Object object = unmarshaller.unmarshal(new StringReader(xml));
            ImmutableDigitalObject unmarshalled = (ImmutableDigitalObject) object;
            return unmarshalled;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.DigitalObject#toXml()
     */
    public String toXml() {
        try {
            /* Marshall with JAXB: */
            JAXBContext context = JAXBContext
                    .newInstance(ImmutableDigitalObject.class);
            Marshaller marshaller = context.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#toString()
     */
    public String toString() {
        int contentSize = content == null ? 0 : 1;
        String checksum = content == null || content.getChecksum() == null
                ? "" : content.getChecksum().toString();
        int containedSize = contained == null ? 0 : contained.size();
        int eventsSize = events == null ? 0 : events.size();
        int fragmentsSize = fragments == null ? 0 : fragments.size();
        int metaSize = metadata == null ? 0 : metadata.size();
        return String
                .format(
                        "DigitalObject: id '%s', title '%s'; %s content elements, "
                                + "%s contained objects, %s events, %s fragments; "
                                + "type '%s', manifestation of '%s', checksum '%s', metadata '%s'",
                        permanentUri, title, contentSize, containedSize,
                        eventsSize, fragmentsSize, format, manifestationOf,
                        checksum, metaSize);
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final ImmutableDigitalObject o) {
        if (this.permanentUri != null && o.permanentUri != null) {
            /* The ID is optional, so if we have one we use it: */
            return this.permanentUri.toString().compareTo(
                    o.permanentUri.toString());
        } else if (this.permanentUri != null || o.permanentUri != null) {
            /* If only one of them is defined, they are not equal: */
            return -1;
        } else {
            /* But if none is, we use the XML serialization: */
            return this.toXml().compareTo(o.toXml());
        }
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof ImmutableDigitalObject
                && this.compareTo((ImmutableDigitalObject) obj) == 0;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (permanentUri != null) {
            return permanentUri.toString().hashCode();
        } else {
            return this.toXml().hashCode();
        }
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.DigitalObject#getTitle()
     */
    public String getTitle() {
        return title;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.DigitalObject#getFormat()
     */
    public URI getFormat() {
        return format;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.DigitalObject#getPermanentUri()
     */
    public URI getPermanentUri() {
        return permanentUri;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.DigitalObject#getManifestationOf()
     */
    public URI getManifestationOf() {
        return manifestationOf;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.DigitalObject#getMetadata()
     */
    public List<Metadata> getMetadata() {
        return metadata == null ? null : new ArrayList<Metadata>(metadata);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.DigitalObject#getContained()
     */
    public List<DigitalObject> getContained() {
        return contained == null ? null : new ArrayList<DigitalObject>(
                contained);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.DigitalObject#getContent()
     */
    public DigitalObjectContent getContent() {
        return content;
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.DigitalObject#getEvents()
     */
    public List<Event> getEvents() {
        return events == null ? null : new ArrayList<Event>(events);
    }

    /**
     * {@inheritDoc}
     * @see eu.planets_project.services.datatypes.DigitalObject#getFragments()
     */
    public List<Fragment> getFragments() {
        return fragments == null ? null : new ArrayList<Fragment>(fragments);
    }

    /* Schema generation: */

    /***/
    private static java.io.File baseDir = new java.io.File(
            "IF/common/src/resources");
    /***/
    private static String schemaFileName = "digital_object.xsd";

    /** Resolver for schema generation. */
    static class Resolver extends SchemaOutputResolver {
        /**
         * {@inheritDoc}
         * @see javax.xml.bind.SchemaOutputResolver#createOutput(java.lang.String,
         *      java.lang.String)
         */
        public Result createOutput(final String namespaceUri,
                final String suggestedFileName) throws IOException {
            return new StreamResult(new java.io.File(baseDir, schemaFileName
                    .split("\\.")[0]
                    + "_" + suggestedFileName));

        }
    }

    /**
     * Generates the XML schema for this class.
     * @param args Ignored
     */
    public static void main(final String[] args) {
        try {
            Class<ImmutableDigitalObject> clazz = ImmutableDigitalObject.class;
            JAXBContext context = JAXBContext.newInstance(clazz);
            context.generateSchema(new Resolver());
            System.out.println("Generated XML schema for "
                    + clazz.getSimpleName()
                    + " at "
                    + new java.io.File(baseDir, schemaFileName)
                            .getAbsolutePath());
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
