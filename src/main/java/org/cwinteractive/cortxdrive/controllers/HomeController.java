package org.cwinteractive.cortxdrive.controllers;

import java.io.IOException;

import org.cwinteractive.cortxdrive.models.FileInputModel;
import org.cwinteractive.cortxdrive.models.StatusMessage;
import org.cwinteractive.cortxdrive.services.CortxFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

	Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	CortxFileService cortxFileService;

	@GetMapping("/foundation")
	public String foundationHome() {
		return "foundation";
	}

	@GetMapping("/")
	public String start() {
		return "index";
	}

	@GetMapping("/home")
	public String home(FileInputModel fileInputModel, ModelMap modelMap) {
		modelMap.addAttribute("uploadFileData", fileInputModel);
		var cortxFiles = cortxFileService.listFiles();
		modelMap.addAttribute("cortxFiles", cortxFiles);
		return "home";
	}

	@GetMapping("/buckets")
	public String allBuckets(Model model) {
		model.addAttribute("buckets", cortxFileService.getBuckets());
		return "s3tests";
	}

	@PostMapping("/uploadFile")
	public String uploadFile(@ModelAttribute FileInputModel fileInputModel, ModelMap modelMap) throws IOException {
		modelMap.addAttribute("uploadFileData", fileInputModel);
		logger.info("uploading file: " + fileInputModel.getName());
		logger.info("file content type: " + fileInputModel.getFile().getContentType());

		String result = cortxFileService.save(fileInputModel.getFile().getInputStream(),
				fileInputModel.getFile().getOriginalFilename());
		
		logger.info("Upload to CORTX completed: " + result);
		
		modelMap.addAttribute("statusMessage", 
				new StatusMessage("File uploaded", String.format("Your file %s has been successfully saved to CORTX.", fileInputModel.getFile().getOriginalFilename()), 1));

		var cortxFiles = cortxFileService.listFiles();
		modelMap.addAttribute("cortxFiles", cortxFiles);
		
		return "home";
	}

}
