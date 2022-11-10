package com.adanyalov.aydahartelegrambot.repository;

import com.adanyalov.aydahartelegrambot.model.UserRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UserRequestRepository extends JpaRepository<UserRequest, Long> {
}
