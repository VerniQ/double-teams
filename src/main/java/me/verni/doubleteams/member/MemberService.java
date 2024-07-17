package me.verni.doubleteams.member;

import me.verni.doubleteams.configuration.implementation.PluginConfigImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MemberService {
    private final Map<UUID, Member> membersByUniqueId = new HashMap<>();
    private final PluginConfigImpl configuration;
    private final MemberRepository memberRepository;

    public MemberService(PluginConfigImpl configuration, MemberRepository memberRepository) {
        this.configuration = configuration;
        this.memberRepository = memberRepository;
    }

    public Optional<Member> findMember(UUID uuid) {
        return Optional.ofNullable(membersByUniqueId.get(uuid));
    }

    public void create(UUID uuid, String name, String tag) {
        membersByUniqueId.put(uuid, new Member(uuid, name, tag));
    }

    public void saveMember(Member member) {
       this.memberRepository.saveMember(member);
    }


}
