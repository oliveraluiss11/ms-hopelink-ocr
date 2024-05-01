package com.jayway.msocr.contract.repository;

import com.jayway.msocr.contract.entity.OcrDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcrRepository extends MongoRepository<OcrDocument, String> {
    Boolean existsByDocumentNumberAndOperationNumber(String documentNumber, String operationNumber);
}
