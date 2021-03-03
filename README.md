[<img src="src\main\resources\static\images\draw.png" width=200>](https://baekpt.site)


# ✍🏻 `진행중` [Lamb](https://github.com/weirdbb91/lamb) 

---
[저장소 링크](https://github.com/weirdbb91/lamb)

외부 API를 활용한 실제로 이용 가능한 서비스

📈 주요 학습 목표
- 🔥 Spring Security를 이용한 보안 기능 개선
- 🔥 Thymeleaf 활용
- ⏱️ 네이버 검색 API 활용
- ✔ 네이버 아이디로 로그인 + 🔥 구글, 깃헙, 페북, 카카오

---
---

# 📝 오늘 할 일

## 🚀 2020년 3월 3일
---
Spring Security 구조에 대해 알면 알수록 해보고 싶은게 많아진다

- ⛔ ~~기존 계정을 소셜 계정과 연동~~
  1. 소셜계정의 권한을 GUEST로 변경
  2. 로그인 성공 지점에서 계정 권한확인
     1. USER 등급의 권한 이상 일 때 : 동작 없음
     2. GUEST 등급의 권한 이하 일 때 : 아이디 패스워드 입력  

문득 그럼 그냥 기존 계정 회원가입과 다름이 없다는 생각이 들었다  
차라리 기존 계정과 소셜 계정을 따로 나누지 말고 통합관리 하는게 더 편할 것  

- 🔥 계정에 활동 닉네임을 추가해서 관리
    1. add (String)nickname on Member Entity
    2. add size and unique validation of nickname column
    3. make a form named "MemberInfoUpdate" that updates Member information
    4. bind the form
    5. check the Member has nickname after authenticated with social login
       1. hasNickname : pass
       2. !hasNickname : locate to "MemberInfoUpdate" form

- ⏱ make the "Board" works
- ⏱ add a basic reply feature
- ⏱ add "Search" form
- ⏱ apply naver search API on "Search" form



