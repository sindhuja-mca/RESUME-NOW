package com.hsi.parsing.parsers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * updated by sindhu & Keerthi
 */

public abstract class AbstractParser {
	private static final String NAME = "^[\\p{L} .'-]+$";
	private static final String SKILLS = "\\b(C|C++|C#|(Java|JAVA)|(JavaScript|Java Script)|J2EE|UI/UX Design|Python|.Net|Perl|Ruby|PHP|Shell scripting|Machine Learning|Artificial Intelligence|Robotics|(IoT|IOT)|Quantum Computing|Data Science|Data Visualization|Networking|Cloud|SOA|Security|Embedded Systems|Android|iOS|Graphic Design|HTML|XML|CSS|Database Management|(MySQL|MySql|SQL)|WordPress|Project Management|Enterprise Systems|Customer Relationship Management|Data Validation|Escalation handling|Report analysis|Salesforce|Oracle|Netsuite|Enterprise Resource Planning|SAP|EAI|Business Continuity Planning|Content Management Systems|Technical Writer|Tech Support|(Testing|testing|TESTING)|Automation|(selenium|Selenium)|(QA|qa)|System Administrator|Unix|Windows|MacOS|Linux|Server)\\b";
	private static final String EXPERIENCE = "\\b(\\d{1,2}\\.{0,1}\\d{0,2}\\+{0,1})\\s{0,1}(years|Years|Yrs|YEARS)\\b?";
	private static final String EMAIL = "[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+";
	private static final String EDUCATION = "\\b(Diploma|(BSc|B.Sc|B.Sc.)|(MSc|M.Sc|M.Sc.)|(BE|B.E|B.E.)|(Bachelor's|Bachelors|Bachelor)|(Engineer|Engineering)|(BTech|B.Tech|B.Tech.|B.TECH|B.TECH.|B Tech)|(Master's|Masters)|(MTech|M.Tech|M.TECH)|M.E|MBA|(MCA|M.C.A|Master of Computer Applications)|(BCA|B.C.A|Bachelors of Computer Applications)|(B.com|B.Com|BCom|Bachelor of Commerce)|(M.Com|Master of Commerce)|(B.A|Bachelor of Arts)|(M.A|Master of Arts)|(PHD|PhD))\\b";
	private static final String PHONE_NUMBER = "(?:(?:\\+|0{0,2})91(\\s*[\\- ]\\s*)?|[0 ]?)?[789]\\d{9}|(\\d[ -]?){18}\\d";

	public abstract String convert(MultipartFile file) throws IOException;

	public void handleName(Set<String> list, String[] fileData, int i) {
		if (list.size() <= 0) {
			String probableName = findPattern(fileData[i], Pattern.compile(NAME, Pattern.CASE_INSENSITIVE));
			if (probableName != null && !"PROFILE".equals(probableName) && probableName.length() < 20) {
				writeToList(list, probableName);

			}
		}
	}

	public void handlePhone(Set<String> list, String[] fileData, int i) {
		
		writeToList(list,findPattern(fileData[i], Pattern.compile(PHONE_NUMBER, Pattern.MULTILINE | Pattern.DOTALL)));
	}

	public void handleMail(Set<String> list, String[] fileData, int i) {
		writeToList(list, findPattern(fileData[i], Pattern.compile(EMAIL, Pattern.MULTILINE)));
	}

	public void handleEducation(Set<String> list, String[] fileData, int i) {
		writeToList(list, findPattern(fileData[i], Pattern.compile(EDUCATION, Pattern.MULTILINE)));
	}

	public void handleExperience(Set<String> list, String[] fileData, int i) {
		if (list.size() <= 0) {
			writeToList(list, findPattern(fileData[i], Pattern.compile(EXPERIENCE, Pattern.CASE_INSENSITIVE)));
		}
	}

	public void handleSkills(Set<String> list, String[] fileData, int i) {
		writeToList(list, findPattern(fileData[i], Pattern.compile(SKILLS, Pattern.CASE_INSENSITIVE)));
	}

