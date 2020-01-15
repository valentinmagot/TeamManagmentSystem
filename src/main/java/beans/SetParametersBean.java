/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import java.io.Serializable;
import java.sql.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import persistence.Parameters;
import persistence.UserAccount;

/**
 *
 * @author valentinmagot
 */
@Named(value = "setParametersBean")
@SessionScoped
public class SetParametersBean implements Serializable{
    
    private String code;
    private String minNum;
    private String maxNum;
    private String expirationDate;
    
    private String status;
    
    @PersistenceContext(unitName = "UserAccountPU")
    EntityManager em;
    @Resource
    private javax.transaction.UserTransaction utx;

    public SetParametersBean() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMinNum() {
        return minNum;
    }

    public void setMinNum(String minNum) {
        this.minNum = minNum;
    }

    public String getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(String maxNum) {
        this.maxNum = maxNum;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public Parameters find(String code) {
        return em.find(Parameters.class, code);
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
    
    

    public boolean setParameters() {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
    UserAccount user = (UserAccount) session.getAttribute("User");
        
        try {
            utx.begin();
            Parameters par = new Parameters();
            par.setId(code);
            par.setMinNum(minNum);
            par.setMaxNum(maxNum);
            par.setExpirationDate(Date.valueOf(expirationDate));
            par.setInstructorId(user.getUserId());
            em.persist(par);
            utx.commit();
            status = bundle.getString("goodPar");
            return true;
        } catch (IllegalArgumentException | NotSupportedException | SystemException | RollbackException | HeuristicMixedException | HeuristicRollbackException | SecurityException | IllegalStateException ex) {
            Logger.getLogger(SetParametersBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        status = bundle.getString("badPar");
        return false;
    }
    
}
