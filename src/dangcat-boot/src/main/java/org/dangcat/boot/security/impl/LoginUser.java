package org.dangcat.boot.security.impl;

import org.dangcat.boot.security.SecurityUtils;
import org.dangcat.commons.utils.ValueUtils;

import java.util.Collection;
import java.util.HashSet;

public class LoginUser implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String no;
    private String password;
    private Collection<Integer> permissions = new HashSet<Integer>();
    private String role;
    private String type;

    public LoginUser(Integer id, String no, String name, String role, String password, String type) {
        this.id = id;
        this.no = no;
        this.name = name;
        this.role = role;
        this.password = password;
        this.type = type;
    }

    public LoginUser(String no, String role, String password, String type) {
        this(null, no, no, role, password, type);
    }

    public boolean checkPassword(String password) {
        return SecurityUtils.isMatch(password, this.getPassword());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        LoginUser other = (LoginUser) obj;
        if (this.no == null) {
            if (other.no != null)
                return false;
        } else if (!this.no.equals(other.no))
            return false;
        return true;
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getNo() {
        return this.no;
    }

    public String getPassword() {
        return this.password;
    }

    public Collection<Integer> getPermissions() {
        return this.permissions;
    }

    public String getRole() {
        return this.role;
    }

    public String getType() {
        return this.type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.no == null) ? 0 : this.no.hashCode());
        return result;
    }

    public boolean isValid() {
        return !(ValueUtils.isEmpty(this.no) || ValueUtils.isEmpty(this.role) || ValueUtils.isEmpty(this.password));
    }
}
