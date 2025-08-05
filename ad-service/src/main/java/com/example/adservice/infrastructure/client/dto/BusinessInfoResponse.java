package com.example.adservice.infrastructure.client.dto;

import java.util.List;

public record BusinessInfoResponse(
        String status_code,
        int request_cnt,
        int valid_cnt,
        List<BusinessData> data
) {
    public record BusinessData(
            String b_no,
            String valid,
            RequestParam request_param,
            BusinessStatus status
    ) {}

    public record RequestParam(
            String b_no,
            String start_dt,
            String p_nm
    ) {}

    public record BusinessStatus(
            String b_no,
            String b_stt,
            String b_stt_cd,
            String tax_type,
            String tax_type_cd,
            String end_dt,
            String utcc_yn,
            String tax_type_change_dt,
            String invoice_apply_dt,
            String rbf_tax_type,
            String rbf_tax_type_cd
    ) {}
}