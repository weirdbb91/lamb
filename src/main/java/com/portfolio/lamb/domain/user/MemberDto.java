package com.portfolio.lamb.domain.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MemberDto {

    private String username;

    private String nickname;

    private String password;

    private String comparePassword;

    private String confirm;

    private String email;
}
