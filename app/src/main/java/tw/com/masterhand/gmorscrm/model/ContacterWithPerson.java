package tw.com.masterhand.gmorscrm.model;

import tw.com.masterhand.gmorscrm.room.record.ContactPerson;

public class ContacterWithPerson {
    public ContactPerson contactPerson;
    public tw.com.masterhand.gmorscrm.room.record.Contacter contacter;

    public ContactPerson getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(ContactPerson contactPerson) {
        this.contactPerson = contactPerson;
    }

    public tw.com.masterhand.gmorscrm.room.record.Contacter getContacter() {
        return contacter;
    }

    public void setContacter(tw.com.masterhand.gmorscrm.room.record.Contacter contacter) {
        this.contacter = contacter;
    }

}
