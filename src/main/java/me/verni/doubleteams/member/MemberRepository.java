package me.verni.doubleteams.member;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MemberRepository {

    List<Member> loadMembers();

    boolean existsMember(Member member);

    CompletableFuture<Void> saveMember(Member member);

    CompletableFuture<Void> removeMember(Member member);

}
