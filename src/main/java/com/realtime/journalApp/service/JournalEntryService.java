package com.realtime.journalApp.service;

import com.realtime.journalApp.controller.JournalEntryController;
import com.realtime.journalApp.entity.JournalEntry;
import com.realtime.journalApp.entity.User;
import com.realtime.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//@Component
@Service
@Slf4j
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    String uN ;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String  userName){
        try {
            User user = userService.findByUserName(userName);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry save = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(save);
            userService.saveUser(user);
        } catch(Exception e){
            log.info("Error occurred for {}", uN, e);
            throw new RuntimeException("An error occurred while saving JournalEntry", e);
        }
    }

    public void saveEntry(JournalEntry journalEntry) {
        journalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> findById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deleteById(ObjectId id, String userName) {
        boolean removed = false;
        try {
            User user = userService.findByUserName(userName);
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
            if(removed){
                journalEntryRepository.deleteById(id);
                userService.saveUser(user);
            }
        } catch(Exception e){
            log.error("Error", e);
            throw new RuntimeException("An error occurred while deleting JournalEntry", e);
        }
        return removed;
    }

//    public List<JournalEntry> findByUserName(String userName) {
//
//    }

}
