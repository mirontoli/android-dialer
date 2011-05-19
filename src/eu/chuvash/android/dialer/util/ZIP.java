package eu.chuvash.android.dialer.util;

/**
 * @author Robert Jonsson, ITM Östersund
 * @version 2.0
 * @file Ex03_11 - ZIP.java
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import android.util.Log;

public class ZIP {
	// ******************************************************
	// METODER FÖR ATT PACKA FILER
	// ******************************************************

	// Statisk metod för att packa en fil till angiven zip-fil
	public static boolean compress(String file, String zipFile) {
		String[] tmp = { file };
		return compress(tmp, zipFile);
	}

	// Statisk metod för att packa flera filer till angiven zip-fil
	public static boolean compress(String[] files, String zipFile) {
		boolean ok = true;

		Log.d("ZIP", "Start compressing to " + zipFile);
		try {
			// Skapar eventuella kataloger som behövs
			File zipFileParent = new File(zipFile).getParentFile();
			if (zipFileParent != null)
				zipFileParent.mkdirs();

			// Skapar ZIP-filen
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(zipFile)));

			// Loopa igenom de filer som ska packas
			for (int i = 0; i < files.length; i++) {
				// Anropar metod som packar filen
				if (!compress(new File(files[i]), out)) {
					ok = false;
				}
			}

			// Stänger zip-filen när vi är klar
			out.close();
		} catch (IOException fel) {
			Log.e("ZIP", "Error compressing to " + zipFile + "\n"
					+ fel.toString());
			return false;
		}

		Log.d("ZIP", "Done compressing");
		return ok;
	}

	// Packar en fil med angiven ZipOutpuStream
	private static boolean compress(File file, ZipOutputStream out) {
		// Kontrollerar först om det är en katalog som ska packas
		if (file.isDirectory()) {
			// Hämtar i så fall de filer som finns i katalogen
			File[] files = file.listFiles();

			// Loopar igenom alla filer
			for (int i = 0; i < files.length; i++) {
				// Anropar metod för att packa filen (rekursivt)
				compress(files[i], out);
			}
		} else {
			// Det var ingen katalog utan en fil som ska packas
			// Skapar en buffer för att slippa kopiera tecken för tecken
			byte[] buffer = new byte[4096];
			int bytesRead;

			Log.d("ZIP", "Adding: " + file.getPath() + "... ");
			try {
				// För att läsa från filen
				FileInputStream in = new FileInputStream(file);

				// Skapa en ZipEntry för filen och lägg till den i ZIP-filen
				ZipEntry entry = new ZipEntry(file.getPath());
				out.putNextEntry(entry);

				// överför data
				while ((bytesRead = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytesRead);
				}

				// Stäng strömmar
				out.closeEntry();
				in.close();
			} catch (IOException fel) {
				Log.e("ZIP", "Error\n" + fel.toString());
				return false;
			}
		}
		return true;
	}

	// ******************************************************
	// METOD FÖR ATT PACKA UPP FILER
	// ******************************************************

	// Statisk metod för att packa upp en zip-fil till angiven katalog
	public static boolean decompress(String source, String destination) {
		Log.d("ZIP", "Start decompressing " + source + " to " + destination);
		try {
			// öppnar ZIP-filen för lösning
			ZipFile zipFile = new ZipFile(source);

			// Hämtar alla filer i ZIP-filen
			Enumeration<? extends ZipEntry> e = zipFile.entries();

			// Loopa igenom alla filer
			while (e.hasMoreElements()) {
				// Hämta nästa fil i ZIP-filen
				ZipEntry entry = e.nextElement();

				File destFile = new File(destination, entry.getName());
				Log.d("ZIP", "Extracting: " + destFile + "... ");

				// Skapar eventuella kataloger som behövs
				File destinationParent = destFile.getParentFile();
				if (destinationParent != null)
					destinationParent.mkdirs();

				// Packa upp filen om det inte är en katalog
				if (!entry.isDirectory()) {
					// Skapa en BufferedInputStream för att lösa filen
					BufferedInputStream in = new BufferedInputStream(zipFile
							.getInputStream(entry));

					// Skapar en BufferedOutputStream för att spara filen
					BufferedOutputStream out = new BufferedOutputStream(
							new FileOutputStream(destFile));

					// Skapar en byte-buffer för att slippa kopiera tecken för
					// tecken
					byte[] buffer = new byte[4096];
					int bytesWrite;

					// Kopiera data från zip-fil till destination
					while ((bytesWrite = in.read(buffer)) != -1) {
						out.write(buffer, 0, bytesWrite);
					}

					out.flush();
					out.close();
					in.close();
				}
			}

			zipFile.close();
		} catch (IOException ioe) {
			Log.e("ZIP", "Error decompressing " + source + "\n"
					+ ioe.toString());
			return false;
		}
		Log.d("ZIP", "Done decompressing.");
		return true;
	}
}