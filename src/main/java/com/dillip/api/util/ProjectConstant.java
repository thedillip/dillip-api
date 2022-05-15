package com.dillip.api.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class ProjectConstant {

	// constant variable

	public static final String SUCCESS_MSG = "SUCCESS";
	public static final String ERR_MSG = "ERROR";
	public static final String RESOURCE_NOT_PRESENT = "Resource Not Present in Database";
	public static final String DELETED_MSG = "Resource Deleted";
	public static final String CREATED_MSG = "Resource Created";
	public static final String UPDATED_MSG = "Resource Updated";
	public static final String DATA_FOUND = "Data Found";
	public static final String DATA_NOT_FOUND = "Data not Found";
	public static final String NOT_PRESENT = "NOT PRESENT";

	// constant method

	public static final String formattedDateTime(LocalDateTime date) {
		
		// your local date/time with no timezone information
		LocalDateTime localNow = date;
		// setting UTC as the timezone
		ZonedDateTime zonedUTC = localNow.atZone(ZoneId.of("UTC"));
		// converting to IST
		ZonedDateTime zonedIST = zonedUTC.withZoneSameInstant(ZoneId.of("Asia/Kolkata"));
		
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd MMMM yyyy hh:mm:ss a");
		return zonedIST.format(myFormatObj).toUpperCase();
	}
	
//	public static void main(String[] args) {
//		System.out.println(formattedDateTime(LocalDateTime.now()));
//	}
}
