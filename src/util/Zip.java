package util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream.UnicodeExtraFieldPolicy;
import org.apache.commons.io.IOUtils;

public class Zip {

	/**
	 * Create a zip file given an array of {@link File} objects.
	 * <p>
	 * <ul>
	 * <li>language encoding flag: signal that a file name has been encoded using UTF-8</li>
	 * <li>createUnicodeExtraFields: can be used to add an additional UTF-8 encoded file name to the entry's metadata</li>
	 * </ul>
	 * 
	 * Windows "compressed folder" feature doesn't recognize any flag or extra field 
	 * and creates archives using the platforms default encoding 
	 * and expects archives to be in that encoding when reading them.
	 * <p>
	 * The Windows "compressed folder" can't be used if file names are incompatible with the encoding of the target platform
	 * 
	 * @param files an array of {@link File} objects
	 * @return the file zip generated
	 * @throws IOException
	 * 
	 * @see https://commons.apache.org/proper/commons-compress/zip.html
	 */
	public static File doZip(File[] files) throws IOException {
		ZipArchiveOutputStream zos = null;
		InputStream fis = null;
		
		File fzip = File.createTempFile("zip", ".zip");
		FileOutputStream fos = new FileOutputStream(fzip);
		
		try{
			zos = new ZipArchiveOutputStream(fos);
			zos.setEncoding("UTF-8");
			zos.setCreateUnicodeExtraFields( UnicodeExtraFieldPolicy.ALWAYS );
			zos.setLevel( Deflater.BEST_SPEED );
			
			for (int j = 0; j < files.length; j++) {
				File file = files[j];
				if(file.isFile()) {
					// For faster reading you should use BufferedInputStream to wrap any InputStream
					fis = new BufferedInputStream( new FileInputStream(file) );
					ZipArchiveEntry entry = new ZipArchiveEntry( file.getName() );
				
					// begins writing a new zip file and sets the position to the
					// start of data
					zos.putArchiveEntry(entry);
			
					byte[] buf = new byte[1024];
					int len;
					while ((len = fis.read(buf)) > 0) {
						zos.write(buf, 0, len);
					}
					zos.closeArchiveEntry();
					IOUtils.closeQuietly(fis);
				}
			}
			
			zos.finish();
			
		} catch( IOException e ){
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(zos);
			IOUtils.closeQuietly(fis);
		}
		
		return fzip;
	}
	
	/**
	 * Create a zip file given an array of file path strings.
	 * 
	 * @param sfiles an array of file path strings
	 * @return the file zip generated
	 * @throws IOException
	 * 
	 * @see {@link #doZip(File[])}
	 */
	public static File doZip(String[] sfiles) throws IOException {
		File[] files = new File[sfiles.length];
		for (int i = 0; i < sfiles.length; i++) {
			files[i] = new File(sfiles[i]);
		}
		return doZip(files);
	}
	
}
