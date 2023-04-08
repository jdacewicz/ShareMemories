package com.sharememories.sharememories.service;

import com.sharememories.sharememories.domain.Contact;
import com.sharememories.sharememories.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContactService {

    private ContactRepository repository;

    @Autowired
    public ContactService(ContactRepository repository) {
        this.repository = repository;
    }

    public Optional<Contact> getContactById(long id) {
        return repository.findById(id);
    }
}
