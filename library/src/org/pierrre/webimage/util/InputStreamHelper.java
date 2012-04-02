package org.pierrre.webimage.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamHelper {
	public static byte[] readFully(InputStream inputStream) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		int byteCount;
		byte[] buffer = new byte[1024];
		
		while ((byteCount = inputStream.read(buffer)) != -1) {
			os.write(buffer, 0, byteCount);
		}
		
		return os.toByteArray();
	}
}
