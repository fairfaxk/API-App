package com.api.project.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.api.project.models.BathroomDetails;

@Repository
public interface BathroomDetailsRepository extends MongoRepository<BathroomDetails, String> {
	public BathroomDetails findByPlaceId(String placeId);
}
