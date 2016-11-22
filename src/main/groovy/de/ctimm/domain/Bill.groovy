package de.ctimm.domain

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.xml.XmlUtil

import javax.persistence.*
import java.sql.Timestamp

/**
 * @author Christopher Timm <WhiteHatTux@timmch.de>
 *
 */
@Entity
class Bill {
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss"

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Date collectionTimestamp = new Date()
    @Id
    @GeneratedValue
    Long id
    Integer account
    @JsonIgnore
    @ManyToOne
    Owner owner
    String number
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Date issued
    String accessKey
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Date dateOfAuthorization
    @JsonIgnore
    @Lob
    private String xml
    @JsonIgnore
    Integer xmlNumber
    Double total
    String identification
    Double discounts


    Bill(Integer account) {
        this.account = account
    }

    protected Bill() {
    }

    def getXml() {
        if (xml != null) {
            return new XmlSlurper().parseText(xml)
        } else {
            return null
        }

    }

    void setXml(def xml) {
        this.xml = XmlUtil.serialize(xml)
    }


    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Bill bill = (Bill) o

        if (accessKey != bill.accessKey) return false
        if (account != bill.account) return false
        if (dateOfAuthorization != bill.dateOfAuthorization) return false
        if (discounts != bill.discounts) return false
        if (id != bill.id) return false
        if (identification != bill.identification) return false
        if (issued != bill.issued) return false
        if (number != bill.number) return false
        if (owner != bill.owner) return false
        if (total != bill.total) return false
        if (xmlNumber != bill.xmlNumber) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (account != null ? account.hashCode() : 0)
        result = 31 * result + (owner != null ? owner.hashCode() : 0)
        result = 31 * result + (number != null ? number.hashCode() : 0)
        result = 31 * result + (issued != null ? issued.hashCode() : 0)
        result = 31 * result + (accessKey != null ? accessKey.hashCode() : 0)
        result = 31 * result + (dateOfAuthorization != null ? dateOfAuthorization.hashCode() : 0)
        result = 31 * result + (xmlNumber != null ? xmlNumber.hashCode() : 0)
        result = 31 * result + (total != null ? total.hashCode() : 0)
        result = 31 * result + (identification != null ? identification.hashCode() : 0)
        result = 31 * result + (discounts != null ? discounts.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return "Bill{" +
                "id=" + id +
                ", account=" + account +
                ", number='" + number + '\'' +
                ", xmlNumber=" + xmlNumber +
                '}';
    }
}
