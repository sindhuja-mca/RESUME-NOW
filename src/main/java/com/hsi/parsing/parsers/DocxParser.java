package com.hsi.parsing.parsers;

import java.io.IOException;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("docxParser")
public class DocxParser extends AbstractParser {
	@Value("${src_path}")
	private String PATH;
	@Value("${property_value_to_save_csv}")
	private String CSV;
	@Value("${property_value_to_save_failure_csv}")
	private String failureDir;


	@Override
	public String convert(MultipartFile file) throws IOException {

		System.out.println("converting *****");
		try {
			XWPFWordExtractor docxextractor = null;

			XWPFDocument document = new XWPFDocument(file.getResource().getInputStream());
			docxextractor = new XWPFWordExtractor(document);
			String fileText = docxextractor.getText();
			String fileDataHeader = "";
			String[] fileData = fileText.split("\\r?\\n");
			
			docxextractor.close();
			commonFileProcessing(file, fileData, fileDataHeader, PATH, CSV,failureDir, ".docx");
			

		} catch (Exception exep) {
			exep.printStackTrace();
		}
		return "Writing successfull";

	}

}
