package com.dillip.api.controller;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.dillip.api.dto.PinCodeDTO;
import com.dillip.api.dto.RandomQuoteDTO;
import com.dillip.api.response.ApiEntity;
import com.dillip.api.response.ApiResponseObject;
import com.dillip.api.response.BankDetailsResponse;
import com.dillip.api.service.CommonService;
import com.dillip.api.util.ProjectConstant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@CrossOrigin
public class CommonController {
	
	private final static Logger LOGGER = Logger.getLogger("Dillip Logger");
	
	@Autowired
	private CommonService commonService;
	
	@Operation(summary = "fetchRandomQuote")
	@GetMapping(path = "/quote")
	public ResponseEntity<ApiResponseObject> getRandomQuote()
	{	
		
		HttpStatus status = null;
		HttpHeaders httpHeaders = new HttpHeaders();
		String message = null;
		RandomQuoteDTO response = null;
		LOGGER.log(Level.INFO, "############# Hitting getRandomQuote() in Controller Layer ###############");
		try {
			response = commonService.getRandomQuote();
			if(response != null)
			{
				message = ProjectConstant.DATA_FOUND;
				status = HttpStatus.OK;
			}
			else
			{
				message = ProjectConstant.DATA_NOT_FOUND;
				status = HttpStatus.NOT_FOUND;
			}
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "############# Exception Occured ##########", e);
			message = e.getMessage();
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		
		return new ResponseEntity<>(new ApiEntity<RandomQuoteDTO>(message, response), httpHeaders, status);
	}
	
	@Operation(summary = "getPostOfficeDetailsByPinCode")
	@GetMapping(path = "/postoffice-details/{pin_code}")
	public ResponseEntity<ApiResponseObject> getPostOfficeDetailsByPinCode(
			 @Parameter(description = "String",required = true) @PathVariable(name = "pin_code",required = true) String pinCode
			)
	{	
		
		HttpStatus status = null;
		HttpHeaders httpHeaders = new HttpHeaders();
		String message = null;
		List<PinCodeDTO> response = null;
		LOGGER.log(Level.INFO, "############# Hitting getPostOfficeDetailsByPinCode() in Controller Layer ###############");
		try {
			response = commonService.getPinCodeDetails(pinCode);
			if(!response.isEmpty() && response != null)
			{
				message = ProjectConstant.DATA_FOUND;
				status = HttpStatus.OK;
			}
			else
			{
				message = ProjectConstant.DATA_NOT_FOUND;
				status = HttpStatus.NOT_FOUND;
			}
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "############# Exception Occured ##########", e);
			message = e.getMessage();
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		
		return new ResponseEntity<>(new ApiEntity<List<PinCodeDTO>>(message, response), httpHeaders, status);
	}
	
	@Operation(summary = "getPostOfficeDetailsByPinCode")
	@GetMapping(path = "/bank-details/{ifsc_code}")
	public ResponseEntity<ApiResponseObject> getBankDetailsByIfsc(
			 @Parameter(description = "String",required = true) @PathVariable(name = "ifsc_code",required = true) String ifscCode
			)
	{	
		
		HttpStatus status = null;
		HttpHeaders httpHeaders = new HttpHeaders();
		String message = null;
		BankDetailsResponse response = null;
		LOGGER.log(Level.INFO, "############# Hitting getPostOfficeDetailsByPinCode() in Controller Layer ###############");
		try {
			response = commonService.getBankDetailsByIfsc(ifscCode);
			if(response != null)
			{
				message = ProjectConstant.DATA_FOUND;
				status = HttpStatus.OK;
			}
			else
			{
				message = ProjectConstant.DATA_NOT_FOUND;
				status = HttpStatus.NOT_FOUND;
			}
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "############# Exception Occured ##########", e);
			message = e.getMessage();
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		
		return new ResponseEntity<>(new ApiEntity<BankDetailsResponse>(message, response), httpHeaders, status);
	}
}
