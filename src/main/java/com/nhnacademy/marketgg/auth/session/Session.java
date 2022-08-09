package com.nhnacademy.marketgg.auth.session;

// TODO 4: 세션을 다루기 위한 인터페이스
public interface Session {

    String SESSION_ID = "session-id";
    String MEMBER = "member";

    long getCreationTime(); // 세션이 생성된 시간을 January 1 ,1970 GMT 부터 long 형 밀리세컨드 값으로 반환

    String getId(); // 세션 고유 ID

    long getLastAccessedTime();  // 웹 브라우저의 요청이 마지막으로 시도된 시간을 long 형 ms 값으로 반환

    void setMaxInactiveInterval(int var1);  // 세션을 유지할 시간을 초단위로 설정 합니다.

    int getMaxInactiveInterval(); // 세션의 유효시간을 초 단위로 반환 합니다. 기본값은 30초 입니다.

    Object getAttribute(String var1); // get attribute

    void setAttribute(String var1, Object var2); // attribute 설정

    void removeAttribute(String var1); // attribute 삭제

    void invalidate(); // 현재 세션을 종료. 세션관련 모든 값 삭제

    boolean isNew(); // session 이 만들어졌는지 boolean 타입으로 반환 합니다.

}
