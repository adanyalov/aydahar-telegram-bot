package com.adanyalov.aydahartelegrambot.repository;

import com.adanyalov.aydahartelegrambot.model.Deckbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface DeckboxRepository extends JpaRepository<Deckbox, Long> {
    List<Deckbox> findAllByOrderByIdAsc();

}
