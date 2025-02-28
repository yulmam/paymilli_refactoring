package com.paymilli.paymilli.domain.payment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MetaResponse {

    private long total_count;
    private int pagable_count;
}
