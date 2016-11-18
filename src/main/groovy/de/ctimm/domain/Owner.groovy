package de.ctimm.domain

import com.fasterxml.jackson.annotation.JsonIgnore

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
@Entity
class Owner {
    @JsonIgnore
    Date collectionTimestamp = new Date()
    @Id
    @GeneratedValue
    Long id
    Integer account
    String name
    String email
    String email1
    String email2
    String cellphone
    String phone
    String direction
    Date billUpdateTimeStamp
    @OneToMany(mappedBy = 'owner')
    @JsonIgnore
    List<Bill> billsList = new ArrayList<>()

    protected Owner() {
    }

    void addBill(Bill bill) {
        if (getBill(bill.number) != null) {
            billsList.remove(bill)
        }
        billsList.add(bill)
    }

    void addBills(List<Bill> bills) {
        billsList.addAll(bills)
    }

    Bill getBill(String billNumber) {
        billsList.find { (it.number == billNumber) }
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Owner owner = (Owner) o

        if (account != owner.account) return false
        if (cellphone != owner.cellphone) return false
        if (direction != owner.direction) return false
        if (email != owner.email) return false
        if (email1 != owner.email1) return false
        if (email2 != owner.email2) return false
        if (name != owner.name) return false
        if (phone != owner.phone) return false

        return true
    }

    int hashCode() {
        int result
        result = (account != null ? account.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        result = 31 * result + (email != null ? email.hashCode() : 0)
        result = 31 * result + (email1 != null ? email1.hashCode() : 0)
        result = 31 * result + (email2 != null ? email2.hashCode() : 0)
        result = 31 * result + (cellphone != null ? cellphone.hashCode() : 0)
        result = 31 * result + (phone != null ? phone.hashCode() : 0)
        result = 31 * result + (direction != null ? direction.hashCode() : 0)
        return result
    }

    Owner(Integer account) {
        this.account = account
    }
}
