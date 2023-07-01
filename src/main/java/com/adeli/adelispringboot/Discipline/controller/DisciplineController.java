/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.Controller;


//import com.example.demo.entity.Role;
import com.example.demo.entity.Amande;
import com.example.demo.entity.Discipline;
import com.example.demo.entity.Notifications;
import com.example.demo.entity.Retenue;
import com.example.demo.entity.User;
import com.example.demo.message.response.ResponseMessage;
import com.example.demo.entity.Session;
import com.example.demo.repository.AmandeRepository;
import com.example.demo.repository.DisciplineRepository;
import com.example.demo.repository.RetenueRepository;
import com.example.demo.repository.SessionRepository;
import com.example.demo.repository.TontineRepository;
import com.example.demo.repository.UserRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.demo.repository.NotificationsRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 * @author Casimir
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin/discipline")
public class DisciplineController {
           
    @Autowired
    SessionRepository sessionRepository;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    DisciplineRepository disciplineRepository;
    
    @Autowired
    NotificationsRepository notificationsRepository;       
    
    JSONObject json;
    String mts;
    
    @PostMapping("")
    public ResponseEntity<?> createAmande(@RequestBody Discipline discipline, @RequestParam("user") User u) { 
        User user = userRepository.findById(u.getId()).get(); 
        List<Session> sess = sessionRepository.findByEtat(true);
        
        Session session = sess.get(0);
        discipline.setSession(session);
        discipline.setUser(user);        
        
        Notifications notifications = new Notifications();
        if(discipline.getType().equals("Retard")){
            notifications.setDescription(user.getName()+" est venu en retard le "+discipline.getDate());
        }else if(discipline.getType().equals("Absence")){
            notifications.setDescription(user.getName()+" ne s'est pas pointé à la séance du "+discipline.getDate());
        }else{
            notifications.setDescription(user.getName()+" a troublé à la séance du "+discipline.getDate());
        }
        
        notifications.setDate(LocalDate.now());    
        
        notificationsRepository.save(notifications);      
        disciplineRepository.save(discipline);
        return new ResponseEntity<>(new ResponseMessage("discipline mise à jour"), HttpStatus.CREATED);
    }
    
    @PutMapping("")
    public ResponseEntity<?> updateDiscipline(@RequestBody Discipline disc, @RequestParam("user") User u, @RequestParam("id") Long id) { 
        User user = userRepository.findById(u.getId()).get(); 
        List<Session> sess = sessionRepository.findByEtat(true);
        Discipline discipline = disciplineRepository.findById(id).get();
        Session session = sess.get(0);
        discipline.setSession(session);
        discipline.setUser(user);        
        discipline.setSanction(disc.getSanction());        
        discipline.setType(disc.getType());        
        discipline.setDate(disc.getDate());    
        
        System.out.println("sanction: "+discipline.getSanction());
        System.out.println("motif: "+discipline.getType());
        
//        Notifications notifications = new Notifications();
//        if(discipline.getType().equals("Retard")){
//            notifications.setDescription(user.getName()+" est venu en retard le "+discipline.getDate());
//        }else if(discipline.getType().equals("Absence")){
//            notifications.setDescription(user.getName()+" ne s'est pas pointé à la séance du "+discipline.getDate());
//        }else{
//            notifications.setDescription(user.getName()+" a troublé à la séance du "+discipline.getDate());
//        }
//        
//        notifications.setDate(LocalDate.now());    
//        
//        notificationsRepository.save(notifications);      
        disciplineRepository.save(discipline);
        return new ResponseEntity<>(new ResponseMessage("discipline mise à jour"), HttpStatus.CREATED);
    }

    @GetMapping()
    public List<JSONObject> getActiveSessionTontine(){
        return disciplineRepository.findDiscipline();
    }
             
    @GetMapping("/id/{id}")
    public List<JSONObject> getUsers(@PathVariable Long id) {
        User u = userRepository.findById(id).get();
        
        return disciplineRepository.findDisciplineUser(id);
    }
             
    @DeleteMapping("")
    public void deleDepartement(@RequestParam("id") Long id) {
        disciplineRepository.deleteById(id);
    }
}
