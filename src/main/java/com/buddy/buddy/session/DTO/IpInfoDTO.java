package com.buddy.buddy.session.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IpInfoDTO {
    private String country;
    private String countryCode;
    private String city;
    private String isp;
    private String region;
    private String timezone;
    private String latitude;
    private String longitude;
    private String status;
}
