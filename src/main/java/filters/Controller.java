/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filters;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import persistence.Parameters;
import persistence.Team;
import persistence.UserRequest;
import persistence.UserTeam;



public class Controller {
    
    
    public Controller(){
         
    }
    
    
    public void persist(EntityManager em, UserTransaction utx, Object object) {
        try {
            utx.begin();
            em.persist(object);
            utx.commit();
        } catch (NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Find an Item based on Id
     * @param em
     * @param teamId
     * @return 
     */
    public Team findTeam(EntityManager em, String teamId) {
        Team team = em.find(Team.class, teamId);
        return team;
        
    }
    
 
    
        public List<Team> allTeam(EntityManager em) {
         Query query;
        query = em.createQuery(
                "SELECT t FROM Team t");
        return performQuery(query);
    }
    
    public List<Team> findTeambyProf(EntityManager em, String userId) {
        
         Query query2;
        query2 = em.createQuery("SELECT t FROM Team t, Parameters par WHERE t.code = par.code AND par.instructorId = :userId  ");
        query2.setParameter("userId",userId);
        return performQuery(query2);
    }
    
    public List<Team>findTeamLiaison(EntityManager em, String userId) {
        
         Query query7;
        query7 = em.createQuery("SELECT t FROM Team t WHERE t.liaison = :userId  ");
        query7.setParameter("userId",userId);
        return performQuery(query7);
    }
    
    public List<UserRequest>findRequests(EntityManager em, String teamId) {
        
         Query query7;
        query7 = em.createQuery("SELECT ur FROM UserRequest ur WHERE ur.teamId= :teamId");
        query7.setParameter("teamId",teamId);
        return performQuery(query7);
    }
       
     public List<Team> findByCode(EntityManager em, String cc) {
        
         Query query3 = em.createQuery(
                "SELECT t FROM Team t" +
                " WHERE t.code = :code");
        query3.setParameter("code",cc);
        return performQuery(query3);
    }
    
    public List<UserTeam> findUserTeam(EntityManager em, String userId, String code) {
        
         Query query4;
        query4 = em.createQuery(
                "SELECT ut FROM UserTeam ut WHERE ut.code = :code AND ut.userId = :userId");
        query4.setParameter("code",code);
        query4.setParameter("userId",userId);
        return performQuery(query4);
    } 
    
    public List<UserTeam> findUserinTeam(EntityManager em, String userId) {
        
         Query query6;
        query6 = em.createQuery(
                "SELECT ut FROM UserTeam ut WHERE ut.userId = :userId");
        query6.setParameter("userId",userId);
        return performQuery(query6);
    }
    
    public List<UserTeam> findUserofTeam(EntityManager em, String teamId) {
        
         Query query9;
        query9 = em.createQuery(
                "SELECT ut FROM UserTeam ut WHERE ut.teamId = :teamId");
        query9.setParameter("teamId",teamId);
        return performQuery(query9);
    }
    
    public List<UserRequest> findStudRequest(EntityManager em, String userId, String code) {
        
        Query query7;
        query7 = em.createQuery(
                "SELECT r FROM UserRequest r WHERE r.userId = :userId AND r.teamId = :code");
        query7.setParameter("userId",userId);
        query7.setParameter("code",code);
        return performQuery(query7);
    }
    
    public List<Team> findTeamIncomplete(EntityManager em) {
        String stat = "Incomple";
        
         Query query5;
        query5 = em.createQuery(
                "SELECT t FROM Team t WHERE t.status = :status");
        query5.setParameter("status",stat);
        return performQuery(query5);
    } 
    
    public List<UserRequest>findRequestByLiaison(EntityManager em, String liaison) {
        
         Query query8;
        query8 = em.createQuery(
                "SELECT r FROM UserRequest r WHERE r.liaison = :liaison");
        query8.setParameter("liaison",liaison);
        return performQuery(query8);
    }
    
    
    private List performQuery(final Query query) {
        List resultList = query.getResultList();
        if (resultList.isEmpty()) {
            return null;
        } 
        ArrayList<String> results;
        results = new ArrayList<>();
        results.addAll(resultList);
        return results;
    }


    
}
