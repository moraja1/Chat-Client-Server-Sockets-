package data.model.repository;

import jakarta.persistence.*;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ContactListId implements Serializable {
    private static final long serialVersionUID = -6990201936877494832L;
    @Column(name = "owner_user", nullable = false)
    private Long ownerUser;

    @Column(name = "member_user", nullable = false)
    private Long memberUser;

    public Long getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(Long ownerUser) {
        this.ownerUser = ownerUser;
    }

    public Long getMemberUser() {
        return memberUser;
    }

    public void setMemberUser(Long memberUser) {
        this.memberUser = memberUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ContactListId entity = (ContactListId) o;
        return Objects.equals(this.ownerUser, entity.ownerUser) &&
                Objects.equals(this.memberUser, entity.memberUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerUser, memberUser);
    }

}