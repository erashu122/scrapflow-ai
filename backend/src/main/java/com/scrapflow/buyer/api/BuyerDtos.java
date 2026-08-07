package com.scrapflow.buyer.api;
import com.scrapflow.buyer.domain.BuyerDocumentType;
import com.scrapflow.buyer.domain.BuyerRegistrationStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class BuyerDtos {
  private BuyerDtos() { }
  public record AddressRequest(@NotBlank @Size(max = 160) String line1, @Size(max = 160) String line2, @NotBlank @Size(max = 80) String city, @NotBlank @Size(max = 80) String state, @NotBlank @Pattern(regexp = "^[1-9][0-9]{5}$", message = "must be a six-digit Indian postal code") String postalCode, @NotBlank String country) { }
  public record MaterialRequirementRequest(@NotBlank @Size(max = 80) String category, @NotNull @DecimalMin(value = "0.01") BigDecimal monthlyRequirementKg) { }
  public record UpsertBuyerRequest(@NotBlank @Size(max = 180) String companyName, @NotBlank @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$", message = "must be a valid GSTIN") String gstin, @NotBlank @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$", message = "must be a valid PAN") String pan, @NotBlank @Pattern(regexp = "^[2-9][0-9]{11}$", message = "must be a 12-digit Aadhaar number") String aadhaar, @NotNull @Valid AddressRequest companyAddress, @NotNull @Valid AddressRequest factoryAddress, @NotEmpty @Size(max = 20) List<@Valid MaterialRequirementRequest> materialRequirements, @NotNull @DecimalMin(value = "0.01") BigDecimal totalMonthlyRequirementKg) { }
  public record AddressResponse(String line1, String line2, String city, String state, String postalCode, String country) { }
  public record MaterialRequirementResponse(String category, BigDecimal monthlyRequirementKg) { }
  public record BuyerDocumentResponse(String id, BuyerDocumentType type, String contentType, long sizeBytes, Instant uploadedAt) { }
  public record BuyerResponse(String id, String companyName, String gstin, String pan, String aadhaarLastFour, AddressResponse companyAddress, AddressResponse factoryAddress, List<MaterialRequirementResponse> materialRequirements, BigDecimal totalMonthlyRequirementKg, BuyerRegistrationStatus status, List<BuyerDocumentResponse> documents) { }
}
