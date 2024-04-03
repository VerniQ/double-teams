package me.verni.member;

import java.util.HashMap;
import java.util.UUID;

public class MemberService {
    private final HashMap<UUID, Member> membersByUniqueId = new HashMap<>();
}
