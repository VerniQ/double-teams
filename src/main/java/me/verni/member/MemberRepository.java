package me.verni.member;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MemberRepository {

    List<Member> loadMembers();

    CompletableFuture<Void> saveMember(Member member);

    CompletableFuture<Void> removeMember(Member member);

}
