package com.scrapflow.buyer.application;
import com.scrapflow.buyer.api.BuyerDtos;
import com.scrapflow.buyer.domain.Address;
import com.scrapflow.buyer.domain.BuyerDocument;
import com.scrapflow.buyer.domain.BuyerDocumentType;
import com.scrapflow.buyer.domain.BuyerProfile;
import com.scrapflow.buyer.domain.MaterialRequirement;
import com.scrapflow.buyer.infrastructure.BuyerDocumentRepository;
import com.scrapflow.buyer.infrastructure.BuyerProfileRepository;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BuyerService {
  private final BuyerProfileRepository buyers; private final BuyerDocumentRepository documents; private final DocumentStoragePort storage; private final DocumentContentValidator contentValidator;
  public BuyerService(BuyerProfileRepository buyers, BuyerDocumentRepository documents, DocumentStoragePort storage, DocumentContentValidator contentValidator) { this.buyers = buyers; this.documents = documents; this.storage = storage; this.contentValidator = contentValidator; }
  public BuyerDtos.BuyerResponse get(String userId) { return toResponse(profile(userId)); }
  public BuyerDtos.BuyerResponse upsert(String userId, BuyerDtos.UpsertBuyerRequest request) {
    BuyerProfile profile = buyers.findByUserId(userId).orElseGet(() -> new BuyerProfile(userId));
    try { profile.update(request.companyName().trim(), request.gstin().trim().toUpperCase(), request.pan().trim().toUpperCase(), sha256(request.aadhaar()), request.aadhaar().substring(8), address(request.companyAddress()), address(request.factoryAddress()), request.materialRequirements().stream().map(item -> new MaterialRequirement(item.category().trim(), item.monthlyRequirementKg())).toList(), request.totalMonthlyRequirementKg()); return toResponse(buyers.save(profile)); }
    catch (org.springframework.dao.DuplicateKeyException exception) { throw new ResponseStatusException(HttpStatus.CONFLICT, "GSTIN or PAN is already registered"); }
  }
  public BuyerDtos.BuyerDocumentResponse upload(String userId, BuyerDocumentType type, MultipartFile file) {
    BuyerProfile profile = profile(userId); validateFile(type, file); String detectedContentType;
    try (InputStream input = file.getInputStream()) { detectedContentType = contentValidator.validate(type, input); }
    catch (IOException exception) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot read uploaded document", exception); }
    String extension = extension(detectedContentType); String key = "buyers/" + profile.getId() + "/" + type.name().toLowerCase() + "/" + UUID.randomUUID() + extension;
    try (InputStream input = file.getInputStream()) { storage.store(key, input); }
    catch (IOException exception) { throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Document storage is unavailable", exception); }
    BuyerDocument document = new BuyerDocument(profile.getId(), type, key, detectedContentType, file.getSize(), sha256(file)); documents.findByBuyerIdAndType(profile.getId(), type).ifPresent(existing -> documents.delete(existing)); return toResponse(documents.save(document));
  }
  private BuyerProfile profile(String userId) { return buyers.findByUserId(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complete company details before uploading documents")); }
  private void validateFile(BuyerDocumentType type, MultipartFile file) { if (file == null || file.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A document is required"); long max = type == BuyerDocumentType.COMPANY_LOGO ? 5_000_000L : 10_000_000L; if (file.getSize() > max) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Document exceeds the permitted size"); }
  private Address address(BuyerDtos.AddressRequest source) { return new Address(source.line1().trim(), source.line2() == null ? null : source.line2().trim(), source.city().trim(), source.state().trim(), source.postalCode(), source.country().trim()); }
  private BuyerDtos.BuyerResponse toResponse(BuyerProfile profile) { List<BuyerDtos.BuyerDocumentResponse> uploads = documents.findByBuyerId(profile.getId()).stream().map(this::toResponse).toList(); return new BuyerDtos.BuyerResponse(profile.getId(), profile.getCompanyName(), profile.getGstin(), profile.getPan(), profile.getAadhaarLastFour(), address(profile.getCompanyAddress()), address(profile.getFactoryAddress()), profile.getMaterialRequirements().stream().map(item -> new BuyerDtos.MaterialRequirementResponse(item.category(), item.monthlyRequirementKg())).toList(), profile.getTotalMonthlyRequirementKg(), profile.getStatus(), uploads); }
  private BuyerDtos.AddressResponse address(Address address) { return new BuyerDtos.AddressResponse(address.line1(), address.line2(), address.city(), address.state(), address.postalCode(), address.country()); }
  private BuyerDtos.BuyerDocumentResponse toResponse(BuyerDocument document) { return new BuyerDtos.BuyerDocumentResponse(document.getId(), document.getType(), document.getContentType(), document.getSizeBytes(), document.getUploadedAt()); }
  private String sha256(String value) { try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); } catch (NoSuchAlgorithmException exception) { throw new IllegalStateException(exception); } }
  private String sha256(MultipartFile file) { try (InputStream stream = file.getInputStream()) { MessageDigest digest = MessageDigest.getInstance("SHA-256"); stream.transferTo(new java.io.OutputStream() { @Override public void write(int value) { digest.update((byte) value); } @Override public void write(byte[] buffer, int offset, int length) { digest.update(buffer, offset, length); } }); return HexFormat.of().formatHex(digest.digest()); } catch (IOException | NoSuchAlgorithmException exception) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot read uploaded document", exception); } }
  private String extension(String contentType) { return switch (contentType) { case "application/pdf" -> ".pdf"; case "image/png" -> ".png"; case "image/jpeg" -> ".jpg"; default -> throw new IllegalArgumentException("Unsupported content type"); }; }
}
