package eu.chuvash.android.dialer.util;

import java.io.File;
import java.io.FileFilter;

import android.util.Log;

public class FILE {
	private static final String TAG = "FILE";
	// make instantiating unavailable:
	private FILE() {}
	public static File[] getChildrenDirs(String dirPath) {
		File[] dirs = null;
		File dir = new File(dirPath);
		if (dir.exists() && dir.isDirectory()) {
			// how to list only directories:
			// http://www.javafaq.nu/java-example-code-120.html
			FileFilter dirFilter = new FileFilter() {
				@Override
				public boolean accept(File dirToFilter) {
					return dirToFilter.isDirectory();
				}
			};
			dirs = dir.listFiles(dirFilter);
		}
		return dirs;
	}
	// removes all files with a specific extension in a specific directory
	public static void cleanDirectory(String directoryToClean, String extension) {
		Log.d(TAG, "directoryToClean: " + directoryToClean);
		File dir = new File(directoryToClean);
		if (dir.exists() && dir.isDirectory()) {
			//Log.d(TAG, "directoryToClean, dir exists and is a directory");
			String[] files = dir.list();
			for (String file : files) {
				//Log.d(TAG, "file i katalogen: " + file);
				String ext = file.substring(
						file.lastIndexOf(".") + 1, file.length());
				//Log.d(TAG, "extension: " + extension);
				if (ext.equals(extension)) {
					//Log.d(TAG, "Equals ext dps: " + file);
					File f = new File(directoryToClean + file);
					if (f.isFile() && f.canWrite()) {
						//Log.d(TAG, "trying to delete a file: " + file);
						boolean success = f.delete();
						if (success) {
							Log.d(TAG, "Removed: " + file);
						}
					}
				}
			}
		}
	}
}
