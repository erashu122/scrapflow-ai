package com.scrapflow.buyer.api;
import com.scrapflow.buyer.application.BuyerService;
import com.scrapflow.buyer.domain.BuyerDocumentType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/buyers/me")
@PreAuthorize("hasRole('BUYER')")
@Tag(name = "Buyer registration")
public class BuyerController {
  private final BuyerService buyers;
  public BuyerController(BuyerService buyers) { this.buyers = buyers; }
  @GetMapping @Operation(summary = "Get the current buyer registration") public BuyerDtos.BuyerResponse get(Authentication authentication) { return buyers.get((String) authentication.getPrincipal()); }
  @PutMapping @Operation(summary = "Create or update the buyer registration") public BuyerDtos.BuyerResponse upsert(Authentication authentication, @Valid @RequestBody BuyerDtos.UpsertBuyerRequest request) { return buyers.upsert((String) authentication.getPrincipal(), request); }
  @PostMapping(path = "/documents", consumes = "multipart/form-data") @ResponseStatus(HttpStatus.CREATED) @Operation(summary = "Upload a buyer registration document") public BuyerDtos.BuyerDocumentResponse upload(Authentication authentication, @RequestParam BuyerDocumentType type, @RequestPart("file") MultipartFile file) { return buyers.upload((String) authentication.getPrincipal(), type, file); }
}
