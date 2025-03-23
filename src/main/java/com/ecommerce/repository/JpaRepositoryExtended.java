package com.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JpaRepositoryExtended<T, ID> extends JpaRepository<T, ID> {
    @Override
    <S extends T> S save(S entity);
}
