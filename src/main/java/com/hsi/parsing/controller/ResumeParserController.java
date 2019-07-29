package com.hsi.parsing.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.map.SingletonMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hsi.parsing.Util.ResumeParserConstant;
import com.hsi.parsing.exception.ResumeParsingException;
import com.hsi.parsing.model.CandidateDetails;
import com.hsi.parsing.service.CSVReaderServiceImpl;
import com.hsi.parsing.service.ConvertToMultipleCSVService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/v1/")
public class ResumeParserController {

	@Autowired
	CSVReaderServiceImpl csvReader;

	@Autowired
	ConvertToMultipleCSVService convertToMultipleCSV;
	


	static Logger logger = Logger.getLogger(ResumeParserController.class.getName());

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@PostMapping(value = "/convertToCSV")
	public ResponseEntity<Map> convertToCSVFormat(@RequestParam("File") MultipartFile[] files,
												  HttpServletResponse response) throws IOException {
		try {
			logger.info(ResumeParserConstant.LOG_FOR_ENTRY_FOR_CONVERTCSVFORMAT_METHOD);

			convertToMultipleCSV.convertToCSV(files);
			return new ResponseEntity<>(new SingletonMap("successMessage",ResumeParserConstant.SUCCESS_MSG_FOR_CSV_CREATE), HttpStatus.OK);
		} catch (ResumeParsingException e) {
			return new ResponseEntity(new SingletonMap("errorMessage",e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}



	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GetMapping(value = "/getCandidateDetails")
	public ResponseEntity<List<CandidateDetails>> getCandidateDetailsnew() throws IOException {
		try {
			logger.info(ResumeParserConstant.LOG_FOR_ENTRY_FOR_GETCANDIDATEDETAILS_METHOD);
			List<CandidateDetails> candidateDetailsFromCSVFile = csvReader.getCandidateDetailsFromCSVFile();
			return new ResponseEntity<>(candidateDetailsFromCSVFile, HttpStatus.OK);
		} catch (ResumeParsingException e) {
			return new ResponseEntity(new SingletonMap("errorMessage",e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@GetMapping(value = "/downloadFiles")
	public void download(HttpServletResponse response ) throws IOException 
	{

		    response.setContentType("text/csv");
		    response.setHeader("Content-Disposition", "attachment;filename=candidatedetails.csv");
		    List<String> codeList = new ArrayList<>();
			List<CandidateDetails> candidateDetailsFromCSVFile = csvReader.getCandidateDetailsFromCSVFile();
			for(CandidateDetails list1:candidateDetailsFromCSVFile) {
				codeList.add((list1.getCandidateName()+","+list1.getCandidatePhone()+","+list1.getCandidateEmail()+","+list1.getExperience()+","+list1.getQualification()+","+list1.getSkills()+","+list1.getUploadedFileName()+","+list1.getUploadedDate()));
				
			}
		    ServletOutputStream out = response.getOutputStream();
		    
		    String headers =  "CandidateName,Phone,Email,Experience,Qualification,Skills,FileName,Date"+System.getProperty("line.separator");
		    String codesCommaSeparated = headers + String.join(System.getProperty("line.separator"), codeList);
		    InputStream in = null;
		    
		    try {
		    	
		      in = new ByteArrayInputStream(codesCommaSeparated.getBytes("UTF-8"));
		      out.write(codesCommaSeparated.getBytes(), 0, codesCommaSeparated.length());
		    } 
		    catch (ResumeParsingException e) {
				logger.info("errorMessage");
			}
		    finally {
		        try {
		          in.close();
		          out.flush();
		          out.close();
		        } catch (IOException io) {
		          logger.info("Unable to close the I/O resources due to error: {}");
		        } catch (Exception ex) {
		          logger.info("Unable to close the I/O resources due to error: {}");
		        }
		  }
			

	}
}