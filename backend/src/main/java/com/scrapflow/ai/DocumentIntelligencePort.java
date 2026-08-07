package com.scrapflow.ai;

import java.net.URI;
import java.util.Map;

/** Boundary for a future, human-reviewed document extraction provider. */
public interface DocumentIntelligencePort {
  ExtractionResult analyse(URI securedDocument, String documentType);
  record ExtractionResult(Map<String, String> fields, double confidence, String provider, String provenance) { }
}
