/**
 * 
 */
package eu.planets_project.ifr.core.techreg.formats;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


/**
 * A mutable, hidden implementation of the {@link Format} interface.
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 */
final class MutableFormat implements Serializable, Format {

    /**
     * A format URI representing any format, e.g. to define that a service can
     * process any format.
     */
    public static final URI ANY = URI.create(FormatUtils.ANY_FORMAT);
    

    /**
     * A format URI representing a "folder".
     */
    public static final URI FOLDER = URI.create(FormatUtils.FOLDER_TYPE);

    /**
     * A format URI representing an unknown format, e.g. to set the format of a
     * digital object to be identified.
     */
    public static final URI UNKNOWN = URI.create(FormatUtils.UNKNOWN_FORMAT);

    private static final long serialVersionUID = -4713590391811379383L;

    private URI typeURI;

    private Set<URI> aliases = new HashSet<URI>();

    private URL registryURL;

    private String summary;

    private String version;

    private Set<String> mimeTypes = new HashSet<String>();

    private Set<String> extensions = new HashSet<String>();

    /**
     * @param typeURI The type URI (see FormatRegistry)
     */
    protected MutableFormat(URI typeURI) {
        this.typeURI = typeURI;
        if (FormatUtils.isMimeUri(typeURI)) {
            String mime = typeURI.toString().replace(
                    FormatUtils.MIME_URI_PREFIX, "");
            this.mimeTypes = new HashSet<String>();
            this.mimeTypes.add(mime);
            this.summary = mime.toLowerCase();
        } else if (FormatUtils.isExtensionUri(typeURI)) {
            String ext = typeURI.toString().replace(FormatUtils.EXT_URI_PREFIX,
                    "");
            this.extensions = new HashSet<String>();
            this.extensions.add(ext);
            this.summary = ext.toUpperCase();
        }
    }

    /**
     * @return the typeURI
     */
    public URI getUri() {
        return typeURI;
    }

    /**
     * @param typeURI the typeURI to set
     */
    public void setTypeURI(URI typeURI) {
        this.typeURI = typeURI;
    }

    /**
     * @return the aliases
     */
    public Set<URI> getAliases() {
        return aliases;
    }

    /**
     * @param aliases the aliases to set
     */
    public void setAliases(Set<URI> aliases) {
        this.aliases = aliases;
    }

    /**
     * @return the registryURL
     */
    public URL getRegistryUrl() {
        if (registryURL == null) {
            try {
                return typeURI.toURL();
            } catch (MalformedURLException e) {
                return null;
            }
        }
        return registryURL;
    }

    /**
     * @param registryURL the registryURL to set
     */
    public void setRegistryUrl(URL registryURL) {
        this.registryURL = registryURL;
    }

    /**
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @return the summary with the version number appended.
     */
    public String getSummaryAndVersion() {
        if (version != null)
            return summary + " " + version;
        return summary;
    }

    /**
     * @param summary the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the mimeTypes
     */
    public Set<String> getMimeTypes() {
        return mimeTypes;
    }

    /**
     * @param mimeTypes the mimeTypes to set
     */
    public void setMimeTypes(Set<String> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    /**
     * @return the extensions
     */
    public Set<String> getExtensions() {
        return extensions;
    }

    /**
     * @param extensions the extensions to set
     */
    public void setExtensions(Set<String> extensions) {
        this.extensions = extensions;
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MutableFormat) {
            MutableFormat f = (MutableFormat) obj;
            /*
             * Format objects MUST be uniquely identified by the assigned URI.
             */
            return this.typeURI.equals(f.typeURI);
        } else {
            return super.equals(obj);
        }
    }

    /**
     * {@inheritDoc}
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.typeURI.hashCode();
    }
}