/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;


import filters.Controller;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
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
import persistence.UserTeam;

/**
 *
 * @author valentinmagot
 */
@Named(value = "createTeamBean")
@RequestScoped
public class CreateTeamBean {
    
    private String id;
    private String name;
    private String code;
    private String status;
    private String userId;
    private List<Team> tems;
    Controller control_ = new Controller();
    @PersistenceContext(unitName = "UserAccountPU")
    EntityManager em;
    @Resource
    private javax.transaction.UserTransaction utx;
    
    Parameters par;
    UserTeam ut;

    public CreateTeamBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Team> getTems() {
        tems = control_.allTeam(em);
        return tems;
    }

    public void setTems(List<Team> tems) {
        this.tems = tems;
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    public UserTransaction getUtx() {
        return utx;
    }

    public void setUtx(UserTransaction utx) {
        this.utx = utx;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void persist(EntityManager em, UserTransaction utx, Object object) {
        FacesContext context = FacesContext.getCurrentInstance();
         ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
        try {
            utx.begin();
            em.persist(object);
            utx.commit();
        } catch (NotSupportedException | SystemException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            throw new RuntimeException(e);
        }catch (RollbackException ex){
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ex);
            status= bundle.getString("badCreation");
       }
    }
    
    private static java.sql.Date convertUtilToSql(java.util.Date uDate) {
        java.sql.Date sDate = new java.sql.Date(uDate.getTime());
        return sDate;
    }
    
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void createTeam() {

    FacesContext facesContext = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
    UserAccount user = (UserAccount) session.getAttribute("User");
//         SetParametersBean param = new SetParametersBean();
//         Parameters par = param.find(code);
         par = em.find(Parameters.class, code);
         FacesContext context = FacesContext.getCurrentInstance();
         ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
         
         List<UserTeam> userTeam = control_.findUserTeam(em, user.getUserId(), code);
         
         if(userTeam == null){
             if(par != null){
             
                try {
                   Team nt = new Team();
                   Date date = new Date();
                   java.sql.Date sqlDate = new java.sql.Date(date.getTime());
                   nt.setId(id);
                   nt.setName(name);
                   nt.setMinNum(par.getMinNum());
                   nt.setMaxNum(par.getMaxNum());
                   nt.setCode(code);
                   nt.setCreationDate(sqlDate);
                   nt.setLiaison(user.getUserId());
                   Date expiration = par.getExpirationDate();
                   java.sql.Date expirationDate = convertUtilToSql(expiration);
                   setUserToTeam(user.getUserId(), id, code);
                   nt.setExpirationDate(expirationDate);
                   em.persist(nt);
                   utx.commit();
                   status= bundle.getString("goodCreation") +", "+bundle.getString("liaison")+": "+user.getFirstname();
                }catch (IllegalArgumentException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException ex) {
                   Logger.getLogger(SetParametersBean.class.getName()).log(Level.SEVERE, null, ex);
                   status= bundle.getString("badCreation");
                }catch (IllegalStateException ex){
                    status= bundle.getString("goodCreation") +", "+bundle.getString("liaison")+": "+user.getFirstname();
                }
            }else {
                status = bundle.getString("badParCreation");
            }
         }else {
             Logger.getLogger(SetParametersBean.class.getName()).log(Level.SEVERE, null, userTeam.toString());
             status = bundle.getString("studInTeam");
         }
         
    }
    
    public void setUserToTeam(String userId, String teamId, String code){
        ut = new UserTeam();
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
        try{
            ut.setTeamId(teamId);
            ut.setUserId(userId);
            ut.setCode(code);
            em.persist(ut);
            utx.commit();
            status= bundle.getString("goodCreation");
        }catch (IllegalStateException | IllegalArgumentException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException ex) {
            status= bundle.getString("badCreation");
        }
        
    }
    
}
