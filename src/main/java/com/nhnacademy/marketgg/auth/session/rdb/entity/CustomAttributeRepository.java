package com.nhnacademy.marketgg.auth.session.rdb.entity;

import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface CustomAttributeRepository {

    List<Attribute> findAttributesBySessionId(String sessionId);

}
