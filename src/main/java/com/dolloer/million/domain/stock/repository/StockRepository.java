package com.dolloer.million.domain.stock.repository;

import com.dolloer.million.domain.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    List<Stock> findBySymbolAndDateBeforeOrderByDateAsc(String symbol, LocalDate today);
}
