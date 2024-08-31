package org.example;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as
 * possible Don't worry about performance, concurrency, etc You can use in Memory collection for
 * sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById Implementations
 * should be in a single class This class could be auto tested
 */
public class DocumentManager {

  private final Map<String, Document> documents = new HashMap<>();

  /**
   * Implementation of this method should upsert the document to your storage And generate unique id
   * if it does not exist, don't change [created] field
   *
   * @param document - document content and author data
   * @return saved document
   */
  public Document save(Document document) {
    String id = document.getId();
    if (id == null || id.isEmpty()) {
      document.setId(UUID.randomUUID().toString());
    }
    documents.put(document.getId(), document);
    return document;
  }

  /**
   * Implementation this method should find documents which match with request
   *
   * @param request - search request, each field could be null
   * @return list matched documents
   */
  public List<Document> search(SearchRequest request) {
    List<String> titlePrefixes = request.getTitlePrefixes();
    List<String> containsContents = request.getContainsContents();
    List<String> authorIds = request.getAuthorIds();
    Instant createdFrom = request.getCreatedFrom();
    Instant createdTo = request.getCreatedTo();

    return documents.values().stream()
        .filter(doc -> isNullOrEmpty(titlePrefixes) || titlePrefixes.stream()
            .anyMatch(prefix -> doc.getTitle().startsWith(prefix)))
        .filter(doc -> isNullOrEmpty(containsContents) || containsContents.stream()
            .anyMatch(content -> doc.getContent().contains(content)))
        .filter(doc -> isNullOrEmpty(authorIds) || authorIds.contains(doc.getAuthor().getId()))
        .filter(doc -> (createdFrom == null || !doc.getCreated().isBefore(createdFrom)) &&
            (createdTo == null || !doc.getCreated().isAfter(createdTo)))
        .toList();
  }

  private <T> boolean isNullOrEmpty(List<T> list) {
    return list == null || list.isEmpty();
  }

  /**
   * Implementation this method should find document by id
   *
   * @param id - document id
   * @return optional document
   */
  public Optional<Document> findById(String id) {
    return Optional.ofNullable(documents.get(id));
  }

  @Getter
  public static class SearchRequest {

    private List<String> titlePrefixes;
    private List<String> containsContents;
    private List<String> authorIds;
    private Instant createdFrom;
    private Instant createdTo;
  }

  @Getter
  @Setter
  public static class Document {

    private String id;
    private String title;
    private String content;
    private Author author;
    private Instant created;
  }

  @Getter
  public static class Author {

    private String id;
    private String name;
  }
}
