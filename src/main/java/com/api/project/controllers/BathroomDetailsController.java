package com.api.project.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.project.models.BathroomAndPlaceDetails;
import com.api.project.models.BathroomDetails;
import com.api.project.repositories.BathroomDetailsRepository;
import com.google.maps.GeoApiContext;
import com.google.maps.PlaceDetailsRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlaceDetails;

@RestController
@RequestMapping("/api/bathroom-details")
@CrossOrigin("*")
public class BathroomDetailsController {
	@Autowired
	BathroomDetailsRepository bathroomDetailsRepo;
	
	@Value("${api.key}")
	private String apiKey;
	/**
	 * Gets the place details (from google api) and bathroom details (from MongoDB) of input place
	 * 
	 * @param placeId
	 * @return
	 */
	@GetMapping("/find-by-place-id/{placeId}")
	public BathroomAndPlaceDetails findByPlaceId(@PathVariable("placeID") String placeId){
		BathroomAndPlaceDetails bathroomAndPlaceDetails = new BathroomAndPlaceDetails();
		BathroomDetails bathroomDetails = bathroomDetailsRepo.findByPlaceId(placeId);
		PlaceDetails placeDetails = new PlaceDetails();
		
		try {
			placeDetails = new PlaceDetailsRequest(new GeoApiContext.Builder().apiKey(apiKey).build()).placeId(placeId).await();
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bathroomAndPlaceDetails.setBathroomDetails(bathroomDetails);
		bathroomAndPlaceDetails.setPlaceDetails(placeDetails);
		return bathroomAndPlaceDetails;
	}
	
}
