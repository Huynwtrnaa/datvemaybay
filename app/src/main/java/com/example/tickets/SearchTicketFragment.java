package com.example.tickets;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.flights.Flights;
import com.example.singiair.DBHelper;
import com.example.singiair.R;
import com.example.singiair.User;

import java.util.ArrayList;
import java.util.List;

public class SearchTicketFragment extends Fragment {

    private DBHelper MyDB;
    private List<Flights> allFlightsList;
    private List<Flights> uniqueFlightsList = new ArrayList<>();
    private ListTicketAdapter listTicketAdapter;
    String departureCountry, departureCity, flightForCountry, flightForCity, departureDateTime, flightReturnDate;
    int user_id;
    private Context context;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Get values from another bundle
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            departureCountry = bundle.getString("departureCountry");
            departureCity = bundle.getString("departureCity");
            departureDateTime = bundle.getString("departureDateTime");
            flightForCountry = bundle.getString("flightForCountry");
            flightForCity = bundle.getString("flightForCity");
            flightReturnDate = bundle.getString("fightReturnDate");
            user_id = bundle.getInt("user_id");
        }


        View mview = inflater.inflate(R.layout.fragment_search_ticket, container, false);

        ListView listView = (ListView) mview.findViewById(R.id.lv_ticket);
        allFlightsList = new ArrayList<>();
        MyDB = new DBHelper(getContext());

        listTicketAdapter = new ListTicketAdapter(getContext(),R.layout.row_ticket, uniqueFlightsList);
        listView.setAdapter(listTicketAdapter);

        updateAllFlightsList();

