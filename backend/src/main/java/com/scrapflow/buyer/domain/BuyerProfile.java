package com.scrapflow.buyer.domain;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("buyers")
@CompoundIndex(name = "buyer_user_unique", def = "{'userId': 1}", unique = true)
public class BuyerProfile {
  @Id private String id;
  private String userId; private String companyName; @Indexed(unique = true, sparse = true) private String gstin; @Indexed(unique = true, sparse = true) private String pan; private String aadhaarHash; private String aadhaarLastFour;
  private Address companyAddress; private Address factoryAddress; private List<MaterialRequirement> materialRequirements; private BigDecimal totalMonthlyRequirementKg;
  private BuyerRegistrationStatus status; private Instant createdAt; private Instant updatedAt;
  public BuyerProfile() { }
  public BuyerProfile(String userId) { this.userId = userId; this.status = BuyerRegistrationStatus.DRAFT; this.createdAt = Instant.now(); this.updatedAt = this.createdAt; }
  public String getId() { return id; } public String getUserId() { return userId; } public String getCompanyName() { return companyName; } public String getGstin() { return gstin; } public String getPan() { return pan; } public String getAadhaarLastFour() { return aadhaarLastFour; } public Address getCompanyAddress() { return companyAddress; } public Address getFactoryAddress() { return factoryAddress; } public List<MaterialRequirement> getMaterialRequirements() { return materialRequirements; } public BigDecimal getTotalMonthlyRequirementKg() { return totalMonthlyRequirementKg; } public BuyerRegistrationStatus getStatus() { return status; }
  public void update(String companyName, String gstin, String pan, String aadhaarHash, String aadhaarLastFour, Address companyAddress, Address factoryAddress, List<MaterialRequirement> materialRequirements, BigDecimal totalMonthlyRequirementKg) { this.companyName = companyName; this.gstin = gstin; this.pan = pan; this.aadhaarHash = aadhaarHash; this.aadhaarLastFour = aadhaarLastFour; this.companyAddress = companyAddress; this.factoryAddress = factoryAddress; this.materialRequirements = List.copyOf(materialRequirements); this.totalMonthlyRequirementKg = totalMonthlyRequirementKg; this.status = BuyerRegistrationStatus.SUBMITTED; this.updatedAt = Instant.now(); }
}
