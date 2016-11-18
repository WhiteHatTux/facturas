package de.ctimm.domain

import groovy.xml.XmlUtil

import javax.persistence.*
import java.sql.Timestamp

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
@Entity
class Bill {
    Date collectionTimestamp = new Date()
    @Id
    @GeneratedValue
    Long id
    Integer account
    @ManyToOne
    Owner owner
    String number
    Timestamp issued
    String accessKey
    Timestamp dateOfAuthorization
    @Lob
    private String xml
    Integer xmlNumber

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
}