	private void writeToList(Set<String> list, String var) {
		if (var != null && !"[]".equals(var) && !"".equals(var) && !" ".equals(var)) {
			list.add(var);
		}
	}

	private static String findPattern(String line, Pattern pattern) {

		Matcher matcher = pattern.matcher(line.replace(" ", ""));
		if (matcher.find()) {
			String group = matcher.group();
			group = group.replaceAll("  ", "");
			group = group.replaceAll("  ", "");
			return group;
		}
		return null;
	}

	protected void commonFileProcessing(MultipartFile file, String[] fileData, String fileDataHeader, String PATH,
			String successDir,String failureDir, String extension) throws IOException {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String fileName = file.getOriginalFilename();


		Set<String> nameS = new HashSet<>();
		Set<String> emailS = new HashSet<>();
		Set<String> phoneS = new HashSet<>();
		Set<String> experienceS = new HashSet<>();
		Set<String> educationS = new HashSet<>();
		Set<String> skillsS = new HashSet<>();

		String[] headerArray = { fileDataHeader };
		for (int i = 0; i < headerArray.length; i++) {
			if (headerArray[i] != null) {
				handleName(nameS, fileData, i);
			}
		}

		for (int i = 0; i < fileData.length; i++) {
			/* extract Name */
			if (fileData[i] != null) {
				/* extract Name */
				handleName(nameS, fileData, i);
				/* extract phone numbers */
				handlePhone(phoneS, fileData, i);
				/* extract email */
				handleMail(emailS, fileData, i);
				/* Years of experience */
				handleExperience(experienceS, fileData, i);
				/* extract education */
				handleEducation(educationS, fileData, i);
				/* extract skills */
				handleSkills(skillsS, fileData, i);

			}

		}

		StringBuilder mainB = new StringBuilder();

		String name = nameS.iterator().next().trim();
		if (StringUtils.isBlank(name) || "CURRICULUM VITAE".equalsIgnoreCase(name) || "RESUME".equalsIgnoreCase(name)) {
			nameS.clear();
			nameS.add(fileName.split("\\.")[0].split("\\[")[0]);
		}

		if (experienceS.isEmpty() || StringUtils.isBlank(experienceS.iterator().next())) {
			experienceS.clear();
			experienceS.add("Not Mentioned!");
		}
		
		writeToSB(nameS, mainB);
		writeToSB(phoneS, mainB);
		writeToSB(emailS, mainB);
		writeToSB(skillsS, mainB);
		writeToSB(educationS, mainB);
		writeToSB(experienceS, mainB);
		writeToSB(Collections.singleton(fileName), mainB);
		writeToSB(Collections.singleton(sdf.format(date)), mainB);
		mainB.deleteCharAt(mainB.length() - 1);

		String subDirectory = successDir;
		if(nameS.isEmpty()||phoneS.isEmpty()||emailS.isEmpty()||skillsS.isEmpty()||educationS.isEmpty()||experienceS.isEmpty()) {
			subDirectory = failureDir;
		}
		
		FileWriter fw = getFileWriter(PATH, subDirectory , extension, fileName);

		
		fw.write(mainB.toString());

		fw.close();
	}

	private FileWriter getFileWriter(String PATH, String subdirectory, String extension, String fileName) throws IOException {
		FileWriter fw;
		String csvfileName = fileName.replace(extension, ".csv");
		String pathname = PATH + "//" + subdirectory;
		File csvPath = new File(pathname);
		if (csvPath.exists() && csvPath.isDirectory()) {
			System.out.println("Directory exists");
		} else {
			csvPath.mkdirs();
		}

		File csvFile = new File(pathname + "//" + csvfileName);

		if (csvFile.exists()) {
			fw = new FileWriter(csvFile, true);
		} else {
			csvFile.createNewFile();
			fw = new FileWriter(csvFile);
		}
		return fw;
	}

	private void writeToSB(Set<String> nameS, StringBuilder mainB) {

		for (String str : nameS) {
			mainB.append(str.replace(",", "|")).append("|");
		}

		if (nameS.size() > 0) {
			mainB.deleteCharAt(mainB.length() - 1);
		}

		mainB.append(",");
	}

	
}
