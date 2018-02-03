/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Emm
 */
@Entity
@Table (name = "tb_papel")
public class Papel implements Serializable {
    
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private int idPapel;
   
    @Column(nullable=false)
    private String descPapel;
    
    private boolean ativo = true;
    
    private boolean privAdmin = false;
    
    @Column(name="PRIV_SUPERADMIN")
    private boolean privSuperAdmin = false;

    public boolean isPrivSuperAdmin() {
        return privSuperAdmin;
    }

    public void setPrivSuperAdmin(boolean privSuperAdmin) {
        this.privSuperAdmin = privSuperAdmin;
    }
    
    @OneToMany
    private List<Usuario> usuario;

    public List<Usuario> getUsuario() {
        return usuario;
    }

    public void setUsuario(List<Usuario> usuario) {
        this.usuario = usuario;
    }

    public int getIdPapel() {
        return idPapel;
    }

    public void setIdPapel(int idPapel) {
        this.idPapel = idPapel;
    }

    public String getDescPapel() {
        return descPapel;
    }

    public void setDescPapel(String descPapel) {
        this.descPapel = descPapel;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public boolean isPrivAdmin() {
        return privAdmin;
    }

    public void setPrivAdmin(boolean privAdmin) {
        this.privAdmin = privAdmin;
    }
    
    
}
