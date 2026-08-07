package com.scrapflow.buyer.infrastructure;
import com.scrapflow.buyer.domain.BuyerDocument;
import com.scrapflow.buyer.domain.BuyerDocumentType;
import java.util.Optional;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface BuyerDocumentRepository extends MongoRepository<BuyerDocument, String> { Optional<BuyerDocument> findByBuyerIdAndType(String buyerId, BuyerDocumentType type); List<BuyerDocument> findByBuyerId(String buyerId); }
