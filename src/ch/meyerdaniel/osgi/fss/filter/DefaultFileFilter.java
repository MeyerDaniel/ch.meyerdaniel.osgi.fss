package ch.meyerdaniel.osgi.fss.filter;

import java.nio.file.Path;
import java.util.HashSet;

import ch.meyerdaniel.osgi.fss.api.FileFilter;

/**
 * This class is used to decide whether a file will be included for further
 * processing or not. The default implementation supports recursive pattern
 * matches.
 * 
 * <p>
 * <b>Example:</b></br>
 * 
 * <code>*.*</code> Any files in the root directory.</br>
 * <code>**&#47;*.*</code> Any files.</br> <code>**&#47;*.xml</code> Any files
 * ending with <code>xml</code>.</br> <code>*.xml</code> Any files in the root
 * directory ending with <code>xml</code>.</br>
 * 
 * 
 * @author Daniel Meyer
 */
public class DefaultFileFilter implements FileFilter {

	private final HashSet<String> recursiveFilePatterns = new HashSet<>();
	private final HashSet<String> nonRecursiveFilePatterns = new HashSet<>();
	private Path rootPath;

	@Override
	public boolean accept(Path file) {
		int lastIndex = file.getFileName().toString().lastIndexOf(".");
		if (lastIndex != -1) {
			String extension = file.getFileName().toString().substring(lastIndex + 1);
			if (recursiveFilePatterns.contains("*") || recursiveFilePatterns.contains(extension)) {
				return true;
			} else if (/* The file is in the root directory */file.getParent().equals(rootPath)) {
				return nonRecursiveFilePatterns.contains("*") || nonRecursiveFilePatterns.contains(extension);
			}
		}
		return false;
	}

	@Override
	public void addFilePattern(String filePattern) {
		if (filePattern.startsWith("**/*.")) {
			if (filePattern.equals("**/*.*")) {
				recursiveFilePatterns.clear();
			}
			if (!recursiveFilePatterns.contains("*.*")) {
				recursiveFilePatterns.add(filePattern.substring(5));
			}
		} else if (filePattern.startsWith("*.")) {
			if (filePattern.equals("*.*")) {
				nonRecursiveFilePatterns.clear();
			}
			if (!nonRecursiveFilePatterns.contains("*.*")) {
				nonRecursiveFilePatterns.add(filePattern.substring(2));
			}
		}
		/**
		 * Patterns in the recursive collection have higher severity than the
		 * others.
		 */
		nonRecursiveFilePatterns.removeAll(recursiveFilePatterns);
	}

	@Override
	public void setRootPath(Path rootPath) {
		this.rootPath = rootPath;
	}
}
