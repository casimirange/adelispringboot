/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.Controller;


//import com.example.demo.entity.Role;
import com.example.demo.entity.Amande;
import com.example.demo.entity.Notifications;
import com.example.demo.entity.User;
import com.example.demo.message.response.ResponseMessage;
import com.example.demo.entity.Session;
import com.example.demo.repository.AmandeRepository;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 * @author Casimir
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin/amandes")
public class AmandesController {
           
    @Autowired
    TontineRepository tontineRepository;
    
    @Autowired
    SessionRepository sessionRepository;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    RetenueRepository retenueRepository;
    
    @Autowired
    AmandeRepository amandeRepository;
    
    @Autowired
    NotificationsRepository notificationsRepository;       
    
    JSONObject json;
    String mts;
    
    @PostMapping("")
    public ResponseEntity<?> createAmande(@RequestParam("user") User u, @RequestBody Amande amande) { 
        User user = userRepository.findById(u.getId()).get(); 
        List<Session> sess = sessionRepository.findByEtat(true);
        Amande am = amandeRepository.findFirstByOrderByIdAmandeDesc();
        amande.setCredit(amande.getCredit());
        amande.setDebit(amande.getDebit());
        double solde = 0;
        if(am != null){
            if(amande.getCredit() != 0){
                solde = am.getSolde() - amande.getCredit();
                amande.setSolde(solde);
            }else{
                solde = am.getSolde() + amande.getDebit();
                amande.setSolde(solde);
            }
            
        }else {
            solde = amande.getDebit();
            amande.setSolde(solde);
        }
        
        Session session = sess.get(0);
        amande.setDate(LocalDate.now());
        amande.setSession(session);
        amande.setUser(user);        
        
        Notifications notifications = new Notifications();
        notifications.setDescription(user.getName()+" a été amandé d'un montant de "+amande.getDebit()+ " € pour motif: "+amande.getMotif());
        notifications.setDate(LocalDate.now());    
        
        notificationsRepository.save(notifications);      
        amandeRepository.save(amande);
        return new ResponseEntity<>(new ResponseMessage("amande enregistrée"), HttpStatus.CREATED);
    }
    
    
    @PostMapping("/retrait/{id}")
    public ResponseEntity<?> retraitAmande(@PathVariable Long id, @RequestBody Amande amande) { 
        User user = userRepository.findById(id).get(); 
        List<Session> sess = sessionRepository.findByEtat(true);
        Amande am = amandeRepository.findFirstByOrderByIdAmandeDesc();
        double solde = 0;
        if(am != null){
            if(amande.getCredit() > am.getSolde()){
                return new ResponseEntity<>(new ResponseMessage("erreur! -> montant impossible à retiré car supérieur"), HttpStatus.BAD_REQUEST);
            }
            solde = am.getSolde() - amande.getCredit();
            amande.setSolde(solde);
        }else{
            solde = amande.getCredit();
            amande.setSolde(solde);
        }
        
        Session session = sess.get(0);
        amande.setDate(LocalDate.now());
        amande.setSession(session);
        amande.setDebit(0);
        amande.setUser(user);        
        
        Notifications notifications = new Notifications();
        notifications.setDescription(user.getName()+" a rétiré un montant de "+amande.getCredit()+ " € pour motif: "+amande.getMotif());
        notifications.setDate(LocalDate.now());    
        
        notificationsRepository.save(notifications);      
        amandeRepository.save(amande);
        return new ResponseEntity<>(new ResponseMessage("retrait effectué!"), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> remboursePret(@PathVariable Long id, @RequestBody Amande amande) { 
        Amande amandes = amandeRepository.findById(id).get();
//        amandes
//        prets2.setRembourse(true);
//        prets2.setDateRemboursement(LocalDate.now());
//        prets2.setMontant_rembourse(prets.getMontant_rembourse());
//        
//        
//        Notifications notifications = new Notifications(
//                prets2.getUser().getName()+" a remboursé son prêt de "+prets.getMontant_rembourse()+" €",
//                LocalDate.now());
//        
//        double solde = ton.getMontant() + prets.getMontant_rembourse();
//        tontine.setMontant(solde);        
//        tontine.setDebit(prets.getMontant_rembourse());
//        tontine.setCredit(0);
//        tontine.setMotif("remboursement de prêt par "+prets2.getUser().getName());
//        tontine.setUser(prets2.getUser());
//        tontine.setDate(LocalDate.now()); 
//        tontine.setSession(session); 
//        
//        pretRepository.save(prets2);
//        notificationsRepository.save(notifications);
//        tontineRepository.save(tontine);
        return new ResponseEntity<>(new ResponseMessage("prêt remboursé!"), HttpStatus.CREATED);
    }

//    @PostMapping("/{id}")
//    public ResponseEntity<?> createTontine(@PathVariable Long id, @RequestBody Tontine tontine) { 
//        User user = userRepository.findById(id).get();  
//        Retenue retenue = new Retenue();
//        System.out.println("user: "+ user.getName());
//        List<Session> sess = sessionRepository.findByEtat(true);
//        Session session = sess.get(0);
//        System.out.println("session: "+ session.getIdSession());
////        Tontine tontine = new Tontine();
//        
//        double montant = session.getMontant();
//        double mangwa = session.getRetenue();
//        System.out.println("session montant: "+ montant);
//        System.out.println("montant: "+ tontine.getDebit());
//        if (montant > tontine.getDebit()){
//            return new ResponseEntity<>(new ResponseMessage("Erreur! -> Le montant de la cotisation n'est pas correct"),
//              HttpStatus.BAD_REQUEST);
//        }   
//        Tontine ton = tontineRepository.findFirstByOrderByIdTontineDesc();
////        System.out.println("last: "+ ton.getIdTontine());
//        double solde = 0;
//        if(ton != null){
//            solde = ton.getMontant() + montant - mangwa;
//            tontine.setMontant(solde);
//        }else{
//            solde = montant - mangwa;
//            tontine.setMontant(solde);
//        }
////        double solde = ton.getMontant() + tontine.getDebit() - mangwa;
//        double debit = montant - mangwa;
//        
//        tontine.setDebit(debit);
//        tontine.setCredit(0);
//        tontine.setMotif("cotisation "+user.getName());
//        tontine.setUser(user);
//        if (!planningRepository.existsByDate(LocalDate.now())){
//            return new ResponseEntity<>(new ResponseMessage("Erreur! -> Ce jour n'est pas inscrit dans le planning de réunion"),
//              HttpStatus.BAD_REQUEST);
//        }
//        tontine.setDate(LocalDate.now()); 
//        tontine.setSession(session);
////        session.setReunion(reunion);
//        System.out.println("session: "+ session.getDebut());
//        if(tontineRepository.existsByDateAndUser(tontine.getDate(), tontine.getUser())){
//            return new ResponseEntity<>(new ResponseMessage("Attention! -> l'utilisateur "+user.getName()+" a déjà cotisé"),
//              HttpStatus.BAD_REQUEST);
//        }  
//                
//        tontineRepository.save(tontine);
//        
//        Retenue rete = retenueRepository.findFirstByOrderByIdRetenueDesc();
////        double ret = tontine.getDebit() - session.getRetenue();
//        double solde2 = 0;
//        if(rete != null){
//            solde2 = rete.getSolde() + mangwa;
//            retenue.setSolde(solde2);
//        }else{
//            solde2 = mangwa;
//            retenue.setSolde(solde2);
//        }
//        retenue.setDebit(mangwa);
//        retenue.setCredit(0);
//        retenue.setDate(LocalDate.now());
//        retenue.setUser(user);
//        retenue.setMotif("cotisation");
//        
//        
//        System.out.println("mangwa: "+ solde2);
//        retenueRepository.save(retenue);
//        
//      return new ResponseEntity<>(new ResponseMessage("Tontine enregistrée"), HttpStatus.CREATED);
//    }


    @GetMapping()
    public List<JSONObject> getActiveSessionTontine(){
        return amandeRepository.findAmande();
    }


    @GetMapping("/solde")
    public JSONObject getSoldeAmande(){
        Amande amande = amandeRepository.findFirstByOrderByIdAmandeDesc();
        Map<String, Object> response = new HashMap<>();
        JSONObject solde;
//        response.put("solde", amande.getSolde());
        if(amande != null){
            response.put("solde", amande.getSolde());
        }else{
            response.put("solde", 0);
        }
        solde = new JSONObject(response);
        return solde;
    }
             
    @GetMapping("/id/{id}")
    public List<JSONObject> getUsers(@PathVariable Long id) {
        User u = userRepository.findById(id).get();
        
        return amandeRepository.findAmandeUser(id);
    }
}
