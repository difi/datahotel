package no.difi.datahotel.client.wizard;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimetypesFileTypeMap;

import org.apache.myfaces.custom.fileupload.StorageStrategy;
import org.apache.myfaces.custom.fileupload.UploadedFile;

public class UploadedFilePlaceholder implements UploadedFile {

	private File csvFile; 
	
	private static final long serialVersionUID = -5800522649252276188L;
	
	public UploadedFilePlaceholder(File file) {
		csvFile = file;
	}

	public byte[] getBytes() throws IOException {
		InputStream is = new FileInputStream(csvFile);

	    // Get the size of the file
	    long length = csvFile.length();

	    // You cannot create an array using a long type.
	    // It needs to be an int type.
	    // Before converting to an int type, check
	    // to ensure that file is not larger than Integer.MAX_VALUE.
	    if (length > Integer.MAX_VALUE) {
	        // File is too large
	    }

	    // Create the byte array to hold the data
	    byte[] bytes = new byte[(int)length];

	    // Read in the bytes
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length
	           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }

	    // Ensure all the bytes have been read in
	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file "+csvFile.getName());
	    }

	    // Close the input stream and return bytes
	    is.close();
	    return bytes;
	}

	public String getContentType() {
		return new MimetypesFileTypeMap().getContentType(csvFile);
	}

	public InputStream getInputStream() throws IOException {
		InputStream fis = new FileInputStream(csvFile);
		return fis;
	}

	public String getName() {
		return csvFile.getName();
	}

	public long getSize() {
		return csvFile.length();
	}

	@Override
	public StorageStrategy getStorageStrategy() {
		return null;
	}


}