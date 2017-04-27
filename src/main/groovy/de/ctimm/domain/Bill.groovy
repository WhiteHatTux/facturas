package de.ctimm.domain

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.xml.XmlUtil

import javax.persistence.*

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
    @Transient
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    Date dateOfNecessaryPayment
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

    void setXml(xml) {
        if (xml == null) {
            this.xml = null
        } else {
            this.xml = XmlUtil.serialize(xml)
        }

    }


    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Bill bill = (Bill) o

        if (accessKey != bill.accessKey) return false
        if (account != bill.account) return false
        if (dateOfAuthorization != bill.dateOfAuthorization) return false
        if (issued != bill.issued) return false
        if (number != bill.number) return false
        if (owner != bill.owner) return false

        return true
    }

    int hashCode() {
        int result
        result = 31 * result + (account != null ? account.hashCode() : 0)
        result = 31 * result + (owner != null ? owner.hashCode() : 0)
        result = 31 * result + (number != null ? number.hashCode() : 0)
        result = 31 * result + (issued != null ? issued.hashCode() : 0)
        result = 31 * result + (accessKey != null ? accessKey.hashCode() : 0)
        result = 31 * result + (dateOfAuthorization != null ? dateOfAuthorization.hashCode() : 0)
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
