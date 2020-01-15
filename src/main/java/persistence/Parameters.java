/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author valentinmagot
 */
@Entity
@Table(name="Parameters8843488")
public class Parameters implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String code;
    private String minNum;
    private String maxNum;
    private String instructorId;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date expirationDate;
    
    
    

    public Parameters() {
    }

    public Parameters(String minNum, String maxNum, Date expirationDate, String code) {
        this.minNum = minNum;
        this.maxNum = maxNum;
        this.expirationDate = expirationDate;
        this.code = code;
    }

    
    public String getId() {
        return code;
    }

    public void setId(String id) {
        this.code = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (code != null ? code.hashCode() : 0);
        return hash;
    }
   
     
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Parameters)) {
            return false;
        }
        Parameters other = (Parameters) object;
        if ((this.getId()== null && other.getId()!= null) || (this.getId()!= null && !this.code.equals(other.code))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "persistence.Parameters8843488[ id=" + code + " ]";
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

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }
    
    
    
}
