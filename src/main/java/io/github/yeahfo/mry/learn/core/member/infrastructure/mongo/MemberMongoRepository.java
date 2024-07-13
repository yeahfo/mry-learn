package io.github.yeahfo.mry.learn.core.member.infrastructure.mongo;

import io.github.yeahfo.mry.learn.core.member.domain.Member;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberMongoRepository extends MongoRepository< Member, Long > {
}
