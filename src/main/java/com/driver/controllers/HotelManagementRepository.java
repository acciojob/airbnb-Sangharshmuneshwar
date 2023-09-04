package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Repository
public class HotelManagementRepository {

    HashMap<String,Hotel> hotelHashMap = new HashMap<>();
    HashMap<Integer,User> userHashMap = new HashMap<>();

    HashMap<Integer, List<Booking>> bookingByuser = new HashMap<>();
    HashMap<String,Booking> bookinksByBookingId = new HashMap<>();
    public String addHotel(Hotel hotel) {
        if (hotelHashMap.size()>0){
            for (String name : hotelHashMap.keySet()){
                if (name.equals(hotel.getHotelName()))
                    return "FAILURE";
            }
        }
        hotelHashMap.put(hotel.getHotelName(),hotel);
        return "SUCCESS";

    }

    public Integer addUser(User user) {
        if (userHashMap.containsKey(user.getaadharCardNo())){
            return 0;
        }
        userHashMap.put(user.getaadharCardNo(),user);
        return user.getaadharCardNo();
    }

    public String getHotelWithMostFacilities() {
        String ans = "";
        int maxHotel = 0;

        for (String name : hotelHashMap.keySet()){
            if (hotelHashMap.get(name).getFacilities().size()>maxHotel){
                maxHotel = hotelHashMap.get(name).getFacilities().size();
                ans = hotelHashMap.get(name).getHotelName();
            }
            else if (hotelHashMap.get(name).getFacilities().size()==maxHotel) {
                int result = ans.compareTo(name);

                if (result > 0) {
                    ans = name;
                }
            }

        }
        return ans;
    }




    public int bookARoom(Booking booking) {
        if (hotelHashMap.get(booking.getHotelName()).getAvailableRooms()<booking.getNoOfRooms()){
            return -1;
        }
        int totalAmountPaid = 0;
        totalAmountPaid = hotelHashMap.get(booking.getHotelName()).getPricePerNight()*booking.getNoOfRooms();
        booking.setAmountToBePaid(totalAmountPaid);
       Hotel hotel =  hotelHashMap.get(booking.getHotelName());
       //restting avilable rooms in hotel
       hotel.setAvailableRooms(hotel.getAvailableRooms()-booking.getNoOfRooms());
       hotelHashMap.put(hotel.getHotelName(),hotel);

       booking.setBookingId(UUID.randomUUID().toString());
        List<Booking> list = new ArrayList<>();
        if (bookingByuser.containsKey(booking.getBookingAadharCard())){
            list = bookingByuser.get(booking.getBookingAadharCard());
        }
        list.add(booking);
        bookingByuser.put(booking.getBookingAadharCard(),list);
        bookinksByBookingId.put(booking.getBookingId(),booking);

        return totalAmountPaid;
    }

    public int getBookings(Integer aadharCard) {
        if (bookingByuser.containsKey(aadharCard)){
            return bookingByuser.get(aadharCard).size();
        }
        return 0;
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {

        Hotel hotel = hotelHashMap.get(hotelName);

        if (hotel != null) {
            List<Facility> currentFacilities = hotel.getFacilities();

            // Iterate through newFacilities and add non-duplicate facilities to the hotel
            for (Facility facility : newFacilities) {
                if (!currentFacilities.contains(facility)) {
                    currentFacilities.add(facility);
                }
            }

            // Update the hotel's facilities in the database
            hotel.setFacilities(currentFacilities);
            hotelHashMap.put(hotelName, hotel);
        }

        return hotel; // Return the updated Hotel object
    }
}
