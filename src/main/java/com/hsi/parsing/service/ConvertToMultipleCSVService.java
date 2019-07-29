package com.hsi.parsing.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Siyona on 6/18/2019.
 */
@Service
public interface ConvertToMultipleCSVService {
	void convertToCSV(MultipartFile[] files) throws IOException;
}

