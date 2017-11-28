package com.company.ms.services.interfaces;

import javax.transaction.Transactional;

import com.company.ms.userapi.message.AccountData;

@Transactional
public interface ISrvAccount {

	public Object create(AccountData account);

	public Object limits();

	Object update(AccountData accountData);

}
