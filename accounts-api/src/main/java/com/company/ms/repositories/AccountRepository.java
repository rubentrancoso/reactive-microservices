package com.company.ms.repositories;

import javax.transaction.Transactional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.company.ms.entities.Account;

@Repository
public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {
	@Transactional
	public Account findByAccountId(Long accountId);
}
