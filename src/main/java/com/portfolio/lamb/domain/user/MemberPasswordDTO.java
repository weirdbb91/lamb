package com.portfolio.lamb.domain.user;

import lombok.Data;
import lombok.Getter;

@Data
public class MemberPasswordDTO {
    private String password;
    private String newPassword;
    private String confirm;
}
