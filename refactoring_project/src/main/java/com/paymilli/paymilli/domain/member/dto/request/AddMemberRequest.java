package com.paymilli.paymilli.domain.member.dto.request;

import com.paymilli.paymilli.domain.member.infrastructure.entity.Gender;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class AddMemberRequest {

    @Size(min = 4, max = 20)
    private String loginId;
    private String name;
    @Size(min = 8, max = 13)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?`~]+$")
    private String password;
    private String birthday;
    private Gender gender;
    private String phone;
    private String paymentPassword;

    public void setPassword(String password) {
        this.password = password;
    }
}
