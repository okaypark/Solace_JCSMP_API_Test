package me.park.config;

import com.solacesystems.jcsmp.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Solace Events Broker접속을 위한 세션관리 (JSCMP) : 여러 API 별로 세션관리 가능
@Configuration
public class SolaceConfig {

    @Value("${solace.host}")
    private String solaceHost;

    @Value("${solace.msgVpn}")
    private String msgVpn;

    @Value("${solace.clientUsername}")
    private String solaceUsername;

    @Value("${solace.clientPassword}")
    private String solacePassword;

    private JCSMPProperties properties;

    // JSCMP 세션생성 (connect는 Service에서 관리)
    // @Bean에 등록을 해서 의존성 주입 : 다른 클래스 객체에서 생성자를 통해 의존성 주입하면 모든 클래스 공통사용가능(JSCMPSession)
    @Bean
    public JCSMPSession initJSCMPSession() throws JCSMPException {

        properties = new JCSMPProperties();

        // 호스트, 사용자 정보, 비밀번호 및 VPN 이름 설정
        properties.setProperty(JCSMPProperties.HOST, solaceHost); // Host:Port
        properties.setProperty(JCSMPProperties.USERNAME, solaceUsername); // Username
        properties.setProperty(JCSMPProperties.PASSWORD, solacePassword); // Password
        properties.setProperty(JCSMPProperties.VPN_NAME, msgVpn); // VPN Name

        // 중복 구독시 무시하고 진행
        properties.setProperty(JCSMPProperties.IGNORE_DUPLICATE_SUBSCRIPTION_ERROR, true);

        // JCSMPSession 생성만 담당. 세션 반환해서 외부에서 연결 및 통신 처리(Service 파트)
        return JCSMPFactory.onlyInstance().createSession(properties);
    }

    public void setIgnoreDuplicateSubscriptionError() {
        properties.setProperty(JCSMPProperties.IGNORE_DUPLICATE_SUBSCRIPTION_ERROR, true);
    }


}