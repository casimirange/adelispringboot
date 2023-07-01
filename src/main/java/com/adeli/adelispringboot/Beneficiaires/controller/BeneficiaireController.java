/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.Controller;


//import com.example.demo.entity.Role;
import com.example.demo.entity.Beneficiaire;
import com.example.demo.entity.Notifications;
import com.example.demo.entity.User;
import com.example.demo.message.response.ResponseMessage;
import com.example.demo.entity.Session;
import com.example.demo.entity.Tontine;
import com.example.demo.repository.BeneficiaireRepository;
import com.example.demo.repository.NotificationsRepository;
import com.example.demo.repository.ReunionRepository;
import com.example.demo.repository.SessionRepository;
import com.example.demo.repository.TontineRepository;
import com.example.demo.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 * @author Casimir
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin/beneficiaire")
public class BeneficiaireController {
    
    @Autowired
    ReunionRepository reunionRepository;
    
    @Autowired
    TontineRepository tontineRepository;
    
    @Autowired
    SessionRepository sessionRepository;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    BeneficiaireRepository beneficiaireRepository;
    
    @Autowired
    NotificationsRepository notificationsRepository;        
    
    JSONObject json;
    String mts;

//    @PostMapping("/{id}")
//    public ResponseEntity<?> createTontine(@RequestParam("id") Long id, @RequestBody Beneficiaire beneficiaire) { 
//        Tontine tontine = new Tontine();
////        Beneficiaire beneficiaire = new Beneficiaire();
//        User user = userRepository.findById(id).get();
//        System.out.println("user: "+ user.getName());
//        List<Session> sess = sessionRepository.findByEtat(true);
//        Session session = sess.get(0);
//        System.out.println("session: "+ session.getIdSession());
//        
//        double montant = session.getMontant();
//        double mangwa = session.getRetenue();
//        System.out.println("session montant: "+ montant);
//        System.out.println("montant: "+ tontine.getDebit());
//        Beneficiaire b = beneficiaireRepository.findFirstByOrderByIdBeneficiaireDesc();
////        if(b.getNom().equals("")){
////        System.out.println("last benef: "+ b.getNom());
////        }else {
////            System.out.println("tu es mort");
////        }
//        if(beneficiaireRepository.existsBySessionAndNom(session, user.getName())){
//            return new ResponseEntity<>(new ResponseMessage("Attention! -> l'utilisateur "+user.getName()+" a déjà bouffé cette session"),
//              HttpStatus.BAD_REQUEST);
//        }
//        double bouff = (montant) * session.getParticipants();
//        System.out.println("nombre d'utilisateur: "+bouff);
//                
//        Tontine ton = tontineRepository.findFirstByOrderByIdTontineDesc();
//        System.out.println("last: "+ ton.getIdTontine());
//        double credits = (bouff * 3)/4;
//        if(!ton.equals(null) && ton.getMontant() >= bouff ){
//            double credit = (bouff *3)/4;
//            System.out.println("credit: "+ credit);
//            tontine.setCredit(credit);
//            double solde = ton.getMontant() - credit;
//            tontine.setMontant(solde);
//            beneficiaire.setMontant(credit);
//        }else{
//            return new ResponseEntity<>(new ResponseMessage("Erreur! -> montant isuffisant pour bouffer"), 
//                    HttpStatus.BAD_REQUEST);
//        }
//        
//        tontine.setDebit(0);
//        
//        tontine.setMotif(user.getName()+": a bouffé");
//        tontine.setUser(user);
//        tontine.setDate(LocalDate.now()); 
//        tontine.setSession(session);
////        session.setReunion(reunion);
//        System.out.println("session: "+ session.getDebut());
//        System.out.println("count date: "+ tontineRepository.countByDate(LocalDate.now()));
//        System.out.println("part: "+ session.getParticipants());
////        if(tontineRepository.existsByDateAndUser(tontine.getDate(), tontine.getUser())){
////            return new ResponseEntity<>(new ResponseMessage("Attention! -> l'utilisateur "+user.getName()+" a déjà cotisé"),
////              HttpStatus.BAD_REQUEST);
////        }
//
//
////        if (!tontineRepository.countByDate(LocalDate.now()).equals(session.getParticipants())){
////            return new ResponseEntity<>(new ResponseMessage("Attention! -> Vous ne pouvez pas encore bouffer car tout les membres n'ont pas encore cotisé"),
////              HttpStatus.BAD_REQUEST);
////        }
//        
//        
//        
//        beneficiaire.setDate(LocalDate.now());
//        
//        beneficiaire.setSession(session);
//        beneficiaire.setNom(user.getName());
//        
//        Notifications notifications = new Notifications(
//                user.getName()+" a bouffé la tontine",
//                LocalDate.now());
//        
//        beneficiaireRepository.save(beneficiaire);
//        tontineRepository.save(tontine);
//        notificationsRepository.save(notifications);
//      return new ResponseEntity<>(new ResponseMessage("Tontine bouffée"), HttpStatus.CREATED);
//    }

