package org.koreait.message.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.koreait.member.constants.Gender;
import org.koreait.member.controllers.RequestJoin;
import org.koreait.member.entities.Member;
import org.koreait.member.repositories.MemberRepository;
import org.koreait.member.services.MemberUpdateService;
import org.koreait.message.controllers.RequestMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

@SpringBootTest
@ActiveProfiles({"default", "test"})
public class MessageInfoServiceTest {

    @Autowired
    private MessageInfoService infoService;

    @Autowired
    private MessageSendService sendService;

    @Autowired
    private MemberUpdateService updateService;

    @Autowired
    private MemberRepository memberRepository;

    private Member sender;
    private Member receiver;

    @BeforeEach
    void init(){
        for (int i = 1; i<= 2; i++){
            RequestJoin form = new RequestJoin();
            form.setEmail("user0"+i+"@test.org");
            form.setName("휴먼");
            form.setNickName("닉네임");
            form.setZipCode("0000");
            form.setAddress("주소");
            form.setAddressSub("나머지 주소");
            form.setGender(Gender.MALE);
            form.setBirthDt(LocalDate.now());
            form.setPassword("_Aa123456");
            form.setConfirmPassword(form.getPassword());
        }
    }

    @Test
    @WithUserDetails(value = "user01@test.org", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void listTest() {
        createMessages();
    }
    void createMessages() {
        for (int i = 0; i < 10; i++){
            RequestMessage message = new RequestMessage();
            message.setEditorImages("user02@test.org");
        }
    }
}