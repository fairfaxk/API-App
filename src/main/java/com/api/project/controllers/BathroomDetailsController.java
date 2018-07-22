package com.api.project.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.project.models.Bathroom;
import com.api.project.models.BathroomAndPlaceDetails;
import com.api.project.models.BathroomDetails;
import com.api.project.models.Code;
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
	
	/**
	 * Gets the bathroom details (from MongoDB) of input place
	 * 
	 * @param placeId
	 * @return
	 */
	@GetMapping("/findBathroomByPlaceId/{placeId}")
	public BathroomDetails findBathroomByPlaceId(@PathVariable("placeId") String placeId){
		BathroomDetails bathroomDetails = new BathroomDetails();
		bathroomDetails = bathroomDetailsRepo.findByPlaceId(placeId);
		return bathroomDetails;
	}

	@PutMapping("/add-bathroom-details")
	public BathroomDetails addBathroomDetails( @Valid @RequestBody BathroomDetails bathroomDetails){
		//get bathroom details
		BathroomDetails newBathroomDetails = bathroomDetailsRepo.findByPlaceId(bathroomDetails.getPlaceId());
		
		if(newBathroomDetails!=null){
			if(newBathroomDetails.getMensRoom()!=null){
				//Check if one already exists in the database for our given place
				if(newBathroomDetails != null){
					//Checks if the user is saying that there is a mens room (only updates this if it was previously false)
					if(!newBathroomDetails.getMensRoom().isExists() && newBathroomDetails.getMensRoom()!=null){
						newBathroomDetails.getMensRoom().setExists(bathroomDetails.getMensRoom().isExists());
					}			
					//Checks if handicap accessibility was previously set to false, and updates it if the user changed it to true
					if(!newBathroomDetails.getMensRoom().isHandicap()){
						newBathroomDetails.getMensRoom().setHandicap(bathroomDetails.getMensRoom().isHandicap());
					}			
					//If there is a mens room, add the codes and ratings. Can't have codes or ratings if there is no mens room
					if(newBathroomDetails.getMensRoom().isExists()){
						if(bathroomDetails.getMensRoom().getCodes()!=null){
							List<Code> mensCodes = newBathroomDetails.getMensRoom().getCodes();
							mensCodes.addAll(bathroomDetails.getMensRoom().getCodes());
			
							newBathroomDetails.getMensRoom().setCodes(mensCodes);
						}
						if(bathroomDetails.getMensRoom().getRatings()!=null){
							List<Double> mensRatings = newBathroomDetails.getMensRoom().getRatings();
							mensRatings.addAll(bathroomDetails.getMensRoom().getRatings());
			
							newBathroomDetails.getMensRoom().setRatings(mensRatings);
						}
					}
				}						
					
				if(newBathroomDetails.getWomensRoom()!=null){
					//Checks if the user is saying that there is a womens room (only updates this if it was previously false)
					if(!newBathroomDetails.getWomensRoom().isExists()){
						newBathroomDetails.getWomensRoom().setExists(bathroomDetails.getWomensRoom().isExists());
					}		
					//Checks if handicap accessibility was previously set to false, and updates it if the user changed it to true
					if(!newBathroomDetails.getWomensRoom().isHandicap()){
						newBathroomDetails.getWomensRoom().setHandicap(bathroomDetails.getWomensRoom().isHandicap());
					}		
					//Add womens room codes and ratings if there is a women's room
					if(newBathroomDetails.getWomensRoom().isExists()){
						if(bathroomDetails.getWomensRoom().getCodes()!=null){
							List<Code> womensCodes = newBathroomDetails.getWomensRoom().getCodes();
							womensCodes.addAll(bathroomDetails.getWomensRoom().getCodes());
		
							newBathroomDetails.getWomensRoom().setCodes(womensCodes);
						}
						if(bathroomDetails.getWomensRoom().getRatings()!=null){
							List<Double> womensRatings = newBathroomDetails.getWomensRoom().getRatings();
							womensRatings.addAll(bathroomDetails.getWomensRoom().getRatings());
		
							newBathroomDetails.getWomensRoom().setRatings(womensRatings);
						}
					}
				}	
				
				//makes sure that there is a gender neutral restroom
				if(newBathroomDetails.getGenderNeutral()!=null){
					//Checks if the user is saying that there is a gender neutral room (only updates this if it was previously false)
					if(!newBathroomDetails.getGenderNeutral().isExists()){
						newBathroomDetails.getGenderNeutral().setExists(bathroomDetails.getGenderNeutral().isExists());
					}		
					//Checks if handicap accessibility was previously set to false, and updates it if the user changed it to true
					if(!newBathroomDetails.getGenderNeutral().isHandicap()){
						newBathroomDetails.getGenderNeutral().setHandicap(bathroomDetails.getGenderNeutral().isHandicap());
					}
					//adds gender neutral codes and ratings if there is a gender neutral room
					if(newBathroomDetails.getGenderNeutral().isExists()){
						if(bathroomDetails.getGenderNeutral().getCodes()!=null){
							List<Code> genderneutralCodes = newBathroomDetails.getGenderNeutral().getCodes();
							genderneutralCodes.addAll(bathroomDetails.getGenderNeutral().getCodes());
		
							newBathroomDetails.getGenderNeutral().setCodes(genderneutralCodes);
						}
						if(bathroomDetails.getGenderNeutral().getRatings()!=null){
							List<Double> genderneutralRatings = newBathroomDetails.getGenderNeutral().getRatings();
							genderneutralRatings.addAll(bathroomDetails.getGenderNeutral().getRatings());
		
							newBathroomDetails.getGenderNeutral().setRatings(genderneutralRatings);
						}
					}	
				}
					
				bathroomDetailsRepo.deleteByPlaceId(bathroomDetails.getPlaceId());
			}
		}
		//If none exist, use bathroomDetails
		else{
			//if the mens room exists, save its details, otherwise add a blank one
			newBathroomDetails = new BathroomDetails();
			newBathroomDetails.setPlaceId(bathroomDetails.getPlaceId());
			if(bathroomDetails.getMensRoom().isExists()){
				Bathroom mensRoom = new Bathroom();
				
				mensRoom.setExists(bathroomDetails.getMensRoom().isExists());
				mensRoom.setHandicap(bathroomDetails.getMensRoom().isHandicap());
				mensRoom.setCodes(bathroomDetails.getMensRoom().getCodes());
				mensRoom.setRatings(bathroomDetails.getMensRoom().getRatings());
				
				newBathroomDetails.setMensRoom(mensRoom);
			}
			else{
				Bathroom bathroom = new Bathroom();
				bathroom.setCodes(new ArrayList<Code>());
				bathroom.setRatings(new ArrayList<Double>());
				newBathroomDetails.setMensRoom(bathroom);
			}
			
			//if womens room exists, save its details, otherwise add a blank one
			if(bathroomDetails.getWomensRoom().isExists()){
				Bathroom womensRoom = new Bathroom();
				
				womensRoom.setExists(bathroomDetails.getWomensRoom().isExists());
				womensRoom.setHandicap(bathroomDetails.getWomensRoom().isHandicap());
				womensRoom.setCodes(bathroomDetails.getWomensRoom().getCodes());
				womensRoom.setRatings(bathroomDetails.getWomensRoom().getRatings());
				
				newBathroomDetails.setWomensRoom(womensRoom);
			}
			else{
				Bathroom bathroom = new Bathroom();
				bathroom.setCodes(new ArrayList<Code>());
				bathroom.setRatings(new ArrayList<Double>());
				newBathroomDetails.setWomensRoom(bathroom);
			}
			
			//if gender neutral exists, add its details, otherwise add a blank one
			if(bathroomDetails.getGenderNeutral().isExists()){
				Bathroom genderNeutral = new Bathroom();
				
				genderNeutral.setExists(bathroomDetails.getGenderNeutral().isExists());
				genderNeutral.setHandicap(bathroomDetails.getGenderNeutral().isHandicap());
				genderNeutral.setCodes(bathroomDetails.getGenderNeutral().getCodes());
				genderNeutral.setRatings(bathroomDetails.getGenderNeutral().getRatings());
				
				newBathroomDetails.setGenderNeutral(genderNeutral);
			}
			else{
				Bathroom bathroom = new Bathroom();
				bathroom.setCodes(new ArrayList<Code>());
				bathroom.setRatings(new ArrayList<Double>());
				newBathroomDetails.setGenderNeutral(bathroom);
			}
		}
		//Save the bathroom details
		return bathroomDetailsRepo.save(newBathroomDetails);
	}
	
	
	@PutMapping("/vote/{type}")
	public BathroomAndPlaceDetails addPlaceDetails(@Valid @RequestBody BathroomAndPlaceDetails bathroomandplaceDetails, @PathVariable("type") String type) {

		//get bathroom details
		BathroomDetails bathroomDetails = bathroomDetailsRepo.findPlaceById(bathroomandplaceDetails.getBathroomDetails.getPlaceId());
		PlaceDetails placeDetails = new PlaceDetails();

		Bathroom mens = bathroomandplaceDetails.getBathroomDetails().getMensRoom();
		Bathroom womens = bathroomandplaceDetails.getBathroomDetails().getWomensRoom();
		Bathroom genderNeutral = bathroomandplaceDetails.getBathroomDetails().getGenderNeutralRoom();

		//get current codes from bathroom details
		List<Codes> mensCodes = bathroomDetails.getMensRoom.getCodes();
		List<Codes> womensCodes = bathroomDetails.getWomensRoom.getCodes();
		List<Codes> genderNeutralCodes = bathroomDetails.getGenderNeutralRoom.getCodes();

		//get codes from request body
		List<Codes> requestmensCodes = mens.getCodes();
		List<Codes> requestwomensCodes = womens.getCodes();
		List<Codes> requestgenderNeutralCodes = genderNeutral.getCodes();

		int vote = 0;
		if (type == "up") vote += 1;
		if (type == "down") vote += -1;
		for (Code code : mensCodes) {
			if (code.getVotes() != null) {
				if (requestmensCodes.contains(code)) {
					code.setVotes(code.getVotes() + vote);
				}
			} else {
				if (requestmensCodes.contains(code)) {
					code.setVotes(vote);
				}
			}
		}

		for (Code code : womensCodes) {
			if (code.getVotes() != null) {
				if (requestwomensCodes.contains(code)) {
					code.setVotes(code.getVotes() + vote);
				}
			} else {
				if (requestwomensCodes.contains(code)) {
					code.setVotes(vote);
				}
			}
		}

		for (Code code : genderNeutralCodes) {
			if (code.getVotes() != null) {
				if (requestgenderNeutralCodes.contains(code)) {
					code.setVotes(code.getVotes() + vote);
				}
			} else {
				if (requestgenderNeutralCodes.contains(code)) {
					code.setVotes(vote);
				}
			}
		}
}
