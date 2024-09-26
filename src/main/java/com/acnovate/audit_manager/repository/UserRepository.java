package com.acnovate.audit_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acnovate.audit_manager.domain.User;

public interface UserRepository extends JpaRepository<User,Long> {

}