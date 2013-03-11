package ch.meyerdaniel.osgi.fss.service.intern;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.MessageFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.meyerdaniel.osgi.fss.api.FileFilter;

/**
 * This class implements the ability to configure a wrapped watch service.
 * 
 * @author Daniel Meyer
 * 
 */
public class ConfigurableWatchService {

	private final Logger log;

	private final FileFilter fileFilter;
	private final Path root;
	private final WatchService watchService;

	private final ConcurrentHashMap<WatchKey, Path> keys;
	private WatchServiceController controller;

	private final String name;

	/**
	 * @param name
	 *            The name of this watch service.
	 * @param watchService
	 *            The concrete watch service.
	 * @param controller
	 *            The controller for processing events.
	 * @param root
	 *            The root path for this watch service.
	 * @param fileFilter
	 *            A concrete file filter.
	 */
	public ConfigurableWatchService(String name, WatchService watchService, WatchServiceController controller, Path root, FileFilter fileFilter) {
		log = LoggerFactory.getLogger(this.getClass().getName() + "[" + name + "]");
		keys = new ConcurrentHashMap<>();
		this.name = name;
		this.root = root;
		this.fileFilter = fileFilter;
		this.watchService = watchService;
		this.controller = controller;
		fileFilter.setRootPath(root);
	}

	/**
	 * Initializes this watch service and returns an instance of
	 * {@link Callable} that is coupled to this configurable watch service.
	 * 
	 * @return True
	 * @throws IOException
	 *             Can be thrown during the discovering process.
	 */
	public Callable<Boolean> init() throws IOException {
		discoverFiles(root);

		return new Callable<Boolean>() {

			@Override
			public Boolean call() throws IOException {
				log.info(MessageFormat.format("Started watch service based on configuration {0}.", name));

				try {
					WatchKey key = null;
					do {
						key = watchService.take();
						Path path = keys.get(key);

						for (WatchEvent<?> i : key.pollEvents()) {
							WatchEvent<Path> event = (WatchEvent<Path>) i;
							WatchEvent.Kind<Path> kind = event.kind();
							Path name = event.context();
							Path child = path.resolve(name);

							log.debug(MessageFormat.format("Event received for {0}: {1}.", child.getFileName().toString(), kind.name()));

							if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
								if (kind == ENTRY_CREATE) {
									try {
										discoverFiles(child);
									} catch (IOException e) {
										log.error("", e);
									}
								}
							} else if (fileFilter.accept(child)) {
								if (kind == ENTRY_CREATE) {
								} else if (kind == ENTRY_MODIFY) {
									controller.processFile(child);
								} else if (kind == ENTRY_DELETE) {
									controller.processDeletedFile(child);
								}
							}
						}

					} while (key.reset());

				} catch (InterruptedException e) {
					// do nothing
				}
				shutdown();
				return true;
			}
		};
	}

	private void discoverFiles(Path root) throws IOException {
		Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				if (fileFilter.accept(file)) {
					log.debug(MessageFormat.format("Process file {0}.", file.getFileName().toString()));
					controller.processFile(file);
				}
				return super.visitFile(file, attrs);
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				log.debug(MessageFormat.format("Watch on directory {0}.", dir.getFileName().toString()));
				WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
				keys.put(key, dir);
				return super.preVisitDirectory(dir, attrs);
			}
		});
	}

	private void shutdown() throws IOException {
		controller = null;
		watchService.close();
		log.info(MessageFormat.format("Stopped watch service based on configuration {0}.", name));
	}
}
