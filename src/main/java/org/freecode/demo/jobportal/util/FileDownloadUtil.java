package org.freecode.demo.jobportal.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.plaf.UIResource;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

public class FileDownloadUtil {

	private Path foundFile;
	public Resource getFileAsResource(String downloadDir, String fileName) throws IOException {
		
		Path path = Paths.get(downloadDir);
		Files.list(path).forEach(file -> {
			if (file.getFileName().toString().startsWith(fileName)) {
				foundFile = file;
			}
		});
		
		if (foundFile != null) {
			return new UrlResource(foundFile.toUri());
		}
		
		return null;
	}
}
