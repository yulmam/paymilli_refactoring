package com.paymilli.paymilli.domain.payment.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SearchPaymentGroupResponse {

    private MetaResponse meta;
    private List<TransactionResponse> transactions;
}
