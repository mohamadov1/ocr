package com.ucf.tessrect.controller;

import com.ucf.tessrect.MrzRecord;
import com.google.gson.Gson;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Controller
public class ImageToTextConvertor {
	
	private String tesserectOcrLocation = "C:\\OCR\\tessdata";
	
	@GetMapping("/s")
	public String index(){
		return "index";
	}
	
	@RequestMapping(value = "/fileupload")
	public @ResponseBody String fileUpload(@RequestParam("file")MultipartFile multipartFile, HttpServletResponse response) throws IOException, TesseractException{

		File file = multipartToFile(multipartFile,"uploaded.png");

		File fileout = ProcessingImage.convertToBlackAndWhite(file, "blackwhitePic");

		BufferedImage ipimage = ImageIO.read(fileout);
		MrzRecord mrzRecord = ProcessingImage.processImg(ipimage);

		return new Gson().toJson(mrzRecord);
	}

	public  static File multipartToFile(MultipartFile multipart, String fileName) throws IllegalStateException, IOException {
		File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+fileName);
		multipart.transferTo(convFile);
		return convFile;
	}
}
