package com.scrapflow.buyer.infrastructure;
import com.scrapflow.buyer.domain.BuyerProfile;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface BuyerProfileRepository extends MongoRepository<BuyerProfile, String> { Optional<BuyerProfile> findByUserId(String userId); }
