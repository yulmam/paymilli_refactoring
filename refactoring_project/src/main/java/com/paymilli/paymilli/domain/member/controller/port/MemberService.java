package com.paymilli.paymilli.domain.member.controller.port;

import com.paymilli.paymilli.domain.member.dto.request.AddMemberRequest;
import com.paymilli.paymilli.domain.member.dto.request.TokenRequest;
import com.paymilli.paymilli.domain.member.dto.request.UpdatePaymentPasswordRequest;
import com.paymilli.paymilli.domain.member.dto.request.ValidatePaymentPasswordRequest;
import com.paymilli.paymilli.domain.member.dto.response.MemberInfoResponse;
import com.paymilli.paymilli.domain.member.dto.response.TokenResponse;
import com.paymilli.paymilli.domain.member.dto.response.ValidatePaymentPasswordResponse;

import java.util.UUID;

public interface MemberService {
    public void addMember(AddMemberRequest addMemberRequest);
    public MemberInfoResponse getMemberInfo(UUID memberId);
    public void logoutMember(UUID memberId);
    public boolean isSameRefreshToken(String refreshToken);
    public TokenResponse issueTokens(TokenRequest tokenRequest);
    public String reissueAccessToken(String refreshToken);
    public void updatePaymentPassword(UUID memberId, String paymentPasswordToken, UpdatePaymentPasswordRequest updatePaymentPasswordRequest);
    public void deleteMember(UUID memberId);
    public ValidatePaymentPasswordResponse validatePaymentPassword(UUID memberId, ValidatePaymentPasswordRequest validatePaymentPasswordRequest);
}
