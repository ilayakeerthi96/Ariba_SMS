package com.itti.leadcapturing.repo;

import com.itti.leadcapturing.model.SupplierDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierDocumentRepository extends JpaRepository<SupplierDocument, Long> {

    /** All documents for a supplier, any type. */
    List<SupplierDocument> findBySupplierIdOrderByUploadedAtDesc(Long supplierId);

    /** Latest document of a specific type for a supplier. */
    Optional<SupplierDocument> findTopBySupplierIdAndDocumentTypeOrderByUploadedAtDesc(
            Long supplierId, String documentType);

    /** All documents of a specific type for a supplier. */
    List<SupplierDocument> findBySupplierIdAndDocumentTypeOrderByUploadedAtDesc(
            Long supplierId, String documentType);

    /** Check if a document type already exists for a supplier. */
    boolean existsBySupplierIdAndDocumentType(Long supplierId, String documentType);

    /** Delete all documents of a specific type for a supplier (before replacing). */
    void deleteBySupplierIdAndDocumentType(Long supplierId, String documentType);
}