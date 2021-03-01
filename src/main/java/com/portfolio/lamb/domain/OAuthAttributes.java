package com.portfolio.lamb.domain;

import com.portfolio.lamb.domain.user.Member;
import com.portfolio.lamb.domain.user.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 스프링 시큐리티 OAuth 인증을 위한 속성 객체
 * ofNaver, ofKakao 등 of플랫폼명으로 된 메소드들의 리팩토링 여지가 있음.
 */
@Slf4j
@Getter
@ToString
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String username;
    private String email;
    private String picture;
    private String registrationId;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String username, String email, String picture, String registrationId) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.username = username;
        this.email = email;
        this.picture = picture;
        this.registrationId = registrationId;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        log.info("-----------------------------" + registrationId + "----------------------------");
        log.info("userNameAttributeName : " + userNameAttributeName);
        switch (registrationId) {
            case "google":
                return ofGoogle(registrationId, "name", attributes);
            case "github":
                return ofGithub(registrationId, "id", attributes);
            case "facebook":
                return ofFacebook(registrationId, "id", attributes);
            case "naver":
                return ofNaver(registrationId, "name", attributes);
            case "kakao":
                return ofKakao(registrationId, "id", attributes);
            default:
                throw new IllegalArgumentException("해당 로그인을 찾을 수 없습니다.");
        }
    }

    // Google user profile information response template
    /*
    {
        "id": "xx",
        "name": "xx",
        "given_name": "xx",
        "family_name": "xx",
        "link": "xx",
        "picture": "xx",
        "gender": "xx",
        "locale": "xx"
    }
    */
    private static OAuthAttributes ofGoogle(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .username((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .registrationId(registrationId)
                .build();
    }

    // Github user profile information response template
    /*
    {
      "login": "weirdbb91",
      "id": 1,
      "node_id": "MDQ6VXNlcjE=",
      "avatar_url": "https://github.com/images/error/weirdbb91_happy.gif",
      "gravatar_id": "",
      "url": "https://api.github.com/users/weirdbb91",
      "html_url": "https://github.com/weirdbb91",
      "followers_url": "https://api.github.com/users/weirdbb91/followers",
      "following_url": "https://api.github.com/users/weirdbb91/following{/other_user}",
      "gists_url": "https://api.github.com/users/weirdbb91/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/weirdbb91/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/weirdbb91/subscriptions",
      "organizations_url": "https://api.github.com/users/weirdbb91/orgs",
      "repos_url": "https://api.github.com/users/weirdbb91/repos",
      "events_url": "https://api.github.com/users/weirdbb91/events{/privacy}",
      "received_events_url": "https://api.github.com/users/weirdbb91/received_events",
      "type": "User",
      "site_admin": false,
      "name": "SeungHo Baek",
      "company": "GitHub",
      "blog": "https://github.com/blog",
      "location": "San Francisco",
      "email": "weirdbb91@github.com",
      "hireable": false,
      "bio": "There once was...",
      "twitter_username": "none",
      "public_repos": 2,
      "public_gists": 1,
      "followers": 20,
      "following": 0,
      "created_at": "2008-01-14T04:33:35Z",
      "updated_at": "2008-01-14T04:33:35Z",
      "private_gists": 81,
      "total_private_repos": 100,
      "owned_private_repos": 100,
      "disk_usage": 10000,
      "collaborators": 8,
      "two_factor_authentication": true,
      "plan": {
        "name": "Medium",
        "space": 400,
        "private_repos": 20,
        "collaborators": 0
      }
    }
    */
    private static OAuthAttributes ofGithub(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {

        return OAuthAttributes.builder()
                .username(registrationId + attributes.get("id"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("avatar_url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .registrationId(registrationId)
                .build();
    }

    // Facebook user profile information response template
    /*
    {
      "id": "{user-id}",
      "name": "Fiona Fox",
      "birthday": "01/01/1985",
      "email": "fiona@example.com"
      ...
    }
    */
    private static OAuthAttributes ofFacebook(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .username((String) attributes.get("id"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("avatar_url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .registrationId(registrationId)
                .build();
    }

    // Naver user profile information response template
    /*
    {
      "resultcode": "00",
      "message": "success",
      "response": {
        "email": "openapi@naver.com",
        "nickname": "OpenAPI",
        "profile_image": "https://ssl.pstatic.net/static/pwe/address/nodata_33x33.gif",
        "age": "40-49",
        "gender": "F",
        "id": "32742776",
        "name": "오픈 API",
        "birthday": "10-01"
      }
    }
    */
    private static OAuthAttributes ofNaver(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        response.put("name", response.get("id"));

        return OAuthAttributes.builder()
                .username((String) response.get("id"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .registrationId(registrationId)
                .build();
    }


    // Kakao user profile information response template
    /*
    {
      "id":123456789,
      "kakao_account": {
        "profile_needs_agreement": false,
        "profile": {
            "nickname": "Mike",
            "thumbnail_image_url": "http://yyy.kakao.com/.../img_110x110.jpg",
            "profile_image_url": "http://yyy.kakao.com/dn/.../img_640x640.jpg"
        },
        "email_needs_agreement":false,
        "is_email_valid": true,
        "is_email_verified": true,
        "email": "xxxxxxx@xxxxx.com"
        "age_range_needs_agreement":false,
        "age_range":"20~29",
        "birthday_needs_agreement":false,
        "birthday":"1130",
        "gender_needs_agreement":false,
        "gender":"female"
        },
      "properties":{
         "nickname":"MikeKakaoTalk",
         "thumbnail_image":"http://xxx.kakao.co.kr/.../aaa.jpg",
         "profile_image":"http://xxx.kakao.co.kr/.../bbb.jpg",
         "custom_field1":"23",
         "custom_field2":"여"
         ...
      }
    }
    */
    private static OAuthAttributes ofKakao(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        return OAuthAttributes.builder()
                .username((String) properties.get("id"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("profile_image"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .registrationId(registrationId)
                .build();
    }

    //    TODO
    public Member toEntity() {
        return Member.builder().username(username).role(Role.USER).build();
    }
}