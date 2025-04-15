package com.paymilli.paymilli.domain.member.domain;

import com.paymilli.paymilli.domain.member.infrastructure.entity.Gender;
import com.paymilli.paymilli.domain.member.infrastructure.entity.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Getter
@Builder
public final class MemberProfile {

    private final String name;
    private final LocalDate birthday;
    private final Gender gender;
    private final Role role;
    private final String email;
    private final String phone;


    public MemberProfile(String name, String birthday, Gender gender, Role role, String email, String phone) {
        validateName(name);
        validateEmail(email);
        validatePhone(phone);
        this.name = name;
        this.birthday = changeFormat(birthday);
        this.gender = gender;
        this.role = role;
        this.email = email;
        this.phone = phone;
    }


    @Builder
    public MemberProfile(String name, LocalDate birthday, Gender gender, Role role, String email, String phone) {
        validateName(name);
        validateEmail(email);
        validatePhone(phone);
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.role = role;
        this.email = email;
        this.phone = phone;
    }

    private LocalDate changeFormat(String birthday){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return LocalDate.parse(birthday, formatter);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 필수 값입니다.");
        }
    }

    private void validateEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (email == null || !Pattern.matches(emailRegex, email)) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }
    }

    private void validatePhone(String phone) {
        String phoneRegex = "^\\d{10,15}$";  // 10~15자리 숫자
        if (phone == null || !Pattern.matches(phoneRegex, phone)) {
            throw new IllegalArgumentException("유효하지 않은 전화번호입니다.");
        }
    }
}
