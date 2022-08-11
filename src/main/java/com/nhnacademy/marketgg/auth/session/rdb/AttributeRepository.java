package com.nhnacademy.marketgg.auth.session.rdb;

import com.nhnacademy.marketgg.auth.session.rdb.entity.Attribute;
import com.nhnacademy.marketgg.auth.session.rdb.entity.CustomAttributeRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Profile("rdb")
@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Attribute.Id>, CustomAttributeRepository {
}
