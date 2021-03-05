[<img src="src\main\resources\static\images\draw.png" width=200>](https://baekpt.site)


# ✍🏻 `진행중` [Lamb](https://github.com/weirdbb91/lamb) 

---
[저장소 링크](https://github.com/weirdbb91/lamb)

외부 API를 활용한 실제로 이용 가능한 서비스

📈 주요 학습 목표
- 🔥 Spring Security를 이용한 보안 기능 개선
- 🔥 Thymeleaf 활용
- ⏱️ 네이버 검색 API 활용
- ✔ 네이버 아이디로 로그인 + ✔ 구글, 깃헙, 페북, 카카오

---
---

- 📋 **account**
    - **login**
        - form login
            - bind Thymeleaf form
            - password encrypt
                - put BCryptPasswordEncoder
            - save in database
                - set Member Entity
        - `social login`
            - **naver, kakao**
                - copy CommonOAuth2Provider.GOOGLE
                    - adjust to fit naver
                - override ClientRegistrationRepository
                    - put on configure
                - override OAuth2UserService
                    - put on endpoint
            - **google, github, facebook**
                - use plain CommonOAuth2Provider
                - ⚠️ google doesn't through OAuth2UserService
                    - 🙊 google through OidcUserService
                        - ✔️override OidcUserService
        - `remember me`
            - load Member
                - override UserDetailsService
            - save token
                - use JdbcTokenRepositoryImpl 
                    - set database in

        - override CustomLoginSuccessHandler
            - set on configure
    
    - **logout**
        - clear session, authentication, cookies

    - **update info**
        - bind Thymeleaf form
        - validate member informations
  
    - **change password**
        - bind Thymeleaf form
    
    - **sign out**
        - bind Thymeleaf form
        - delete Member Entity
        - clear session
        - revoke Member Authentication

- 📋 **Board**
    - create
    - update, delete
        - identify origin memberId
    - `display Board list`
        - search
            - JPA Repository
        - pageable
            - JPA Ropository
            - Thymeleaf form set

- 📋📌🔥 **Post**
    - 🔥 create
    - update, delete
        - identify origin memberId
    - `display Post list`
        - search
            - JPA Repository
        - pageable
            - JPA Ropository
            - Thymeleaf form set

- 📋 **Reply**
    - create
    - update, delete
        - identify origin memberId

- 📋 **Naver Search API**


# 📝 오늘 할 일

## 🚀 2020년 3월 5일

---
시간 가는줄 모르겠다

- 🔥 add a basic reply feature
- ⏱ add "Search" form
- ⏱ apply naver search API on "Search" form

---


## 2020년 3월 4일

---
소셜 로그인에 시간을 너무 많이 쏟았다  
깃을 좀 더 활용 하자

- ✔ change password
- ✔ sign out
- ✔ Board

역시 영어가 굉장히 중요한것 같다

- ✔ make the "Board" works
- 🔥 add a basic reply feature
- ⏱ add "Search" form
- ⏱ apply naver search API on "Search" form

---


## 2020년 3월 3일

---
Spring Security 구조에 대해 알면 알수록 해보고 싶은게 많아진다

- ⛔ ~~기존 계정을 소셜 계정과 연동~~
    1. ~~소셜계정의 권한을 GUEST로 변경~~
    2. ~~로그인 성공 지점에서 계정 권한확인~~
        1. ~~USER 등급의 권한 이상 일 때 : 동작 없음~~
        2. ~~GUEST 등급의 권한 이하 일 때 : 아이디 패스워드 입력~~

문득 그럼 그냥 기존 계정 회원가입과 다름이 없다는 생각이 들었다  
차라리 기존 계정과 소셜 계정을 따로 나누지 말고 통합관리 하는게 더 편할 것

- ✔ 계정에 활동 닉네임을 추가해서 관리
    1. ✔ add (String)nickname on Member Entity
    2. ✔ add size and unique validation of nickname column
    3. ✔ make a form named "update" that updates Member information
    4. ✔ bind the form
    5. ⛔ ~~check the Member has nickname after authenticated with social login~~
        1. ~~hasNickname : pass~~
        2. ~~!hasNickname : locate to "MemberInfoUpdate" form~~
    5. ✔ init nickname same value with username value when user sign up

Done today...

- 🔥 make the "Board" works
- ⏱ add a basic reply feature
- ⏱ add "Search" form
- ⏱ apply naver search API on "Search" form

---

