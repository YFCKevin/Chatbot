package com.yfckevin.chatbot.service;


import com.yfckevin.chatbot.entity.Member;

import java.util.Optional;

public interface MemberService {
    Optional<Member> findByEmail(String email);

    Member save(Member member);

    Optional<Member> findByUserId(String userId);
}
