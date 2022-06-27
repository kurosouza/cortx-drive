package org.cwinteractive.cortxdrive.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.ipfs.api.IPFS;

@Configuration
public class IPFSClientConfiguration {
	
	@Value("${ipfs.nodeUrl}")
	private String ipfsNodeUrl;

	@Bean
	public IPFS getIPFS() {
		IPFS ipfs = new IPFS(ipfsNodeUrl);		
		return ipfs;
	}
}
