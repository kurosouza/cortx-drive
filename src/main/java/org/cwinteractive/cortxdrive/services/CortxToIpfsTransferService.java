package org.cwinteractive.cortxdrive.services;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CortxToIpfsTransferService {
	
	Logger logger = LoggerFactory.getLogger(CortxToIpfsTransferService.class);
	
	private CortxFileService cortxFileService;
	
	private IPFSFileService ipfsFileService;
	
	public CortxToIpfsTransferService(CortxFileService cortxFileService, IPFSFileService ipfsFileService) {
		this.cortxFileService = cortxFileService;
		this.ipfsFileService = ipfsFileService;
	}
	
	public String moveToIPFS(String fileName) throws Exception {
		File cortxObjectFile = cortxFileService.retrieve(fileName);
		logger.info(String.format("Retrieved cortx object: %s, size: %s", cortxObjectFile.getName(), cortxObjectFile.length()));
		String addResult = ipfsFileService.save(cortxObjectFile);
		return addResult;
	}

}
