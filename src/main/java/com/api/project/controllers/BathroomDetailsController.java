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
/*
	@PutMapping("/add-bathroom-details")
	public BathroomDetails addBathroomDetails( @Valid @RequestBody BathroomDetails bathroomDetails){
		//get bathroom details
		BathroomDetails newBathroomDetails = bathroomDetailsRepo.findByPlaceId(bathroomDetails.getPlaceId());

		if(newBathroomDetails!=null) {
			if (newBathroomDetails.getMensRoom() != null) {
				//Check if one already exists in the database for our given place
				if (newBathroomDetails != null) {
					//Checks if the user is saying that there is a mens room (only updates this if it was previously false)
					if (!newBathroomDetails.getMensRoom().isExists() && newBathroomDetails.getMensRoom() != null) {
						newBathroomDetails.getMensRoom().setExists(bathroomDetails.getMensRoom().isExists());
					}
					//Checks if handicap accessibility was previously set to false, and updates it if the user changed it to true
					if (!newBathroomDetails.getMensRoom().isHandicap()) {
						newBathroomDetails.getMensRoom().setHandicap(bathroomDetails.getMensRoom().isHandicap());
					}
					//If there is a mens room, add the codes and ratings. Can't have codes or ratings if there is no mens room
					if (newBathroomDetails.getMensRoom().isExists()) {
						if (bathroomDetails.getMensRoom().getCodes() != null) {
							List<Code> mensCodes = newBathroomDetails.getMensRoom().getCodes();
							mensCodes.addAll(bathroomDetails.getMensRoom().getCodes());

							newBathroomDetails.getMensRoom().setCodes(mensCodes);
						}
						if (bathroomDetails.getMensRoom().getRatings() != null) {
							List<Double> mensRatings = newBathroomDetails.getMensRoom().getRatings();
							mensRatings.addAll(bathroomDetails.getMensRoom().getRatings());

							newBathroomDetails.getMensRoom().setRatings(mensRatings);
						}
					}
				}
			}

			if (newBathroomDetails.getWomensRoom() != null) {
				//Checks if the user is saying that there is a womens room (only updates this if it was previously false)
				if (!newBathroomDetails.getWomensRoom().isExists()) {
					newBathroomDetails.getWomensRoom().setExists(bathroomDetails.getWomensRoom().isExists());
				}
				//Checks if handicap accessibility was previously set to false, and updates it if the user changed it to true
				if (!newBathroomDetails.getWomensRoom().isHandicap()) {
					newBathroomDetails.getWomensRoom().setHandicap(bathroomDetails.getWomensRoom().isHandicap());
				}
				//Add womens room codes and ratings if there is a women's room
				if (newBathroomDetails.getWomensRoom().isExists()) {
					if (bathroomDetails.getWomensRoom().getCodes() != null) {
						List<Code> womensCodes = newBathroomDetails.getWomensRoom().getCodes();
						womensCodes.addAll(bathroomDetails.getWomensRoom().getCodes());

						newBathroomDetails.getWomensRoom().setCodes(womensCodes);
					}
					if (bathroomDetails.getWomensRoom().getRatings() != null) {
						List<Double> womensRatings = newBathroomDetails.getWomensRoom().getRatings();
						womensRatings.addAll(bathroomDetails.getWomensRoom().getRatings());

						newBathroomDetails.getWomensRoom().setRatings(womensRatings);
					}
				}
			}

			//makes sure that there is a gender neutral restroom
			if (newBathroomDetails.getGenderNeutral() != null) {
				//Checks if the user is saying that there is a gender neutral room (only updates this if it was previously false)
				if (!newBathroomDetails.getGenderNeutral().isExists()) {
					newBathroomDetails.getGenderNeutral().setExists(bathroomDetails.getGenderNeutral().isExists());
				}
				//Checks if handicap accessibility was previously set to false, and updates it if the user changed it to true
				if (!newBathroomDetails.getGenderNeutral().isHandicap()) {
					newBathroomDetails.getGenderNeutral().setHandicap(bathroomDetails.getGenderNeutral().isHandicap());
				}
				//adds gender neutral codes and ratings if there is a gender neutral room
				if (newBathroomDetails.getGenderNeutral().isExists()) {
					if (bathroomDetails.getGenderNeutral().getCodes() != null) {
						List<Code> genderneutralCodes = newBathroomDetails.getGenderNeutral().getCodes();
						genderneutralCodes.addAll(bathroomDetails.getGenderNeutral().getCodes());

						newBathroomDetails.getGenderNeutral().setCodes(genderneutralCodes);
					}
					if (bathroomDetails.getGenderNeutral().getRatings() != null) {
						List<Double> genderneutralRatings = newBathroomDetails.getGenderNeutral().getRatings();
						genderneutralRatings.addAll(bathroomDetails.getGenderNeutral().getRatings());

						newBathroomDetails.getGenderNeutral().setRatings(genderneutralRatings);
					}
				}
			}

			bathroomDetailsRepo.deleteByPlaceId(bathroomDetails.getPlaceId());
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
	}*/
	
	@PutMapping("/add-bathroom-details")
	public BathroomDetails addBathroomDetails( @Valid @RequestBody BathroomDetails bathroomDetails){
		//get bathroom details
		BathroomDetails newBathroomDetails = bathroomDetailsRepo.findByPlaceId(bathroomDetails.getPlaceId());
		
		//Handles null pointers in case the input bathroom isn't already in the DB
		if(newBathroomDetails==null){
			newBathroomDetails = new BathroomDetails();
		}
		
		//Make sure we aren't updating any empty data
		if(bathroomDetails!=null){
			if(bathroomDetails.getMensRoom()!=null){
				Bathroom newMens = newBathroomDetails.getMensRoom();
				
				//Handles null pointers
				if(newMens==null){
					newMens = new Bathroom();
				}
				
				Bathroom mens = bathroomDetails.getMensRoom();
				
				//Only updates if they are changing from false to true
				if(mens.isExists()){
					newMens.setExists(true);
				}
				//Only updates if changing from false to true
				if(mens.isHandicap()){
					newMens.setHandicap(true);
				}
				
				//Adds the input codes to the database if there are any
				if(mens.getCodes()!=null){
					List<Code> codes = newMens.getCodes();
					
					//handles nulls
					if(codes==null){
						codes = new ArrayList<>();
					}
					
					codes.addAll(mens.getCodes());
					
					newMens.setCodes(codes);
				}
				if(mens.getRatings()!=null){
					List<Double> ratings = newMens.getRatings();
					
					//Handles null pointers
					if(ratings==null){
						ratings = new ArrayList<>();
					}
					ratings.addAll(mens.getRatings());
					
					newMens.setRatings(ratings);
				}
				
				newBathroomDetails.setMensRoom(newMens);
			}
			if(bathroomDetails.getWomensRoom()!=null){
				Bathroom newWomens = newBathroomDetails.getWomensRoom();
				
				//Handles null pointers
				if(newWomens==null){
					newWomens = new Bathroom();
				}
				
				Bathroom womens = bathroomDetails.getWomensRoom();
				
				//Only updates if they are changing from false to true
				if(womens.isExists()){
					newWomens.setExists(true);
				}
				//Only updates if changing from false to true
				if(womens.isHandicap()){
					newWomens.setHandicap(true);
				}
				
				//Adds the input codes to the database if there are any
				if(womens.getCodes()!=null){
					List<Code> codes = newWomens.getCodes();
					
					//handles nulls
					if(codes==null){
						codes = new ArrayList<>();
					}
					
					codes.addAll(womens.getCodes());
					
					newWomens.setCodes(codes);
				}
				if(womens.getRatings()!=null){
					List<Double> ratings = newWomens.getRatings();
					
					//Handles null pointers
					if(ratings==null){
						ratings = new ArrayList<>();
					}
					ratings.addAll(womens.getRatings());
					
					newWomens.setRatings(ratings);
				}
				
				newBathroomDetails.setWomensRoom(newWomens);
			}
			if(bathroomDetails.getGenderNeutral()!=null){
				Bathroom newGenderNeutral = newBathroomDetails.getGenderNeutral();
				
				//Handles null pointers
				if(newGenderNeutral==null){
					newGenderNeutral = new Bathroom();
				}
				
				Bathroom genderNeutral = bathroomDetails.getGenderNeutral();
				
				//Only updates if they are changing from false to true
				if(genderNeutral.isExists()){
					newGenderNeutral.setExists(true);
				}
				//Only updates if changing from false to true
				if(genderNeutral.isHandicap()){
					newGenderNeutral.setHandicap(true);
				}
				
				//Adds the input codes to the database if there are any
				if(genderNeutral.getCodes()!=null){
					List<Code> codes = newGenderNeutral.getCodes();
					
					//handles nulls
					if(codes==null){
						codes = new ArrayList<>();
					}
					
					codes.addAll(genderNeutral.getCodes());
					
					newGenderNeutral.setCodes(codes);
				}
				if(genderNeutral.getRatings()!=null){
					List<Double> ratings = newGenderNeutral.getRatings();
					
					//Handles null pointers
					if(ratings==null){
						ratings = new ArrayList<>();
					}
					ratings.addAll(genderNeutral.getRatings());
					
					newGenderNeutral.setRatings(ratings);
				}
				
				newBathroomDetails.setGenderNeutral(newGenderNeutral);
			}
		}
		bathroomDetailsRepo.deleteByPlaceId(newBathroomDetails.getPlaceId());
		//Save the bathroom details
		return bathroomDetailsRepo.save(newBathroomDetails);
	}

	@PutMapping("/vote/{type}")
	public BathroomDetails addVotes(@Valid @RequestBody BathroomDetails bathroomDetails, @PathVariable("type") String type) {

		//get bathroom details
		BathroomDetails newBathroomDetails = bathroomDetailsRepo.findByPlaceId(bathroomDetails.getPlaceId());

		//get current codes from bathroom details
		List<Code> mensCodes = newBathroomDetails.getMensRoom().getCodes();
		List<Code> womensCodes = newBathroomDetails.getWomensRoom().getCodes();
		List<Code> genderNeutralCodes = newBathroomDetails.getGenderNeutral().getCodes();

		//if it is an upvote or downvote
		int vote = 0;
		if (type.equals("up")) vote += 1;
		if (type.equals("down")) vote += -1;

		//get codes to update from request body for mens
		if (bathroomDetails.getMensRoom() != null) {
			if(bathroomDetails.getMensRoom().getCodes()!=null) {
				Bathroom mens = bathroomDetails.getMensRoom();
				List<Code> requestmensCodes = mens.getCodes();
				for (Code code : mensCodes) {
					for (Code code2 : requestmensCodes) {
						if (code.getNumber().equals(code2.getNumber()) && code.getVotes() == code2.getVotes()) {
							int i = mensCodes.indexOf(code);
							code.setVotes(code.getVotes() + vote);
							mensCodes.set(i, code);
						}
					}
				}
			}
		}

		//get codes to update from request body for womens
		if (bathroomDetails.getWomensRoom() != null) {
			if(bathroomDetails.getWomensRoom().getCodes()!=null) {
				Bathroom womens = bathroomDetails.getWomensRoom();
				List<Code> requestwomensCodes = womens.getCodes();
				for (Code code : womensCodes) {
					for (Code code2 : requestwomensCodes) {
						if (code.getNumber().equals(code2.getNumber()) && code.getVotes() == code2.getVotes()) {
							int i = womensCodes.indexOf(code);
							code.setVotes(code.getVotes() + vote);
							womensCodes.set(i, code);
						}
					}
				}
			}
		}

		//get codes to update from request body for gender neutral
		if (bathroomDetails.getGenderNeutral() != null) {
			if(bathroomDetails.getGenderNeutral().getCodes()!=null) {
				Bathroom genderNeutral = bathroomDetails.getGenderNeutral();
				List<Code> requestgenderNeutralCodes = genderNeutral.getCodes();
				for (Code code : genderNeutralCodes) {
					for (Code code2 : requestgenderNeutralCodes) {
						if (code.getNumber().equals(code2.getNumber()) && code.getVotes() == code2.getVotes()) {
							int i = genderNeutralCodes.indexOf(code);
							code.setVotes(code.getVotes() + vote);
							genderNeutralCodes.set(i, code);
						}
					}
				}
			}
		}

		newBathroomDetails.getMensRoom().setCodes(mensCodes);
		newBathroomDetails.getWomensRoom().setCodes(womensCodes);
		newBathroomDetails.getGenderNeutral().setCodes(genderNeutralCodes);

		bathroomDetailsRepo.deleteByPlaceId(bathroomDetails.getPlaceId());
		return bathroomDetailsRepo.save(newBathroomDetails);
	}

	@PutMapping("/replace-bathroom-details")
	public BathroomDetails replaceBathroomDetails( @Valid @RequestBody BathroomDetails bathroomDetails) {
		bathroomDetailsRepo.deleteByPlaceId(bathroomDetails.getPlaceId());
		return bathroomDetailsRepo.save(bathroomDetails);
	}
}
