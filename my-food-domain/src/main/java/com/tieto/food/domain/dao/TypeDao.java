package com.tieto.food.domain.dao;

import java.util.List;

import com.tieto.food.domain.entity.Type;

public interface TypeDao {
    List<Type> listAll();

    List<String> allToString();

    Type merge(Type type);

    Type findType(Long id);
}
