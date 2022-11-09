package data.model;


import jakarta.persistence.*;

@Entity
@Table(name = "contact_list")
public class ContactList {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EmbeddedId
    private ContactListId id;

    @MapsId("ownerUser")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user", nullable = false)
    private User ownerUser;

    @MapsId("memberUser")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_user", nullable = false)
    private User memberUser;

    public ContactListId getId() {
        return id;
    }

    public void setId(ContactListId id) {
        this.id = id;
    }

    public User getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(User ownerUser) {
        this.ownerUser = ownerUser;
    }

    public User getMemberUser() {
        return memberUser;
    }

    public void setMemberUser(User memberUser) {
        this.memberUser = memberUser;
    }

}