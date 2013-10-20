package com.tieto.food.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tieto.food.domain.dao.TypeDao;
import com.tieto.food.domain.entity.Type;

@Service
public class TypeService {

    @Autowired
    private TypeDao typeDao;

    @Transactional
    public Type merge(Type type) {
        return typeDao.merge(type);
    }

    @Transactional(readOnly = true)
    public List<Type> listAll() {
        return typeDao.listAll();
    }

    @Transactional(readOnly = true)
    public List<String> listAllToString() {
        return typeDao.allToString();
    }

    @Transactional
    public Type findType(Long id) {
        return typeDao.findType(id);
    }
}