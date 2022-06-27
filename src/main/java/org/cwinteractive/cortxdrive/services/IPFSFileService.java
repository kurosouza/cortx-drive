package org.cwinteractive.cortxdrive.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

@Service
public class IPFSFileService {

	Logger logger = LoggerFactory.getLogger(IPFSFileService.class);
	
	private IPFS ipfs;
	
	@Value("${ipfs.addFileUrl}")
	String ipfsAddFileUrl;
	
	@Value("${ipfs.retrieveFileUrl}")
	String ipfsRetrieveFileUrl;
	
	public IPFSFileService(IPFS ipfs) {
		this.ipfs = ipfs;
	}
	
	public String save(File file) throws Exception {
		// NamedStreamable.FileWrapper fileWrapper = new NamedStreamable.FileWrapper(file);
		// MerkleNode addResult = ipfs.add(fileWrapper).get(0);		
		// return addResult.toJSONString();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", file);
		
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.postForEntity(ipfsAddFileUrl, requestEntity, String.class);
		logger.info(String.format("SaveFileResult: Response Code: %s, Response text: %s", response.getStatusCode(), response.toString()));
		
		return response.toString();
	
	}
	
	public String save(String fileName, InputStream inputStream) throws Exception {
		byte[] fileData = inputStream.readAllBytes(); 
		NamedStreamable.ByteArrayWrapper fileDataWrapper = new NamedStreamable.ByteArrayWrapper(fileName, fileData);		
		MerkleNode addResult = ipfs.add(fileDataWrapper).get(0);		
		return addResult.toJSONString();
	}
	
	public byte[] getFileContents(String cid) throws Exception {
		Multihash filePointer = Multihash.fromBase58(cid);
		byte[] fileContents = ipfs.cat(filePointer);
		return fileContents;
	}
	
	public File download(String cid, String fileName) throws Exception {
		String prefix = fileName.substring(0, fileName.lastIndexOf('.'));
		String suffix = fileName.substring(fileName.lastIndexOf('.'));
		RestTemplate restTemplate = new RestTemplate();
		File downloadedFile = restTemplate.execute(String.format("%s?arg=%s", ipfsRetrieveFileUrl, cid), HttpMethod.GET, null, clientHttpResponse -> {
			File tmpFile = File.createTempFile(prefix, suffix);
			StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(tmpFile));
			return tmpFile;
		});
		
		return downloadedFile;
	}
	
}
