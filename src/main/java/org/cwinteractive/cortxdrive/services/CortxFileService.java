package org.cwinteractive.cortxdrive.services;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

@Service
public class CortxFileService {
	
	Logger logger = LoggerFactory.getLogger(CortxFileService.class);
	
	@Value("${cortx.defaultBucketName}")
	protected String defaultBucketName;
	
	private AmazonS3 cortxS3client;
	
	public CortxFileService(AmazonS3 cortxS3client) {
		this.cortxS3client = cortxS3client;
	}
	
	public List<String> getBuckets() {		
		return cortxS3client.listBuckets().stream().map(b -> b.getName()).collect(Collectors.toList());
	}
	
	public void save(File file, Map metadata) {
		cortxS3client.putObject(defaultBucketName, file.getName(), file);
	}
	
	public String save(InputStream inputStream, String fileName) {
		logger.info(String.format("Uploading %s to bucket %s", fileName, defaultBucketName));
		PutObjectResult result = cortxS3client.putObject(defaultBucketName, fileName, inputStream, null);
		return result.toString();
	}
	
	public File retrieve(String fileName) throws Exception {
		S3Object s3object = cortxS3client.getObject(defaultBucketName, fileName);
		S3ObjectInputStream inputStream = s3object.getObjectContent();
		File tempFile = File.createTempFile(fileName, null);
		Files.copy(inputStream, Paths.get(tempFile.getAbsolutePath()));
		return tempFile;
	}
	
	public InputStream retrieveAsStream(String fileName) throws Exception {
		S3Object s3object = cortxS3client.getObject(defaultBucketName, fileName);
		S3ObjectInputStream inputStream = s3object.getObjectContent();
		
		return inputStream;
	}
	
	public List<String> listFiles(String bucketName) {
		ObjectListing objectListing = cortxS3client.listObjects(defaultBucketName);
		
		return objectListing.getObjectSummaries().stream().map(obj -> obj.getKey()).collect(Collectors.toList());
	}
	
	public List<String> listFiles() {
		return listFiles(defaultBucketName);
	}
	
}
