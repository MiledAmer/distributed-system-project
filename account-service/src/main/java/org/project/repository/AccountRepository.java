package org.project.repository;
import org.project.domain.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    
}
