package eu.planets_project.services.validation.odfvalidators.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import eu.planets_project.services.utils.FileUtils;
import eu.planets_project.services.utils.ZipUtils;

public class OdfContentHandler {
	
	private static String MIMETYPE_XML = "mimetype";
	public static String CONTENT_XML = "content.xml";
	public static String STYLES_XML = "styles.xml";
	public static String META_XML = "meta.xml";
	public static String SETTINGS_XML = "settings.xml";
	public static String MANIFEST_XML = "manifest.xml";
	public static String DOC_DSIGS_XML = "documentsignatures.xml";
	public static String MACRO_DSIGS_XML = "macrosignatures.xml";
	
	private static final String MATHML_MIMETYPE = "application/vnd.oasis.opendocument.formula";
	
//	private static List<File> xmlComponents = null;
	private static List<String> manifestEntries = null;
	private static List<String> missingFileEntries = null;
	
	private static HashMap<String, File> odfSubFiles = null;
	
	private static File manifestXml = null;
	
	private static File mimetype_file = null;
	
	private static String mimeType_string = null;
	
	private boolean mimeTypeVerified = false;
	
	private boolean detectedDsigSubFiles = false;
	
	private String manifestMimeType = null;
	
	private boolean isNotODF = false;

	private String odfVersion = null;
	private String mathMLVersion = null;
	
	private static File ODF_VALIDATOR_TMP = null;
	private static File xmlTmp = null;
	
	private static Log log = LogFactory.getLog(OdfContentHandler.class);
	
	
	/**
	 * Constructor. Is initialized with the odfFile to be examined. 
	 * @param odfFile the odf file to validate
	 */
	public OdfContentHandler(File odfFile) {
		ODF_VALIDATOR_TMP = FileUtils.createFolderInWorkFolder(FileUtils.getPlanetsTmpStoreFolder(), "ODF_CONTENT_HANDLER");
		FileUtils.deleteAllFilesInFolder(ODF_VALIDATOR_TMP);
		xmlTmp = FileUtils.createFolderInWorkFolder(ODF_VALIDATOR_TMP, FileUtils.randomizeFileName("XML_CONTENT"));
		
		// 1) get all Odf sub files from zp container
		odfSubFiles = extractOdfSubFiles(odfFile);
		
		if(!isNotODF) {
			
			getVersions(odfSubFiles);
			
//			cleanUpContent(odfSubFiles);
			
			missingFileEntries = checkContainerConformity(odfFile);
		}
	}
	
	public File getCurrentXmlTmpDir() {
		return xmlTmp;
	}
	
