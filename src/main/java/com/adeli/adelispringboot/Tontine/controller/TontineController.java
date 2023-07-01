/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.Controller;


//import com.example.demo.entity.Role;
import com.example.demo.entity.Elections;
import com.example.demo.entity.Notifications;
import com.example.demo.entity.Retenue;
import com.example.demo.entity.User;
import com.example.demo.message.response.ResponseMessage;
import com.example.demo.entity.Session;
import com.example.demo.entity.Tontine;
import com.example.demo.repository.ElectionRepository;
import com.example.demo.repository.NotificationsRepository;
import com.example.demo.repository.PlanningRepository;
import com.example.demo.repository.RetenueRepository;
import com.example.demo.repository.ReunionRepository;
import com.example.demo.repository.SessionRepository;
import com.example.demo.repository.TontineRepository;
import com.example.demo.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
//import com.example.demo.repository.RoleRepository;
//import com.example.demo.repository.RoleRepository;
//import com.example.demo.repository.UtilisateurRepository;
//import com.example.demo.security.jwt.JwtProvider;

//import com.example.demo.util.RoleEnum;
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
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 * @author Casimir
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin/tontine")
public class TontineController {
    
    @Autowired
    ReunionRepository reunionRepository;
    
    @Autowired
    TontineRepository tontineRepository;
    
    @Autowired
    SessionRepository sessionRepository;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    RetenueRepository retenueRepository;
    
    @Autowired
    PlanningRepository planningRepository;
    
    @Autowired
    NotificationsRepository notificationsRepository;
    
    @Autowired
    ElectionRepository electionRepository;
    
    JSONObject json;
    String mts;

    @PostMapping()
    public ResponseEntity<?> createTontine(@RequestParam("user") Long id) { 
        User user = userRepository.findById(id).get();  
        Tontine tontine = new Tontine();
        Retenue retenue = new Retenue();
        System.out.println("user: "+ user.getName());
        
        List<Session> sess = sessionRepository.findByEtat(true);
        Session session = sess.get(0);
        System.out.println("session: "+ session.getIdSession());
        Elections e = electionRepository.findByUserAndSession(user.getName(), session);
//        Tontine tontine = new Tontine();
        
        double montant = session.getMontant();
        double mangwa = session.getRetenue();
        System.out.println("session montant: "+ montant);
        System.out.println("montant: "+ tontine.getDebit());
//        if (montant > tontine.getDebit()){
//            return new ResponseEntity<>(new ResponseMessage("Erreur! -> Le montant de la cotisation n'est pas correct"),
//              HttpStatus.BAD_REQUEST);
//        }   
        Tontine ton = tontineRepository.findFirstByOrderByIdTontineDesc();
//        System.out.println("last: "+ ton.getIdTontine());
        double solde = 0;
        if(ton != null){
            solde = ton.getMontant() + e.getMontant();
            tontine.setMontant(solde);
        }else{
            solde = e.getMontant();
            tontine.setMontant(solde);
        }
//        double solde = ton.getMontant() + tontine.getDebit() - mangwa;
        double debit = e.getMontant();
        
        tontine.setDebit(debit);
        tontine.setCredit(0);
        tontine.setMotif("cotisation "+user.getName());
        tontine.setUser(user);
        if (!planningRepository.existsByDate(LocalDate.now())){
            return new ResponseEntity<>(new ResponseMessage("Erreur! -> Ce jour n'est pas inscrit dans le planning de réunion"),
              HttpStatus.BAD_REQUEST);
        }
        tontine.setDate(LocalDate.now()); 
        tontine.setSession(session);
//        session.setReunion(reunion);
        System.out.println("session: "+ session.getDebut());
        if(tontineRepository.existsByDateAndUser(tontine.getDate(), tontine.getUser())){
            return new ResponseEntity<>(new ResponseMessage("Attention! -> l'utilisateur "+user.getName()+" a déjà cotisé"),
              HttpStatus.BAD_REQUEST);
        }  
                
        tontineRepository.save(tontine);
        
        Retenue rete = retenueRepository.findFirstByOrderByIdRetenueDesc();
//        double ret = tontine.getDebit() - session.getRetenue();
        double solde2 = 0;
        if(rete != null){
            solde2 = rete.getSolde() + mangwa;
            retenue.setSolde(solde2);
        }else{
            solde2 = mangwa;
            retenue.setSolde(solde2);
        }
        retenue.setDebit(mangwa);
        retenue.setCredit(0);
        retenue.setDate(LocalDate.now());
        retenue.setUser(user);
        retenue.setMotif("cotisation");
        
        
        System.out.println("mangwa: "+ solde2);
        retenueRepository.save(retenue);
        
        Notifications notifications = new Notifications(
                user.getName()+" a cotisé "+ e.getMontant()+" €",
                LocalDate.now());
        notificationsRepository.save(notifications);
        
      return new ResponseEntity<>(new ResponseMessage("Tontine enregistrée"), HttpStatus.CREATED);
    }

    @GetMapping
    public List<Tontine> getTontine(){      
        return tontineRepository.findAll();
    }

    @GetMapping("/solde")
    public JSONObject soldeTontine(){      
        Tontine tontine = tontineRepository.findFirstByOrderByIdTontineDesc();
        Map<String, Object> response = new HashMap<>();
        JSONObject solde;
        if(tontine != null){
            response.put("solde", tontine.getMontant());
        }else{
            response.put("solde", 0);
        }        
        solde = new JSONObject(response);
        return solde;
    }

    @GetMapping("/lastMonth")
    public List<JSONObject> getLastMonthTontine(){  
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(0);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        if((month + 1)< 10){
            if(month == 0){
                mts = String.valueOf(year - 1)+"/12";
            }else{
                mts = String.valueOf(year)+"/0"+ String.valueOf(month);
            }                
        }else{
            if((month+1) == 10){
                mts = String.valueOf(year)+"/09";
            }else{
                mts = String.valueOf(year)+"/"+ String.valueOf(month);
            }
        }
        return tontineRepository.Tontine(mts);
    }

    @GetMapping("/thisMonth")
    public List<JSONObject> getThisMonthTontine(){  
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(0);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        if(month+1 < 10){
            mts = String.valueOf(year)+"/0"+ String.valueOf(month+1);
            System.out.println("ce mois: "+ mts);
        }else{
            mts = String.valueOf(year)+"/"+ String.valueOf(month+1);
        }
        return tontineRepository.Tontine(mts);
    }

    @GetMapping("/session/{id}")
    public List<JSONObject> getSessionTontine(@PathVariable Long id){ 
        return tontineRepository.TontineSession(id);
    }

    @GetMapping("/session")
    public List<JSONObject> getActiveSessionTontine(){ 
        List<Session> sess = sessionRepository.findByEtat(true);
        List<JSONObject> p ;
        Session session = new Session();
        if(!sess.isEmpty()){
            session = sess.get(0); 
            p = tontineRepository.TontineSession(session.getIdSession());
        }else{
            p = new ArrayList<>();
        }
        return p ;
    }
    
    
           
    @GetMapping("/id/{id}")
    public List<JSONObject> getUsers(@PathVariable Long id) {
        User u = userRepository.findById(id).get();
        
        return tontineRepository.TontineUser(id);
    }
        
}
