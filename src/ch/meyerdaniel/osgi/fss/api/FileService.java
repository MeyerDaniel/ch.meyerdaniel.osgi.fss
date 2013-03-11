package ch.meyerdaniel.osgi.fss.api;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This interface describes features mostly used for using configuration files.
 * 
 * @author Daniel Meyer
 * 
 */
public interface FileService {

	/**
	 * Reads the specified property file.
	 * 
	 * @param file
	 *            Any property file.
	 * @return A property object that contains any properties from the specified
	 *         file.
	 * @throws IOException
	 *             {@link IOException}
	 */
	public Properties readProperties(Path file) throws IOException;

	/**
	 * Stores the specified properties. If the file already exists it will be
	 * overridden.
	 * 
	 * @param file
	 *            The property file.
	 * @param properies
	 *            The properties to store.
	 * @throws IOException
	 *             {@link IOException}
	 */
	public void storeProperties(Path file, Properties properies) throws IOException;

	/**
	 * @param file
	 *            Reads the specified XML file.
	 * @return A document of the specified XML file.
	 * @throws IOException
	 *             {@link IOException}
	 * @throws ParserConfigurationException
	 *             {@link ParserConfigurationException}
	 * @throws SAXException
	 *             {@link SAXException}
	 */
	public Document readXMLFile(Path file) throws IOException, ParserConfigurationException, SAXException;

	/**
	 * Stores the specified document as XML. If the file already exists it will
	 * be overridden.
	 * 
	 * @param file
	 *            The XML file.
	 * @param document
	 *            The document.
	 * @throws IOException
	 *             {@link IOException}
	 * @throws TransformerException
	 *             {@link TransformerException}
	 */
	public void createXMLFile(Path file, Document document) throws IOException, TransformerException;

	/**
	 * Returns the default file system.
	 * 
	 * @return The default file system.
	 */
	public FileSystem getFileSystem();

	/**
	 * Returns a new file watcher.
	 * 
	 * @return A file watcher for the default file system.
	 * @throws IOException
	 *             {@link IOException}
	 */
	public WatchService newWatchService() throws IOException;

}
