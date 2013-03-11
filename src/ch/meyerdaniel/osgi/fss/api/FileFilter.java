package ch.meyerdaniel.osgi.fss.api;

import java.nio.file.Path;

import ch.meyerdaniel.osgi.fss.filter.DefaultFileFilter;
import ch.meyerdaniel.osgi.fss.service.intern.ConfigurableWatchService;

/**
 * This interface describes the functionality for file filters used in
 * {@link ConfigurableWatchService}.
 * 
 * @author Daniel Meyer
 */
public interface FileFilter {

	/**
	 * Test whether the specified file name should be included for further
	 * processing or not.
	 * 
	 * @param file
	 *            Any file.
	 * @return <code>true</code> if the file is should be included.
	 */
	public boolean accept(Path file);

	/**
	 * Adds the specified pattern on that the decision is based.
	 * 
	 * See {@link DefaultFileFilter} which patterns are supported.
	 * 
	 * @param filePattern
	 *            A specific file pattern.
	 */
	public void addFilePattern(String filePattern);

	/**
	 * Use this method to set the root path for non-recursive based decisions.
	 * 
	 * @param rootPath
	 *            The root path.
	 */
	public void setRootPath(Path rootPath);

}
