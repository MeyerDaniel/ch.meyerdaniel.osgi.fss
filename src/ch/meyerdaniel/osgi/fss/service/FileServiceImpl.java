package ch.meyerdaniel.osgi.fss.service;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ch.meyerdaniel.osgi.fss.api.FileService;

public class FileServiceImpl implements FileService {

	private final FileSystem fileSystem;

	public FileServiceImpl() {
		fileSystem = FileSystems.getDefault();
	}

	@Override
	public Document readXMLFile(Path file) throws IOException, ParserConfigurationException, SAXException {
		try (InputStream is = Channels.newInputStream(FileChannel.open(file, READ))) {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);
			doc.getDocumentElement().normalize();
			return doc;
		}
	}

	@Override
	public void createXMLFile(Path file, Document document) throws IOException, TransformerException {
		try (OutputStream os = Channels.newOutputStream(FileChannel.open(file, CREATE))) {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(os);
			transformer.transform(source, result);
		}
	}

	@Override
	public Properties readProperties(Path file) throws IOException {
		try (InputStream is = Channels.newInputStream(FileChannel.open(file, READ))) {
			Properties prop = new Properties();
			prop.load(is);
			return prop;
		}
	}

	@Override
	public void storeProperties(Path file, Properties properies) throws IOException {
		try (OutputStream os = Channels.newOutputStream(FileChannel.open(file, CREATE))) {
			properies.store(os, "");
		}
	}

	@Override
	public FileSystem getFileSystem() {
		return fileSystem;
	}

	@Override
	public WatchService newWatchService() throws IOException {
		return fileSystem.newWatchService();
	}

}
