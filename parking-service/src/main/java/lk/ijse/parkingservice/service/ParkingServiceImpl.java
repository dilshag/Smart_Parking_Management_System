package lk.ijse.parkingservice.service;


import lk.ijse.parkingservice.config.RandomNumberGenarator;
import lk.ijse.parkingservice.dto.ParkingDTO;
import lk.ijse.parkingservice.entity.Parking;
import lk.ijse.parkingservice.repo.ParkingRepo;
import lk.ijse.parkingservice.repo.UserRepository;
import lk.ijse.parkingservice.util.VarList;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ParkingServiceImpl implements ParkingService{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParkingRepo parkingRepo;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public int saveParkingPlace(ParkingDTO parkingDTO) {
        System.out.println("saveParkingPlace");

        try {
            // Step 1: Validate input fields
            if (parkingDTO.getEmail() == null || parkingDTO.getLocation() == null || parkingDTO.getCity() == null) {
                System.out.println("❌ Validation failed: Email, Location, or City is null");
                return VarList.Not_Found; // Or create a new VarList value like Validation_Failed
            }

            // Step 2: Normalize input values
            String email = parkingDTO.getEmail().toLowerCase();
            String location = parkingDTO.getLocation().toLowerCase();
            String city = parkingDTO.getCity().toLowerCase();

            // Step 3: Check if user exists
            if (userRepository.existsUserByEmail(email)) {
                // Step 4: Check if parking location already exists
                if (parkingRepo.existsParkingByLocation(location)) {
                    System.out.println("⚠️ Parking location already exists: " + location);
                    return VarList.All_Ready_Added;
                }

                // Step 5: Map DTO to entity
                Parking parking = modelMapper.map(parkingDTO, Parking.class);
                int code = Integer.parseInt(RandomNumberGenarator.generateCode(6));

                // Step 6: Set processed values
                parking.setLocationCode(code);
                parking.setAvailable(true);
                parking.setEmail(email);
                parking.setLocation(location);
                parking.setCity(city);

                // Step 7: Save to DB
                parkingRepo.save(parking);
                System.out.println("✅ Parking saved successfully");
                return VarList.Created;
            } else {
                System.out.println("❌ User not found: " + email);
                return VarList.Not_Found;
            }
        } catch (Exception e) {
            System.out.println("❌ Exception while saving parking: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


/*

@Override
public int saveParkingPlace(ParkingDTO parkingDTO){
        System.out.println("saveParkingPlace");
        try{
            if(userRepository.existsUserByEmail(parkingDTO.getEmail().toLowerCase())){
                if(parkingRepo.existsParkingByLocation(parkingDTO.getLocation())){
                    return VarList.All_Ready_Added;
                }
                Parking parking = modelMapper.map(parkingDTO, Parking.class);
                int code = Integer.parseInt(RandomNumberGenarator.generateCode(6));
                parking.setLocationCode(code);
                String email = parking.getEmail().toLowerCase();
                String location = parking.getLocation().toLowerCase();
                String city = parking.getCity().toLowerCase();
                parking.setAvailable(true);
                parking.setEmail(email);
                parking.setLocation(location);
                parking.setCity(city);
                parkingRepo.save(parking);
                return VarList.Created;
            }
            return VarList.Not_Found;
        }catch (Exception e){
            System.out.println("Exception"+e.getMessage());
            throw  new RuntimeException();
        }
        }

*/

        @Override
        public int updateParkingPlace(ParkingDTO parkingDTO){
        System.out.println("updateParkingPlace  "+parkingDTO);
        try {
            if(userRepository.existsUserByEmail(parkingDTO.getEmail().toLowerCase())){
                if(parkingRepo.existsParkingById(parkingDTO.getId())){
                    Parking parking = parkingRepo.findById(parkingDTO.getId()).get();
                    parking.setAvailable(true);
                    parking.setPayAmount(parkingDTO.getPayAmount());
                    parking.setEmail(parkingDTO.getEmail().toLowerCase());
                    parking.setLocation(parkingDTO.getLocation().toLowerCase());
                    parking.setCity(parkingDTO.getCity().toLowerCase());
                    parkingRepo.save(parking);
                    return VarList.Created;
                }
            }
            return VarList.Not_Found;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

@Override
public int ReservationUpdateParkingPlace(String Location){
        System.out.println("Booking location "+Location);
        try{
            if(parkingRepo.existsParkingByLocation(Location.toLowerCase())){
                Parking parking = parkingRepo.findByLocation(Location.toLowerCase());
                if(parking.isAvailable()){
                    parking.setAvailable(false);
                    parkingRepo.save(parking);
                    return VarList.Created;
                }
              return VarList.Not_Found;
            }
            return VarList.Not_Found;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public int deleteParkingPlace(UUID id, String email, int LocationCode){

        System.out.println("deleteParkingPlace");
        try{
            if(userRepository.existsUserByEmail(email.toLowerCase())){
                if(parkingRepo.existsById(id) || parkingRepo.existsParkingByLocationCode(LocationCode)){
                    Parking parking = parkingRepo.findById(id).get();
                    parkingRepo.delete(parking);
                    return VarList.OK;
                }
            }
            return VarList.Not_Found;
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    @Override
    public List<ParkingDTO> getParkingPlaceCity(String city){
        System.out.println("getParkingPlaceCity");
        if(parkingRepo.existsParkingByCity(city.toLowerCase())){
            List<Parking> parkingList = parkingRepo.findAllByCity(city);
            List<Parking> parkingList2 = new ArrayList<>();
            for(Parking parking:parkingList){
                if(parking.isAvailable()){
                    parkingList2.add(parking);
                }
            }
            return modelMapper.map(parkingList2,new TypeToken<List<ParkingDTO>>(){}.getType());
        }
        return null;

    }

    @Override
    public List<ParkingDTO> getAllParkingPlace(){
        List<Parking> parkingList = parkingRepo.findAll();
        return modelMapper.map(parkingList,new TypeToken<List<ParkingDTO>>(){}.getType());
    }

}
