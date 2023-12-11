package com.baks.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Helps to avoid double document usage during multiple thread test execution, i.e. there are cases
 * when 'update' and 'delete' operations were performed with the same document.
 */
@Component
public class DocumentsUsageService {

  private final Set<String> documentsIdInUse = new HashSet<>();

  /**
   * Adds document id to list of documents in use.
   */
  public void addDocumentIdToUsageList(String documentId) {
    documentsIdInUse.add(documentId);
  }

  /**
   * Adds documents id's to list of documents in use.
   */
  public void addDocumentsIdToUsageList(List<String> documentIdList) {
    documentsIdInUse.addAll(documentIdList);
  }

  /**
   * Removes document id from list of documents in use.
   */
  public void removeUsedDocumentsFromList(List<String> documentsInTest) {
    documentsInTest.removeIf(documentsIdInUse::contains);
  }
}
