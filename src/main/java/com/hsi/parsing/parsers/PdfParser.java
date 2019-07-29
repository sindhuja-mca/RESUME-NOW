package com.hsi.parsing.parsers;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service("pdfParser")
public class PdfParser extends AbstractParser {

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

			PDDocument document = PDDocument.load(file.getResource().getInputStream());
			PDFTextStripper pdfStripper = new PDFTextStripper();
			String text = pdfStripper.getText(document);
			String fileDataHeader = "";

			String[] fileData = text.split("\\r?\\n");
			document.close();

			commonFileProcessing(file, fileData, fileDataHeader, PATH, CSV, failureDir, ".pdf");

		} catch (Exception exep) {
			exep.printStackTrace();
		}
		return "Writing successfull";

	}

}
