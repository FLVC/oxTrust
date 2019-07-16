package org.gluu.oxtrust.ldap.service;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.gluu.persist.PersistenceEntryManager;
import org.gluu.persist.ldap.impl.LdapEntryManagerFactory;

@Stateless
@Named
public class DataSourceTypeService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1941135478226842653L;

	@Inject
	private PersistenceEntryManager ldapEntryManager;

	public boolean isLDAP() {
		String result = ldapEntryManager.getPersistenceType(null);
		if (result.equals(LdapEntryManagerFactory.PERSISTANCE_TYPE)) {
			return true;
		}
		return false;
	}

}