package com.adeli.adelispringboot.configuration.email.service;

import com.adeli.adelispringboot.configuration.email.dto.EmailDto;

public interface IEmailService {

	void sendEmail(EmailDto emailDto);

}