	private HashMap<String, File> extractOdfSubFiles(File odfFile) {
		String[] files = ZipUtils.getAllFragments(odfFile);
		
		// if there are no files contained, it's not an Odf file ;-)
		// return an empty list and say sorry...
		if(files.length==0) {
			log.error("[OdfContentHandler] extractOdfSubFiles(): The input file '" + odfFile.getName() + "' is NOT an ODF file! Sorry, returning with error!");
			isNotODF = true;
			return new HashMap<String, File>();
		}
		
		HashMap<String, File> subFilesTmp = new HashMap<String, File>();
		for (String currentEntry : files) {
			
			if(currentEntry.endsWith(OdfContentHandler.MIMETYPE_XML)) {
				mimetype_file = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.CONTENT_XML)) {
				File tmpContent = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				subFilesTmp.put("content", tmpContent);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.SETTINGS_XML)) {
				File tmpSettings = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				subFilesTmp.put("settings", tmpSettings);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.STYLES_XML)) {
				File tmpStyles = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				subFilesTmp.put("styles", tmpStyles);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.MANIFEST_XML)) {
				File tmpManifest = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				subFilesTmp.put("manifest", tmpManifest);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.META_XML)) {
				File tmpMeta = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				subFilesTmp.put("meta", tmpMeta);
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.DOC_DSIGS_XML)) {
				File tmpDocDsig = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				subFilesTmp.put("doc_dsigs", tmpDocDsig);
				detectedDsigSubFiles = true;
				continue;
			}
			
			if(currentEntry.endsWith(OdfContentHandler.MACRO_DSIGS_XML)) {
				File tmpMacroDsig = ZipUtils.getFileFrom(odfFile, currentEntry, xmlTmp);
				subFilesTmp.put("macro_dsigs", tmpMacroDsig);
				detectedDsigSubFiles = true;
				continue;
			}
		}
		return subFilesTmp;
	}
	
	public boolean containsDsigSubFiles() {
		return detectedDsigSubFiles;
	}
	
	
	private String getMimeType(File odfMimeType) {
		String mime = FileUtils.readTxtFileIntoString(odfMimeType);
		return mime;
	}
	
	public boolean isMimeTypeVerified() {
		return mimeTypeVerified;
	}
	
	public String getManifestMimeType() {
		return manifestMimeType;
	}
	
	private String getMathMLVersion(File contentXml) {
		String contentString = FileUtils.readTxtFileIntoString(contentXml);
		String[] parts = contentString.split(" ");
		String version = null;
		for (int i=0;i<parts.length;i++) {
			String debug = parts[i];
			if(parts[i].equalsIgnoreCase("MathML")) {
				version = parts[i+1];
				version = "MathML:" + version.substring(0, version.lastIndexOf("//"));
				break;
			}
		}
		log.info("[OdfContentHandler] getMathMLVersion(): Found MathML version = " + version);
		return version;
	}
	
	public boolean containsDocTypeDeclaration(File mathmlXml) {
		String contentString = FileUtils.readTxtFileIntoString(mathmlXml);
		String docTypePattern = "<!DOCTYPE";
		return contentString.contains(docTypePattern);
	}
	
	
	public File removeMathMLDocTypeAndNS(File mathmlXML) {
		String contentString = FileUtils.readTxtFileIntoString(mathmlXML);
		contentString = contentString.replaceAll(">", ">" + System.getProperty("line.separator"));
		String[] lines = contentString.split(System.getProperty("line.separator"));
		String docTypePattern = "<!DOCTYPE";
		
		String dest = "<math:math xmlns:math=\"http://www.w3.org/1998/Math/MathML\"" 
			+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
			+ " xsi:schemaLocation=\"http://www.w3.org/1998/Math/MathML http://www.w3.org/Math/XMLSchema/mathml2/mathml2.xsd\">";
		
		for (int i=0;i<lines.length;i++) {
			if(lines[i].contains(docTypePattern)) {
				lines[i]="";
				continue;
			}
			if(lines[i].contains("xmlns:")) {
				lines[i] = dest;
				continue;
			}
		}
		
		StringBuffer content = new StringBuffer();
		
		for (String string : lines) {
			content.append(string.trim());
		}
		
		File cleanedTmp = new File(xmlTmp, FileUtils.randomizeFileName(mathmlXML.getName()));
		String finalStr = content.toString();
		FileUtils.writeStringToFile(content.toString(), cleanedTmp);
		
//		SAXBuilder builder = new SAXBuilder();
//		Document doc = null;
//		try {
//			doc = builder.build(new StringReader(contentString));
//			Element root = doc.getRootElement();
//			String rootContent = root.getTextTrim();
//			
//		} catch (JDOMException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		return cleanedTmp;
	}
	
	private String getOdfVersion(File odfSubFile) {
		SAXBuilder builder = new SAXBuilder(false);
		Document doc = null;
		try {
			doc = builder.build(odfSubFile);
			Element root = doc.getRootElement();
			Namespace ns = root.getNamespace();
			Attribute officeVersion = root.getAttribute("version", ns);
			String version = "ODF:" + officeVersion.getValue();
			log.info("[OdfContentHandler] getOdfVersion(): Found ODF version = " + version);
			return version;
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isOdfFile() {
		return !isNotODF;
	}

	public List<File> getXmlComponents() {
		List<File> subFiles = new ArrayList<File>(odfSubFiles.values());
		return subFiles;
	}

	private void getVersions(HashMap<String, File> odfSubFiles) {
		// read the Odf mimeType 
		mimeType_string = getMimeType(mimetype_file);
		
		// if the mimetype indicates a MathML file, retrieve the MathML version for schema handling
		// AND the ODF version for the other subfiles...
		if(mimeType_string.equalsIgnoreCase(OdfContentHandler.MATHML_MIMETYPE)) {
			mathMLVersion = getMathMLVersion(odfSubFiles.get("content"));
			
			// And now check the version of the other files...
			Set<String> keys = odfSubFiles.keySet();
			
			for (String string : keys) {
				if(string.equalsIgnoreCase("settings") 
						|| string.equalsIgnoreCase("styles") 
						|| string.equalsIgnoreCase("meta")) {
					odfVersion = getOdfVersion(odfSubFiles.get(string));
					break;
				}
			}
		}
		// if we don't have a formula file here just retrieve the Odf version from 'content.xml' for schema handling
		else {
			odfVersion = getOdfVersion(odfSubFiles.get("content"));
		}
	}

	public String getOdfVersion() {
		return odfVersion;
	}
	
	public String getMathMLVersion() {
		return mathMLVersion;
	}
	
	public String getMimeType() {
		return mimeType_string;
	}

	public boolean isOdfCompliant() {
		if(missingFileEntries.size()==0) {
			return true;
		}
		else {
			return false;
		}
	}

	public List<String> getMissingManifestEntries() {
		return missingFileEntries;
	}

//	public List<String> getManifestEntries() {
//		return manifestEntries;
//	}

	private List<File> initialize(File odfFile) {
		List<File> odfXmlParts = new ArrayList<File>(); 
		ODF_VALIDATOR_TMP = FileUtils.createFolderInWorkFolder(FileUtils.getPlanetsTmpStoreFolder(), "ODF_VALIDATOR_TMP");
		File xmlTmp = FileUtils.createFolderInWorkFolder(ODF_VALIDATOR_TMP, FileUtils.randomizeFileName("XML_CONTENT"));
		FileUtils.deleteAllFilesInFolder(xmlTmp);
		
		String[] files = ZipUtils.getAllFragments(odfFile);
		
		// Determine the MimeType
		for (String string : files) {
			if(string.endsWith(MIMETYPE_XML)) {
				File tmpMimetype = ZipUtils.getFileFrom(odfFile, string, xmlTmp);
				mimeType_string = FileUtils.readTxtFileIntoString(tmpMimetype);
				continue;
			}
		}
		
		if(files.length==0) {
			log.error("[OdfContentHandler] intialize(): The input file '" + odfFile.getName() + "' is NOT an ODF file! Sorry, returning with error!");
			isNotODF = true;
			return odfXmlParts;
		}
		
		for (String currentFragment : files) {
			if(currentFragment.endsWith(CONTENT_XML)) {
				File tmpContent = ZipUtils.getFileFrom(odfFile, currentFragment, xmlTmp);
				odfVersion = getOdfVersion(tmpContent);
				odfXmlParts.add(tmpContent);
				continue;
			}
			if(currentFragment.endsWith(MANIFEST_XML)) {
				manifestXml = ZipUtils.getFileFrom(odfFile, currentFragment, xmlTmp);
				odfXmlParts.add(manifestXml);
				continue;
			}
			if(currentFragment.endsWith(META_XML) 
					|| currentFragment.endsWith(SETTINGS_XML)
					|| currentFragment.endsWith(STYLES_XML)) {
				odfXmlParts.add(ZipUtils.getFileFrom(odfFile, currentFragment, xmlTmp));
				continue;
			}
		}
		return odfXmlParts;
	}
	
	private List<String> checkContainerConformity(File odfFile) {
		mimeTypeVerified = verifyManifestMimeType(mimeType_string, odfSubFiles.get("manifest"));
		
		manifestEntries = getManifestEntries(odfSubFiles.get("manifest"));
		List<String> missingList = new ArrayList<String>();
		if(!isNotODF) {
			List<String> odfContent = ZipUtils.listZipEntries(odfFile);
			
			for (String currentManifestEntry : manifestEntries) {
				if(!odfContent.contains(currentManifestEntry)) {
					if(!currentManifestEntry.contains("META-INF") && !currentManifestEntry.equalsIgnoreCase("/")) {
						missingList.add(currentManifestEntry);
					}
				}
			}
		}
		if(missingList.size()==0) {
			log.info("[OdfContentHandler] checkContainerIntegrity(): Congratulations!!! Successfully checked container integrity of file '" + odfFile.getName() + "'");
		}
		return missingList;
	}
	
	private List<String> getManifestEntries(File manifestXml) {
		List<String> manifestEntries = new ArrayList<String>(); 
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		try {
			doc = builder.build(manifestXml);
			Element root = doc.getRootElement();
			Namespace ns = root.getNamespace();
			List<Element> fileEntries = root.getChildren("file-entry", ns);
			for (Element currentElement : fileEntries) {
				Attribute fileEntryFullPath = currentElement.getAttribute("full-path", ns);
				manifestEntries.add(fileEntryFullPath.getValue());
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return manifestEntries;
	}

	private boolean verifyManifestMimeType(String mimeType, File manifest) {
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		String mediaType = null;
		try {
			doc = builder.build(manifest);
			Element root = doc.getRootElement();
			Namespace ns = root.getNamespace();
			List<Element> fileEntries = root.getChildren("file-entry", ns);
			
			for (Element currentElement : fileEntries) {
				mediaType = currentElement.getAttributeValue("media-type", ns);
				String path = currentElement.getAttributeValue("full-path", ns);
				if(path.equalsIgnoreCase("/") && mediaType.equalsIgnoreCase(mimeType)) {
					log.info("[OdfContentHandler] verifyManifestMimeType(): SUCCESS!!! 'META-INF/manifest.xml' mimeType verified!");
					return true;
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.warn("Mismatch warning: 'META-INF/manifest.xml' mimetype should be: '" + mimeType + "', BUT is: '" + mediaType + "!");
		manifestMimeType = mediaType;
		return false;
	}

}