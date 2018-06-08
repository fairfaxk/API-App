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
	@GetMapping("/findByPlaceId/{placeId}")
	public BathroomAndPlaceDetails findByPlaceId(@PathVariable("placeId") String placeId){
		BathroomAndPlaceDetails bathroomAndPlaceDetails = new BathroomAndPlaceDetails();
		BathroomDetails bathroomDetails = new BathroomDetails();
		bathroomDetails = bathroomDetailsRepo.findByPlaceId(placeId);
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


	@PostMapping("/add-bathroom-details")
	public BathroomAndPlaceDetails addPlaceDetails( @Valid @RequestBody BathroomAndPlaceDetails bathroomandplaceDetails){

		//get bathroom details
		BathroomDetails bathroomDetails = bathroomDetailsRepo.findPlaceById(bathroomandplaceDetails.getBathroomDetails.getPlaceId());
		PlaceDetails placeDetails = new PlaceDetails();

		//get all room info
		Bathroom mens = bathroomandplaceDetails.getBathroomDetails().getMensRoom();
		Bathroom womens = bathroomandplaceDetails.getBathroomDetails().getWomensRoom();
		Bathroom genderNeutral = bathroomandplaceDetails.getBathroomDetails().getGenderNeutralRoom();

		//get current ratings and codes from bathroom details
		List<Ratings> mensRatings = bathroomDetails.getMensRoom.getRatings();
		List<Codes> mensCodes = bathroomDetails.getMensRoom.getCodes();
		List<Ratings> womensRatings = bathroomDetails.getWomensRoom.getRatings();
		List<Codes> womensCodes = bathroomDetails.getWomensRoom.getCodes();
		List<Ratings> genderNeutralRatings = bathroomDetails.getGenderNeutralRoom.getRatings();
		List<Codes> genderNeutralCodes = bathroomDetails.getGenderNeutralRoom.getCodes();

		//add ratings and codes
		mensRatings.add(mens.getRatings());
		mensCodes.add(mens.getCodes());
		womensRatings.add(womens.getRatings());
		womensCodes.add(womens.getCodes());
		genderNeutralRatings.add(genderNeutral.getRatings());
		genderNeutralCodes.add(genderNeutral.getCodes());

		//update bathrooms
		mens.setRatings(mensRatings);
		mens.setCodes(mensCodes);
		womens.setRatings(womensRatings);
		womens.setCodes(womensCodes);
		genderNeutral.setRatings(genderNeutralRatings);
		genderNeutral.setCodes(genderNeutralCodes);

		//set updated bathroom details
		bathroomDetails.setMensRoom(bathroomandplaceDetails.getBathroomDetails().getMensRoom());
		bathroomDetails.setWomensRoom(bathroomandplaceDetails.getBathroomDetails().getWomensRoom());
		bathroomDetails.setGenderNeutralRoom(bathroomandplaceDetails.getBathroomDetails().getGenderNeutralRoom());
		placeDetails = bathroomandplaceDetails.getPlaceDetails();

		//save in bathroom details repo
		bathroomDetailsRepo.save(bathroomDetails);

		return bathroomandplaceDetails;

	}
	
}
