package com.kidd.uber_eat_demo.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kidd.uber_eat_demo.mapper.AddressBookMapper;
import com.kidd.uber_eat_demo.model.entity.AddressBook;
import com.kidd.uber_eat_demo.service.AddressBookService;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
