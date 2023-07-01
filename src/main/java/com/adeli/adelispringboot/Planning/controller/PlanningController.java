/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.Controller;


//import com.example.demo.entity.Role;
import com.example.demo.entity.Notifications;
import com.example.demo.entity.Planing;
import com.example.demo.entity.Reunion;
import com.example.demo.message.response.ResponseMessage;
import com.example.demo.entity.Session;
import com.example.demo.entity.User;
import com.example.demo.repository.NotificationsRepository;
import com.example.demo.repository.PlanningRepository;
import com.example.demo.repository.ReunionRepository;
import com.example.demo.repository.SessionRepository;
import com.example.demo.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
//import com.example.demo.repository.RoleRepository;
//import com.example.demo.repository.RoleRepository;
//import com.example.demo.repository.UtilisateurRepository;
//import com.example.demo.security.jwt.JwtProvider;

//import com.example.demo.util.RoleEnum;
import java.util.List;
import javax.validation.Valid;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 * @author Casimir
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/planing")
public class PlanningController {
        
    @Autowired
    SessionRepository sessionRepository;
    
    @Autowired
    PlanningRepository planningRepository;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    NotificationsRepository notificationsRepository;  

    @PostMapping("/new")
    public ResponseEntity<?> createSession(@RequestBody Planing planing, @RequestParam("user") User u) { 
        System.err.println("id: "+u.getId());
//        Long id = Long.parseLong(x);
        User user = userRepository.findById(u.getId()).get();  
        List<Session> sess = sessionRepository.findByEtat(true);
        Session session = sess.get(0);
        
//        planing.setDate(planing.getDate());
        planing.setSession(session);
        planing.setEtat(false);
        planing.setEvenement("réunion");
        planing.setUser(user);
        
        if (planing.getDate().isBefore(session.getDebut())){
            return new ResponseEntity<>(new ResponseMessage("Erreur! -> La date ne peut être définié avant la période de session"),
              HttpStatus.BAD_REQUEST);
        }
        
        if (planing.getDate().isAfter(session.getFin())){
            return new ResponseEntity<>(new ResponseMessage("Erreur! -> La date ne peut être définié après la période de session"),
              HttpStatus.BAD_REQUEST);
        }
        
        if (planningRepository.existsByDate(planing.getDate())){
            return new ResponseEntity<>(new ResponseMessage("Erreur! -> Cette date a déjà été enregistré au cours de cette session"),
              HttpStatus.BAD_REQUEST);
        }
        
        if (planningRepository.countBySession(session) == 12){
            return new ResponseEntity<>(new ResponseMessage("Attention! -> Vous avez déjà atteint le nombre maximal d'enregistrement"),
              HttpStatus.BAD_REQUEST);
        }
        
        
        Notifications notifications = new Notifications();
        notifications.setDescription("planning de la réunion mis à jour");
        notifications.setDate(LocalDate.now());    
        
        
        planningRepository.save(planing);
        notificationsRepository.save(notifications); 
      return new ResponseEntity<>(new ResponseMessage("mise à jour planning"),
              HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<?> updateSession(@RequestBody Planing plan, @RequestParam("user") User u, @RequestParam("id") Long id) { 
        System.err.println("id: "+u.getId());
//        Long id = Long.parseLong(x);
        User user = userRepository.findById(u.getId()).get();  
        List<Session> sess = sessionRepository.findByEtat(true);
        Session session = sess.get(0);
        Planing planing = planningRepository.findById(id).get();
//        planing.setDate(planing.getDate());
        planing.setSession(session);
        planing.setEtat(false);
        planing.setEvenement("réunion");
        planing.setUser(user);
        planing.setDate(plan.getDate());
        if (planing.getDate().isBefore(session.getDebut())){
            return new ResponseEntity<>(new ResponseMessage("Erreur! -> La date ne peut être définié avant la période de session"),
              HttpStatus.BAD_REQUEST);
        }
        
        if (planing.getDate().isAfter(session.getFin())){
            return new ResponseEntity<>(new ResponseMessage("Erreur! -> La date ne peut être définié après la période de session"),
              HttpStatus.BAD_REQUEST);
        }
        
        if (planningRepository.existsByDate(planing.getDate()) && planing.getUser().equals(plan.getUser())){
            return new ResponseEntity<>(new ResponseMessage("Erreur! -> Cette date a déjà été enregistré au cours de cette session"),
              HttpStatus.BAD_REQUEST);
        }
        
        if (planningRepository.countBySession(session) == 12){
            return new ResponseEntity<>(new ResponseMessage("Attention! -> Vous avez déjà atteint le nombre maximal d'enregistrement"),
              HttpStatus.BAD_REQUEST);
        }
        
           
        
        
        planningRepository.save(planing);
        Notifications notifications = new Notifications();
        notifications.setDescription("planning de la réunion modifié");
        notifications.setDate(LocalDate.now()); 
        notificationsRepository.save(notifications); 
      return new ResponseEntity<>(new ResponseMessage("planning update"), HttpStatus.OK);
    }

    @GetMapping
    public List<JSONObject> getPlaning(){    
        List<Session> sess = sessionRepository.findByEtat(true);
        List<JSONObject> p ;
        if(!sess.isEmpty()){
            p = planningRepository.findPlaning();
        }else{
            p = new ArrayList<>();
        }
        
        return p;
    }

    @GetMapping("/active")
    public List<Session> getActiveSession(){      
        return sessionRepository.findByEtat(true);
    }
       
    @DeleteMapping("")
    public void deleteplaning(@RequestParam("id") Long id) {
        planningRepository.deleteById(id);
    }
        
}
