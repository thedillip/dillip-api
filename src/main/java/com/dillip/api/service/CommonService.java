package com.dillip.api.service;

import java.util.List;

import com.dillip.api.dto.PinCodeDTO;
import com.dillip.api.dto.RandomQuoteDTO;
import com.dillip.api.response.BankDetailsResponse;

public interface CommonService {
	RandomQuoteDTO getRandomQuote();
	List<PinCodeDTO> getPinCodeDetails(String pinCode);
	BankDetailsResponse getBankDetailsByIfsc(String ifscCode);
}
