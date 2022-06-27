package org.cwinteractive.cortxdrive.services;

import java.io.File;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

@Service
public class IPFSFileService {

	private IPFS ipfs;
	
	public IPFSFileService(IPFS ipfs) {
		this.ipfs = ipfs;
	}
	
	public String save(File file) throws Exception {
		NamedStreamable.FileWrapper fileWrapper = new NamedStreamable.FileWrapper(file);
		MerkleNode addResult = ipfs.add(fileWrapper).get(0);
		
		return addResult.toJSONString();
	}
	
	public String save(InputStream inputStream) throws Exception {
		NamedStreamable.InputStreamWrapper inputStreamWrapper = new NamedStreamable.InputStreamWrapper(inputStream);
		MerkleNode addResult = ipfs.add(inputStreamWrapper).get(0);
		
		return addResult.toJSONString();
	}
	
	public byte[] getFileContents(String cid) throws Exception {
		Multihash filePointer = Multihash.fromBase58(cid);
		byte[] fileContents = ipfs.cat(filePointer);
		return fileContents;
	}
	
}
