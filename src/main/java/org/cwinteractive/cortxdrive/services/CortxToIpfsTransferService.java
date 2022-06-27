package org.cwinteractive.cortxdrive.services;

import java.io.InputStream;

public class CortxToIpfsTransferService {
	
	private CortxFileService cortxFileService;
	
	private IPFSFileService ipfsFileService;
	
	public CortxToIpfsTransferService(CortxFileService cortxFileService, IPFSFileService ipfsFileService) {
		this.cortxFileService = cortxFileService;
		this.ipfsFileService = ipfsFileService;
	}
	
	public String moveToIPFS(String fileName) throws Exception {
		InputStream cortxObjectStream = cortxFileService.retrieveAsStream(fileName);
		String addResult = ipfsFileService.save(cortxObjectStream);
		return addResult;
	}

}
