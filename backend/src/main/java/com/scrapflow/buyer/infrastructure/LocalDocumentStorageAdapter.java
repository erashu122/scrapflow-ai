package com.scrapflow.buyer.infrastructure;
import com.scrapflow.buyer.api.BuyerStorageProperties;
import com.scrapflow.buyer.application.DocumentStoragePort;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.springframework.stereotype.Component;
@Component
public class LocalDocumentStorageAdapter implements DocumentStoragePort {
  private final Path root;
  public LocalDocumentStorageAdapter(BuyerStorageProperties properties) { this.root = Path.of(properties.rootPath()).toAbsolutePath().normalize(); }
  @Override public void store(String key, InputStream content) throws IOException { Path destination = root.resolve(key).normalize(); if (!destination.startsWith(root)) throw new IOException("Invalid document storage path"); Files.createDirectories(destination.getParent()); Files.copy(content, destination, StandardCopyOption.REPLACE_EXISTING); }
}
