package com.nhnacademy.marketgg.auth.session.rdb.entity;


import java.util.List;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.nhnacademy.marketgg.auth.session.rdb.entity.QAttribute;

public class AttributeRepositoryImpl extends QuerydslRepositorySupport implements CustomAttributeRepository {


    public AttributeRepositoryImpl() {
        super(Attribute.class);
    }


    @Override
    public List<Attribute> findAttributesBySessionId(String sessionId) {
        QAttribute attribute = QAttribute.attribute;

        return from(attribute)
            .where(attribute.session.id.eq(sessionId))
            .select(attribute)
            .fetch();
    }
}
