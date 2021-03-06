package org.gluu.oxtrust.model.fido2;

import java.io.Serializable;
import java.util.Date;

import org.gluu.persist.model.base.BaseEntry;
import org.gluu.persist.annotation.AttributeName;
import org.gluu.persist.annotation.DataEntry;
import org.gluu.persist.annotation.ObjectClass;

/**
 * Fido2 base persistence entry
 *
 * @author Yuriy Movchan
 * @version 11/02/2018
 */
@DataEntry(sortBy = "creationDate")
@ObjectClass
public class Fido2Entry extends BaseEntry implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7351459527571263266L;

	@AttributeName(ignoreDuringUpdate = true, name = "oxId")
    private String id;

    @AttributeName(ignoreDuringUpdate = true, name = "oxCodeChallenge")
    private String challenge;

    @AttributeName(ignoreDuringUpdate = true, name = "oxCodeChallengeHash")
    private String challengeHash;

    @AttributeName(ignoreDuringUpdate = true, name = "creationDate")
    private Date creationDate;

    @AttributeName(ignoreDuringUpdate = true, name = "oxSessionStateId")
    private String sessionId;

    @AttributeName(name = "personInum")
    private String userInum;

    public Fido2Entry() {
    }

    public Fido2Entry(String dn) {
        super(dn);
    }

    public Fido2Entry(String dn, String id, Date creationDate, String sessionId, String userInum, String challenge) {
        super(dn);
        this.id = id;
        this.creationDate = creationDate;
        this.sessionId = sessionId;
        this.userInum = userInum;
        this.challenge = challenge;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public String getChallengeHash() {
        return challengeHash;
    }

    public void setChallengeHash(String challengeHash) {
        this.challengeHash = challengeHash;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserInum() {
        return userInum;
    }

    public void setUserInum(String userInum) {
        this.userInum = userInum;
    }

}