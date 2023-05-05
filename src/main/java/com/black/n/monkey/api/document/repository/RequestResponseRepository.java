package com.black.n.monkey.api.document.repository;

import com.black.n.monkey.api.document.domain.RequestResponse;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RequestResponseRepository extends CrudRepository<RequestResponse, UUID> {
}