//        for (Flights flight : allFlightsList) {
//            Log.d("FlightDetails", "ID: " + flight.get_id());
//            Log.d("FlightDetails", "Departure Country: " + flight.getFlightDepartureCountry());
//            Log.d("FlightDetails", "Departure City: " + flight.getFlightDepartureCity());
//            Log.d("FlightDetails", "Arrival Country: " + flight.getFlightForCountry());
//            Log.d("FlightDetails", "Arrival City: " + flight.getFlightForCity());
//            Log.d("FlightDetails", "Departure Date and Time: " + flight.getFlightDepartureDateAndTime());
//            Log.d("FlightDetails", "Business Ticket Price: " + flight.getFlightBusinessTicket());
//            Log.d("FlightDetails", "Business Ticket Number: " + flight.getFlightBusinessTicketNumber());
//            Log.d("FlightDetails", "First Class Ticket Price: " + flight.getFlightFirstClassTicket());
//            Log.d("FlightDetails", "First Class Ticket Number: " + flight.getFlightFirstClassTicketNumber());
//        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CharSequence[] items = {"Hạng thường", "Hang thương gia"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), user_id);
                dialog.setTitle("Mua vé");
                dialog.setIcon(R.drawable.buy_ticket);
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            showDialogBuyTicket(user_id, uniqueFlightsList.get(position).get_id(), "businessTicket", uniqueFlightsList.get(position).getFlightDepartureCountry() + ", " + uniqueFlightsList.get(position).getFlightDepartureCity(),
                                    uniqueFlightsList.get(position).getFlightForCountry() + ", " + uniqueFlightsList.get(position).getFlightForCity(), uniqueFlightsList.get(position).getFlightDepartureDateAndTime(), uniqueFlightsList.get(position).getFlightBusinessTicket(),
                                    uniqueFlightsList.get(position).getFlightBusinessTicketNumber());
                        }
                        if (which == 1){
                            showDialogBuyTicket(user_id, uniqueFlightsList.get(position).get_id(), "firstClassTicket", uniqueFlightsList.get(position).getFlightDepartureCountry() + ", " + uniqueFlightsList.get(position).getFlightDepartureCity(),
                                uniqueFlightsList.get(position).getFlightForCountry() + ", " + uniqueFlightsList.get(position).getFlightForCity(), uniqueFlightsList.get(position).getFlightDepartureDateAndTime(), uniqueFlightsList.get(position).getFlightFirstClassTicket(),
                                    uniqueFlightsList.get(position).getFlightFirstClassTicketNumber());
                        }
                    }
                });
                dialog.show();
            }
        });
        return mview;
    }

    private void showDialogBuyTicket(int id_user, int id_flight, String classTicket, String departureFrom, String flightFor, String departureDateAndTime, double ticketPrice, int ticketNumberClass) {
        final EditText taskEditText = new EditText(getContext());
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext(), user_id);
        dialog.setTitle("Mua vé");
        dialog.setIcon(R.drawable.buy_ticket);
        dialog.setMessage("Nhập số lượng thẻ bạn muốn\nGiá một vế: $" + ticketPrice);
        taskEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        taskEditText.setText("1");
        dialog.setView(taskEditText);
        dialog.setPositiveButton("Mua", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String ticketNumber = String.valueOf(taskEditText.getText());
                if(ticketNumber.equals("") || ticketNumber.equals("0")){
                    Toast.makeText(getContext(), "Lỗi!\nBạn chưa nhập số vé!", Toast.LENGTH_SHORT).show();
                } else {
                    if(ticketNumberClass >= Integer.valueOf(ticketNumber)) {
                        User user = MyDB.getUserData(id_user);
                        if (user.getUserMoney() < (ticketPrice * Integer.valueOf(ticketNumber))) {
                            Toast.makeText(getContext(), "Lỗi!\nBạn không có đủ tiền trong tài khoản của bạn.", Toast.LENGTH_SHORT).show();
                        } else {
                            for (int i = 0; i < Integer.parseInt(ticketNumber); i++) {
                                MyDB.insertDataTicketReservation(id_user, id_flight, classTicket, departureFrom, flightFor, departureDateAndTime, null, null, null, null, false, ticketPrice);
                            }
                            MyDB.updateUserMoney(user.getUserId(), user.getUserMoney() - (ticketPrice * Integer.valueOf(ticketNumber)));
                            Toast.makeText(getContext(), "Bạn đã đặt chỗ thành công" + ticketNumber + " vé.\nBạn đã thanh toán tổng cộng: $" + (ticketPrice * Integer.valueOf(ticketNumber)), Toast.LENGTH_SHORT).show();
                            if (classTicket.equals("firstClassTicket")) {
                                MyDB.updateFirstClassTicketNumber(id_flight, (ticketNumberClass - Integer.valueOf(ticketNumber)));
                            } else {
                                MyDB.updateBusinessTicketNumber(id_flight, (ticketNumberClass - Integer.valueOf(ticketNumber)));
                            }
                            updateAllFlightsList();
                        }
                    } else {
                        Toast.makeText(getContext(), "Không đủ vé miễn phí để bán.\nSố vé miễn phí: " + ticketNumberClass, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        dialog.setNeutralButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void getSelectedFlights() {
        uniqueFlightsList.clear();

        Log.d("departureCountry", departureCountry);
        Log.d("departureCity", departureCity);
        Log.d("flightForCountry", flightForCountry);
        Log.d("flightForCity", flightForCity);
        Log.d("departureDateTime", departureDateTime);
        Log.d("flightReturnDate", flightReturnDate);

        for (Flights flight : allFlightsList) {
            boolean matches = true;

            if (!flight.getFlightDepartureCountry().equals(departureCountry)) {
                matches = false;
            }
            if (!flight.getFlightDepartureCity().equals(departureCity)) {
                matches = false;
            }

            if (!flight.getFlightForCountry().equals(flightForCountry)) {
                matches = false;
            }
            if (!flight.getFlightForCity().equals(flightForCity)) {
                matches = false;
            }

            Log.d("FlightCheck", "Flight ID: " + flight.get_id() + ", Matches: " + matches);

            if (matches) {
                uniqueFlightsList.add(flight);
            }
        }
    }


    private void updateAllFlightsList() {
        //get all flights from sqlite
        DBHelper MyDB = new DBHelper(getContext());
        Cursor cursor = MyDB.getData("SELECT * FROM flights");
        allFlightsList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String flightDepartureCountry = cursor.getString(1);
            String flightDepartureCity = cursor.getString(2);
            String flightDepartureDateAndTime = cursor.getString(3);
            String flightForCountry = cursor.getString(4);
            String flightForCity = cursor.getString(5);
            String flightForDateAndTime = cursor.getString(6);
            Double flightBusinessTicket = Double.valueOf(cursor.getString(7));
            Double flightFirstClassTicket = Double.valueOf(cursor.getString(8));
            int flightBusinessTicketNumber = Integer.valueOf(cursor.getString(9));
            int flightFirstClassTicketNumber = Integer.valueOf(cursor.getString(10));
            //add to list
            allFlightsList.add(new Flights(id, flightDepartureCountry, flightDepartureCity, flightForCountry, flightForCity, flightDepartureDateAndTime, flightForDateAndTime, flightBusinessTicket, flightFirstClassTicket, flightBusinessTicketNumber, flightFirstClassTicketNumber));
        }

        getSelectedFlights(); // get just selected flights (departure city, flight for city, date departure, return date)...
        listTicketAdapter.notifyDataSetChanged();
    }
}