    @GetMapping
    public List<JSONObject> getTontine(){     
        List<Session> sess = sessionRepository.findByEtat(true);
        List<JSONObject> p ;
        Session session = new Session();
        if(!sess.isEmpty()){
            session = sess.get(0); 
            p = beneficiaireRepository.getAllBenefBySession(session.getIdSession());
        }else{
            p = new ArrayList<>();
        }
        return p ;
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

    
    @PostMapping("/news/")
    public ResponseEntity<?> newBenef(@RequestParam("id") Long id, @RequestBody Beneficiaire beneficiaire) { 
        Tontine tontine = new Tontine();
//        Beneficiaire beneficiaire = new Beneficiaire();
        User user = userRepository.findById(id).get();
        System.out.println("user: "+ user.getName());
        List<Session> sess = sessionRepository.findByEtat(true);
        
        if(sess.isEmpty()){
            return new ResponseEntity<>(new ResponseMessage("Attention! -> aucune session n'est en cours personne ne peut bouffer pour le moment"),
              HttpStatus.BAD_REQUEST);
        }else{
            
        
        
        Session session = sess.get(0);
        System.out.println("session: "+ session.getIdSession());
                
        Beneficiaire b = beneficiaireRepository.findFirstByOrderByIdBeneficiaireDesc();
        if(beneficiaireRepository.existsBySessionAndNom(session, user.getName())){
            return new ResponseEntity<>(new ResponseMessage("Attention! -> l'utilisateur "+user.getName()+" a déjà bouffé cette session"),
              HttpStatus.BAD_REQUEST);
        }
                        
        Tontine ton = tontineRepository.findFirstByOrderByIdTontineDesc();
        System.out.println("last: "+ ton.getMotif());
        if(!ton.equals(null) && ton.getMontant() >= beneficiaire.getMontant() ){
            double credit = beneficiaire.getMontant();
            System.out.println("credit: "+ credit);
            tontine.setCredit(credit);
            double solde = ton.getMontant() - credit;
            tontine.setMontant(solde);
            beneficiaire.setMontant(credit);
        }else{
            return new ResponseEntity<>(new ResponseMessage("Erreur! -> montant isuffisant pour bouffer"), 
                    HttpStatus.BAD_REQUEST);
        }
        
        tontine.setDebit(0);
        
        tontine.setMotif(user.getName()+": a bouffé");
        tontine.setUser(user);
        tontine.setDate(LocalDate.now()); 
        tontine.setSession(session);
//        session.setReunion(reunion);
//        System.out.println("session: "+ session.getDebut());
//        System.out.println("count date: "+ tontineRepository.countByDate(LocalDate.now()));
//        System.out.println("part: "+ session.getParticipants());
//        if(tontineRepository.existsByDateAndUser(tontine.getDate(), tontine.getUser())){
//            return new ResponseEntity<>(new ResponseMessage("Attention! -> l'utilisateur "+user.getName()+" a déjà cotisé"),
//              HttpStatus.BAD_REQUEST);
//        }


//        if (!tontineRepository.countByDate(LocalDate.now()).equals(session.getParticipants())){
//            return new ResponseEntity<>(new ResponseMessage("Attention! -> Vous ne pouvez pas encore bouffer car tout les membres n'ont pas encore cotisé"),
//              HttpStatus.BAD_REQUEST);
//        }
        
        
        
        beneficiaire.setDate(LocalDate.now());
        
        beneficiaire.setSession(session);
        beneficiaire.setNom(user.getName());
        
        Notifications notifications = new Notifications(
                user.getName()+" a bouffé la tontine",
                LocalDate.now());
        
        beneficiaireRepository.save(beneficiaire);
        tontineRepository.save(tontine);
        notificationsRepository.save(notifications);
      return new ResponseEntity<>(new ResponseMessage("Tontine bouffée"), HttpStatus.CREATED);
    }
    }
        
}
