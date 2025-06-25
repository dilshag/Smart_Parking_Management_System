package lk.ijse.parkingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ParkingDTO {
    private UUID id;
    private String location;
    private int locationCode;
    private String city;
    private boolean available;
    private String email;
    private int payAmount;

}
