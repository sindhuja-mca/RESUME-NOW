package com.hsi.parsing.parsers;

import java.io.IOException;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hsi.parsing.exception.ResumeParsingException;

@Service("docParser")
public class DocParser extends AbstractParser {
	@Value("${src_path}")
	private String PATH;

	@Value("${property_value_to_save_csv}")
	private String successfullyProcessedDir;

	@Value("${property_value_to_save_failure_csv}")
	private String failureDir;
	
	@Override
	public String convert(MultipartFile file) throws IOException {

		System.out.println("converting *****");

		try {
			WordExtractor extractor = null;
			HWPFDocument document = new HWPFDocument(file.getResource().getInputStream());
			extractor = new WordExtractor(document);
			String[] fileData = extractor.getParagraphText();
			String fileDataHeader = extractor.getHeaderText();
			
			extractor.close();
			
			commonFileProcessing(file, fileData, fileDataHeader, PATH, successfullyProcessedDir,failureDir, ".doc");
			

		} catch (Exception exep) {
			exep.printStackTrace();
			throw new ResumeParsingException(exep.getMessage());
		}
		return "Writing successfull";
	}

}
