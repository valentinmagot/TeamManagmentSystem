/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import filters.Controller;
import java.util.List;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transactional;
import javax.transaction.UserTransaction;
import persistence.Parameters;
import persistence.Team;
import persistence.UserAccount;
import persistence.UserRequest;
import persistence.UserTeam;

/**
 *
 * @author valentinmagot
 */
@Named(value = "visualizeTeamBean")
@RequestScoped
public class VisualizeTeamBean implements Serializable{
    
    private List<Team> teams;
    private List<Team> teamsIncomplete;
    private List<Team> allteams;
    private List<UserRequest> req;
    private List<Parameters> par;
    Controller control_;
    private String teamId;
    UserRequest ur;
    private String status;
    
    @PersistenceContext(unitName = "UserAccountPU")
    EntityManager em;
    @Resource
    private javax.transaction.UserTransaction utx;
    
    private static final Logger LOG = Logger.getLogger(VisualizeTeamBean.class.getName());

    public VisualizeTeamBean() {
        this.teams = new ArrayList<>();
        this.allteams = new ArrayList<>();
        this.control_ = new Controller();
        this.par = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.req = new ArrayList<>();
        this.status = "";
        
        
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
        @Transactional(Transactional.TxType.REQUIRES_NEW)
        public boolean setUserToTeam(ActionEvent event){
            String team = (String)event.getComponent().getAttributes().get("action");
            String liaison = (String)event.getComponent().getAttributes().get("action2");
            String code = (String)event.getComponent().getAttributes().get("action3");
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
            
        
            HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
            UserAccount user = (UserAccount) session.getAttribute("User");
            List<UserTeam> userTeam = control_.findUserinTeam(em, user.getUserId());
            List<UserRequest> userRequests = control_.findStudRequest(em, user.getUserId(), team);
         
         if(userTeam == null){
            if(userRequests == null){
                    ur = new UserRequest();
                try{
                    ur.setTeamId(team);
                    ur.setUserId(user.getUserId());
                    ur.setLiaison(liaison);
                    ur.setCourse(code);
                    em.persist(ur);
                    utx.commit();
                    status= bundle.getString("studReq");
                    return true;
                }catch ( IllegalArgumentException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ex);
                    status= bundle.getString("studReqFail");
                    return false;
                }catch (IllegalStateException ex){
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ex);
                    status= bundle.getString("studReq");
                    return false;
                }
            }else {
                status = bundle.getString("alreadyReq");
                return false;
            }
        
          }else {
             status = bundle.getString("studInTeam");
             return false;
          }
        }    
    
        
        @Transactional(Transactional.TxType.REQUIRES_NEW)
        public boolean addUserToTeam(ActionEvent event){
            String team = (String)event.getComponent().getAttributes().get("action");
            String code = (String)event.getComponent().getAttributes().get("action3");
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
            
        
            HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
            UserAccount user = (UserAccount) session.getAttribute("User");
            List<UserTeam> userTeam = control_.findUserinTeam(em, user.getUserId());
            List<Team> wichedteam = control_.findByCode(em, team);
            List<UserTeam> usersInTeam = control_.findUserofTeam(em, team);
         
         if(userTeam == null){
            if(usersInTeam.size() >= Integer.parseInt(wichedteam.get(0).getMaxNum())){
                UserTeam ut = new UserTeam();
                try{
                    ut.setTeamId(team);
                    ut.setUserId(user.getUserId());
                    ut.setCode(code);
                    em.persist(ut);
                    utx.commit();
                    status= "Student added to team";
                    return true;
                }catch ( IllegalArgumentException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ex);
                    status= "Hmmm something happened";
                    return false;
                }catch (IllegalStateException ex){
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ex);
                    status= "Student added to team";
                    return false;
                }
            }else {
                status = "Team is already full";
                return false;
            }  
          }else {
             status = bundle.getString("studInTeam");
             return false;
          }
        }


    public List<Team> getTeams() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        UserAccount user = (UserAccount) session.getAttribute("User");
    
        teams = control_.findTeambyProf(em, user.getUserId());
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }

    public List<Team> getTeamsIncomplete() {
    
        teamsIncomplete = control_.findTeamIncomplete(em);
        return teamsIncomplete;
    }

    public void setTeamsIncomplete(List<Team> teamsIncomplete) {
        this.teamsIncomplete = teamsIncomplete;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public List<UserRequest> getReq() {
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        UserAccount user = (UserAccount) session.getAttribute("User");
        
        req = control_.findRequestByLiaison(em, user.getUserId());
        LOG.log(Level.SEVERE, req.toString());
        return req;
    }

    public void setReq(List<UserRequest> req) {
        this.req = req;
    }

    
    
    
}
