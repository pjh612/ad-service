package com.example.adservice.infrastructure.client;

import com.example.adservice.application.out.ExternalBusinessInfoApiClientPort;
import com.example.adservice.application.out.dto.BusinessInfo;
import com.example.adservice.infrastructure.client.dto.BusinessInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class ExternalBusinessInfoApiClientAdapter implements ExternalBusinessInfoApiClientPort {
    private final RestClient restClient;
    private final String serviceKey;

    public ExternalBusinessInfoApiClientAdapter(RestClient restClient,
                                                @Value("${client.business.serviceKey}") String serviceKey) {
        this.restClient = restClient;
        this.serviceKey = serviceKey;
    }

    @Override
    public BusinessInfo getBusinessInfo(String businessNumber, String representativeName, LocalDate startAt) {
        var requestBody = Map.of(
                "b_no", businessNumber,
                "start_dt", startAt.format(DateTimeFormatter.BASIC_ISO_DATE),
                "p_nm", representativeName
        );


        BusinessInfoResponse response = restClient
                .post()
                .uri(it -> it.path("/validate")
                        .queryParam("serviceKey", serviceKey)
                        .build())

                .body(requestBody)
                .retrieve()
                .body(BusinessInfoResponse.class);

        if (response == null || !"OK".equals(response.status_code())) {
            throw new RuntimeException("사업자 정보 조회에 실패 했습니다.");
        }

        BusinessInfoResponse.BusinessData data = response.data().get(0);
        boolean isValid = "01".equals(data.valid());

        return new BusinessInfo(isValid, data.status().tax_type_cd(), data.status().tax_type());
    }
}
