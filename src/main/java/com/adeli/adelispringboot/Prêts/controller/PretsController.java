/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.Controller;


//import com.example.demo.entity.Role;
import com.example.demo.entity.Notifications;
import com.example.demo.entity.Prets;
import com.example.demo.entity.User;
import com.example.demo.message.response.ResponseMessage;
import com.example.demo.entity.Session;
import com.example.demo.entity.Tontine;
import com.example.demo.repository.PretRepository;
import com.example.demo.repository.RetenueRepository;
import com.example.demo.repository.SessionRepository;
import com.example.demo.repository.TontineRepository;
import com.example.demo.repository.UserRepository;
import java.time.LocalDate;
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
@RequestMapping("/admin/prets")
public class PretsController {
           
    @Autowired
    TontineRepository tontineRepository;
    
    @Autowired
    SessionRepository sessionRepository;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    RetenueRepository retenueRepository;
    
    @Autowired
    PretRepository pretRepository;
    
    @Autowired
    NotificationsRepository notificationsRepository;       
    
    JSONObject json;
    String mts;
    
    @PostMapping("")
    public ResponseEntity<?> createPret(@RequestParam("user") User u, @RequestBody Prets prets) { 
        Tontine ton = tontineRepository.findFirstByOrderByIdTontineDesc();
        Tontine tontine = new Tontine();
        User user = userRepository.findById(u.getId()).get(); 
        List<Session> sess = sessionRepository.findByEtat(true);
        Session session = sess.get(0);
        if(ton.getMontant() < prets.getMontant_prete()){
            return new ResponseEntity<>(new ResponseMessage("Vous ne pouvez prêter ce montant "), HttpStatus.CREATED);
        }
        prets.setRembourse(false);
        prets.setUser(user);
        prets.setDatePret(LocalDate.now());
        prets.setMontant_prete(prets.getMontant_prete());
        prets.setDateRemboursement(null);
        prets.setMontant_rembourse(0);
        prets.setSession(session);
        
        
        Notifications notifications = new Notifications();
        notifications.setDescription(user.getName()+" a emprunté un montant de "+prets.getMontant_prete()+ " €");
        notifications.setDate(LocalDate.now());        
        
        
        double solde = ton.getMontant() - prets.getMontant_prete();
        tontine.setMontant(solde);        
        tontine.setDebit(0);
        tontine.setCredit(prets.getMontant_prete());
        tontine.setMotif("prêt de "+user.getName());
        tontine.setUser(user);
        tontine.setDate(LocalDate.now()); 
        tontine.setSession(session); 
        
        notificationsRepository.save(notifications);
        pretRepository.save(prets);        
        tontineRepository.save(tontine);
        return new ResponseEntity<>(new ResponseMessage("prêt validé!"), HttpStatus.CREATED);
    }
    
    @PutMapping("/rembouser/")
    public ResponseEntity<?> remboursePret(@RequestParam("pret") Long id, @RequestBody Prets prets) { 
        Prets prets2 = pretRepository.findById(id).get();
        Tontine ton = tontineRepository.findFirstByOrderByIdTontineDesc();
        Tontine tontine = new Tontine();
        List<Session> sess = sessionRepository.findByEtat(true);
        Session session = sess.get(0);
        double pret = (prets2.getMontant_prete() * (session.getTaux()/100)) + prets2.getMontant_prete(); 
        if(prets2.isRembourse()){
            System.out.println("pas de prêt");
            return new ResponseEntity<>(new ResponseMessage("ceci n'est pas un prêt !"), HttpStatus.CREATED);
        }
        if(pret != prets.getMontant_rembourse()){
            return new ResponseEntity<>(new ResponseMessage("le montant remboursé est érroné! "), HttpStatus.CREATED);
        }
        System.out.println("remb: "+ pret);
        prets2.setRembourse(true);
        prets2.setDateRemboursement(LocalDate.now());
        prets2.setMontant_rembourse(prets.getMontant_rembourse());
        
        
        Notifications notifications = new Notifications(
                prets2.getUser().getName()+" a remboursé son prêt de "+prets.getMontant_rembourse()+" €",
                LocalDate.now());
        
        double solde = ton.getMontant() + prets.getMontant_rembourse();
        tontine.setMontant(solde);        
        tontine.setDebit(prets.getMontant_rembourse());
        tontine.setCredit(0);
        tontine.setMotif("remboursement de prêt par "+prets2.getUser().getName());
        tontine.setUser(prets2.getUser());
        tontine.setDate(LocalDate.now()); 
        tontine.setSession(session); 
        
        pretRepository.save(prets2);
        notificationsRepository.save(notifications);
        tontineRepository.save(tontine);
        return new ResponseEntity<>(new ResponseMessage("prêt remboursé!"), HttpStatus.CREATED);
    }

    @GetMapping
    public List<JSONObject> getPrets(){      
        return pretRepository.findPrets();
    }

    @GetMapping("/solde")
    public JSONObject soldeTontine(){      
        Tontine tontine = tontineRepository.findFirstByOrderByIdTontineDesc();
        Map<String, Object> response = new HashMap<>();
        JSONObject solde;
        response.put("solde", tontine.getMontant());
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
        Session session = sess.get(0);
        return tontineRepository.TontineSession(session.getIdSession());
    }
    
    @DeleteMapping("/{id}")
    public void deletePret(@PathVariable Long id) {
            pretRepository.delete(pretRepository.findById(id).get());
    }
           
    @GetMapping("/id/{id}")
    public List<JSONObject> getUsers(@PathVariable Long id) {
        User u = userRepository.findById(id).get();
        
        return pretRepository.findPretsUser(id);
    }
        
}
