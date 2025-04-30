package com.buddy.buddy.session.service;

import com.buddy.buddy.session.DTO.IpInfoDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface IpService {

    IpInfoDTO getIpInfo(String ip) throws IOException;
}
