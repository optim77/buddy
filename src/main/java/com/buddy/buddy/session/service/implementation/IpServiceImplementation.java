package com.buddy.buddy.session.service.implementation;

import com.buddy.buddy.session.DTO.IpInfoDTO;
import com.buddy.buddy.session.service.IpService;
import com.buddy.buddy.subscription.service.implementation.SubscriptionServiceImplementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class IpServiceImplementation implements IpService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(IpServiceImplementation.class.getName());


    @Override
    public IpInfoDTO getIpInfo(String ip) throws IOException {
        logger.info("getIpInfo");
        URL url = new URL("http://ip-api.com/json/" + ip);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                IpInfoDTO ipInfoDTO =  objectMapper.readValue(response.toString(), IpInfoDTO.class);

                if (!"success".equalsIgnoreCase(ipInfoDTO.getStatus())) {
                    throw new IOException("API response status is not success");
                }

                return ipInfoDTO;
            } else {
                throw new IOException("Server returned status: " + con.getResponseCode());
            }
        } finally {
            con.disconnect();
        }
    }
}
