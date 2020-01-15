    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package beans;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;
import persistence.UserAccount;

/**
 *
 * @author ssome
 */
@Named(value = "loginBean")
@RequestScoped
public class LoginBean {
    private String userId;
    private String password;
    private String status;
    private String link;
    private Boolean logged;
    @PersistenceContext(unitName = "UserAccountPU")
    private EntityManager em;
    @Resource
    private javax.transaction.UserTransaction utx;
    /**
     * Creates a new instance of LoginBean
     */
    public LoginBean() {
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * @return the link
     */
    public String getLang() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLang(String link) {
        this.link = link;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    public Boolean getLogged() {
        return logged;
    }

    public void setLogged(Boolean logged) {
        this.logged = logged;
    }
    

    public void login() throws IOException {
         UserAccount acc = em.find(UserAccount.class, userId);
         FacesContext context = FacesContext.getCurrentInstance();
         ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
         if (acc != null) {
             try {
                 // check password
                 byte[] salt = acc.getSalt();
                 String saltString = new String(salt, "UTF-8");
                 String checkPass = saltString+password;
                 MessageDigest digest = MessageDigest.getInstance("SHA-256");
                 byte[] checkPassHash = digest.digest(checkPass.getBytes("UTF-8"));
                 if (Arrays.equals(checkPassHash, acc.getPassword())) {
                     //login ok - set user in session context
                     HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
                     session.setAttribute("User", acc);
                     String id = acc.getUserId();
                     
                     
                    if(id.charAt(0) == '1'){
                        
//                        status="Login Successful as an Admin- " + "Welcome " + acc.getFirstname();
                         setLogged(true);
                         status = bundle.getString("goodInsLogin") +" "+ acc.getFirstname();
                         FacesContext.getCurrentInstance().getExternalContext().redirect("/Projet/faces/protected/instructorOperations.xhtml");
                    }else{
                        setLogged(true);
                        status = bundle.getString("goodStudLogin") +" "+ acc.getFirstname();
//                        status="Login Successful as a Student - " + "Welcome " + acc.getFirstname(); 
                       FacesContext.getCurrentInstance().getExternalContext().redirect("/Projet/faces/studentOperations.xhtml");
                    }
                 } else {
                      setLogged(false);  
//                    status="Invalid Login, Please Try again"; 
                      status = bundle.getString("badLogin");
                 }
             } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
                 Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
             }
         } else {
               setLogged(false);  
//             status="Invalid Login, Please Try again";
               status = bundle.getString("badLogin");
         }
    }
    
    public String logout() throws IOException {
        // invalidate session to remove User
        
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
        FacesContext.getCurrentInstance().getExternalContext().redirect("/faces/index.xhtml");
        // navigate to index - see faces-config.xml for navigation rules
        return "logout";
    }
    
}
