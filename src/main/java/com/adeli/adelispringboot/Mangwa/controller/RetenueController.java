/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.Controller;


//import com.example.demo.entity.Role;
import com.example.demo.entity.Notifications;
import com.example.demo.entity.Planing;
import com.example.demo.entity.Retenue;
import com.example.demo.entity.User;
import com.example.demo.message.response.ResponseMessage;
import com.example.demo.entity.Session;
import com.example.demo.entity.Tontine;
import com.example.demo.repository.NotificationsRepository;
import com.example.demo.repository.PlanningRepository;
import com.example.demo.repository.RetenueRepository;
import com.example.demo.repository.ReunionRepository;
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
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 * @author Casimir
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/retenue")
public class RetenueController {
    
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
    
    JSONObject json;
    String mts;


    @GetMapping
    public List<JSONObject> getRetenue(){      
        return retenueRepository.findMangwa();
    }

    @PostMapping("")
    public ResponseEntity<?> createMangwa(@RequestBody Retenue ret, @RequestParam("user") User u) { 
        System.err.println("id: "+u.getId());
//        Long id = Long.parseLong(x);
        User user = userRepository.findById(u.getId()).get();  
//        List<Session> sess = sessionRepository.findByEtat(true);
//        Session session = sess.get(0);
        Retenue retenue = retenueRepository.findFirstByOrderByIdRetenueDesc();
        if(retenue  != null){
            if(ret.getTransaction().equals("Débit")){
                double solde = retenue.getSolde() + ret.getDebit();
                ret.setSolde(solde);
            }else {
                double soldes = retenue.getSolde() - ret.getCredit();
                ret.setSolde(soldes);
            }
        }else{
            if(ret.getTransaction().equals("Débit")){
                double solde = ret.getDebit();
                ret.setSolde(solde);
            }else {
                double soldes = ret.getCredit();
                ret.setSolde(soldes);
            }
        }
        
//        planing.setDate(planing.getDate());
        ret.setUser(user);
        
//        if (planing.getDate().isBefore(session.getDebut())){
//            return new ResponseEntity<>(new ResponseMessage("Erreur! -> La date ne peut être définié avant la période de session"),
//              HttpStatus.BAD_REQUEST);
//        }
//        
//        if (planing.getDate().isAfter(session.getFin())){
//            return new ResponseEntity<>(new ResponseMessage("Erreur! -> La date ne peut être définié après la période de session"),
//              HttpStatus.BAD_REQUEST);
//        }
//        
//        if (planningRepository.existsByDate(planing.getDate())){
//            return new ResponseEntity<>(new ResponseMessage("Erreur! -> Cette date a déjà été enregistré au cours de cette session"),
//              HttpStatus.BAD_REQUEST);
//        }
//        
//        if (planningRepository.countBySession(session) == 12){
//            return new ResponseEntity<>(new ResponseMessage("Attention! -> Vous avez déjà atteint le nombre maximal d'enregistrement"),
//              HttpStatus.BAD_REQUEST);
//        }
        
        double x = ret.getDebit() + ret.getCredit();
        Notifications notifications = new Notifications();
        notifications.setDescription(ret.getUser().getName() +" a effectué une transaction de "+ x +" € pour motif "+ret.getMotif());
        notifications.setDate(LocalDate.now());    
        
        
        retenueRepository.save(ret);
        notificationsRepository.save(notifications); 
      return new ResponseEntity<>(new ResponseMessage("mouvement mangwa effectué"),
              HttpStatus.OK);
    }
    
    @GetMapping("/solde")
    public JSONObject soldeRetenue(){      
        Retenue retenue = retenueRepository.findFirstByOrderByIdRetenueDesc();
        Map<String, Object> response = new HashMap<>();
        JSONObject solde;
//        response.put("solde", retenue.getSolde());
        if(retenue != null){
            response.put("solde", retenue.getSolde());
        }else{
            response.put("solde", 0);
        }
        solde = new JSONObject(response);
        return solde;
    }

    @GetMapping("/session/{id}")
    public List<JSONObject> getSessionTontine(@PathVariable Long id){ 
        return tontineRepository.TontineSession(id);
    }

        
}
