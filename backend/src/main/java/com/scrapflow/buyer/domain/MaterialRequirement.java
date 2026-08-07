package com.scrapflow.buyer.domain;
import java.math.BigDecimal;
public record MaterialRequirement(String category, BigDecimal monthlyRequirementKg) { }
