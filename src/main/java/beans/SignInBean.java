/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package beans;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import persistence.UserAccount;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;

/**
 *
 * @author ssome
 */
@Named(value = "signInBean")
@RequestScoped
public class SignInBean {
    private String userId;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String language;
    private Boolean liaison;
    @PersistenceContext(unitName = "UserAccountPU")
    private EntityManager em;
    @Resource
    private javax.transaction.UserTransaction utx;
    
    private String status;
    
    /**
     * Creates a new instance of SignInBean
     */
    public SignInBean() {
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
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname the firstname to set
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return the lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * @param lastname the lastname to set
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }


    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
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
     * @return the status message
     */
    public String getStatus() {
        return status;
    }

    public Boolean getLiaison() {
        return liaison;
    }

    public void setLiaison(Boolean liaison) {
        this.liaison = liaison;
    }
    
    
    public void persist(Object object) {
        try {
            utx.begin();
            em.persist(object);
            utx.commit();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", e);
            throw new RuntimeException(e);
        }
    }
    
    public void addUser() {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundle bundle = context.getApplication().getResourceBundle(context, "msg");
        try {
            UserAccount acc = new UserAccount();
            liaison = false;
            acc.setUserId(userId);
            acc.setFirstname(firstname);
            acc.setLastname(lastname);
            acc.setEmail(email);
            acc.setLiaison(liaison);
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "I AM HERE -1 ");
//            if(userId.charAt(0) == '1'){
//                type = "Instructor";
//                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "I AM HERE -2");
//            }
//            acc.setType(userId);
            // randomly generate salt value
            final Random r = new SecureRandom();
            byte[] salt = new byte[32];
            r.nextBytes(salt);
            String saltString = new String(salt, "UTF-8");
            // hash password using SHA-256 algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String saltedPass = saltString+password;
            byte[] passhash = digest.digest(saltedPass.getBytes("UTF-8"));
            acc.setSalt(salt);
            acc.setPassword(passhash);
            persist(acc);
//            status="New Account Created Fine";
            status = bundle.getString("goodSignin");
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | RuntimeException ex ) {
            Logger.getLogger(SignInBean.class.getName()).log(Level.SEVERE, null, ex);
//            status="Error While Creating New Account";
            status = bundle.getString("badSignin");
        }
    }
    
}
