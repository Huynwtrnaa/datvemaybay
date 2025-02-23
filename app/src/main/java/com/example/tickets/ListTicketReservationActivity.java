package com.example.tickets;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.reservation.TicketA;
import com.example.singiair.DBHelper;
import com.example.singiair.HomeActivity;
import com.example.singiair.R;
import com.example.singiair.User;

import java.util.ArrayList;
import java.util.List;

public class ListTicketReservationActivity extends AppCompatActivity {

    private User user;
    private ListTicketReservationActivityAdapter listTicketReservationActivityAdapter;
    private List<TicketReservation> allTicketReservationList = new ArrayList<>();
    private List<TicketReservation> uniqueTicketReservationList = new ArrayList<>();
    DBHelper MyDB = new DBHelper(this);
    private boolean checkIn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ticket_reservation);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
//        checkIn = (boolean) intent.getSerializableExtra("checkIn") ? true : false;
        if(intent.hasExtra("checkIn")){
            checkIn = (boolean) intent.getSerializableExtra("checkIn");
        }
        ListView listView = findViewById(R.id.list_view_ticket_reservation);
        allTicketReservationList = new ArrayList<>();

        listTicketReservationActivityAdapter = new ListTicketReservationActivityAdapter(ListTicketReservationActivity.this,R.layout.row_ticket, uniqueTicketReservationList);
        listView.setAdapter(listTicketReservationActivityAdapter);

        updateAllTicketReservationList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(uniqueTicketReservationList.get(position).getTicketReservationCheckIn() == true){
                    Intent intent = new Intent(ListTicketReservationActivity.this, TicketA.class);
                    intent.putExtra("user", user);
                    intent.putExtra("current_position_in_list", position);
                    startActivity(intent);
                    finish();
                } else {
                    CharSequence[] items = {"Check in vé", "Hủy đặt vé"};
                    AlertDialog.Builder dialog = new AlertDialog.Builder(ListTicketReservationActivity.this);
                    dialog.setTitle("Đặt vé");
                    dialog.setIcon(R.drawable.list_icon);
                    dialog.setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0){
                                Intent intent = new Intent(ListTicketReservationActivity.this, CheckInTicketActivity.class);
                                intent.putExtra("ticketReservationID", uniqueTicketReservationList.get(position).getTicketReservationID());
                                intent.putExtra("user", user);
                                startActivity(intent);
                                finish();
                            }
                            if (which == 1){
                                showDialogCancelTicket(uniqueTicketReservationList.get(position).getTicketReservationID());
                            }
                        }
                    });
                    dialog.show();
                }
            }
        });
    }


    private void showDialogCancelTicket(int id) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ListTicketReservationActivity.this);
        TicketReservation ticketReservation = MyDB.getTicketReservationData(id);
        double refunds = ticketReservation.getTicketPrice() / 2;
        dialog.setTitle("Hủy đặt vé!");
        dialog.setIcon(R.drawable.list_icon);
        dialog.setMessage("Bạn có chắc chắn muốn hủy đặt vé không?\n\nNếu hủy đặt vé, bạn sẽ được hoàn lại 50% giá vé.\n\nSố tiền hoàn lại: "+ refunds);
        DBHelper MyDB = new DBHelper(ListTicketReservationActivity.this);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Delete reservation
                try{
                    MyDB.deleteReservationTicket(id);
                    MyDB.updateUserMoney(user.getUserId(), (user.getUserMoney() + refunds));
                    Toast.makeText(ListTicketReservationActivity.this, "Bạn đã hủy vé thành công.", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Log.e("Lỗi!", e.getMessage());
                }
                updateAllTicketReservationList();
            }
        });

        dialog.setNeutralButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void updateAllTicketReservationList() {
        DBHelper MyDB = new DBHelper(ListTicketReservationActivity.this);
        Cursor cursor = MyDB.getData("SELECT * FROM ticketsReserved WHERE id_user = " + user.getUserId() + " AND checkIn = " + (checkIn ? 1 : 0) + " ORDER BY id DESC");
        allTicketReservationList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int id_user = cursor.getInt(1);
            int id_flight = cursor.getInt(2);
            String ticket_class = cursor.getString(3);
            String departureFrom = cursor.getString(4);
            String flightFor = cursor.getString(5);
            String dateAndTimeDeparture = cursor.getString(6);
            String check_in_user_name = cursor.getString(7);
            String check_in_user_surname = cursor.getString(8);
            String check_in_user_passport_number = cursor.getString(9);
            String check_in_user_nationality = cursor.getString(10);
            boolean checkIn = cursor.getInt(11) > 0;
            double ticketPrice = cursor.getDouble(12);
            //add to list
            allTicketReservationList.add(new TicketReservation(id, id_user, id_flight, ticket_class, departureFrom, flightFor, dateAndTimeDeparture, check_in_user_name, check_in_user_surname, check_in_user_passport_number, check_in_user_nationality, checkIn, ticketPrice));
        }
        getSelectedReservation();
        listTicketReservationActivityAdapter.notifyDataSetChanged();
    }

    private void getSelectedReservation() {
        uniqueTicketReservationList.clear();
        for (int i = 0; i < allTicketReservationList.size(); i++) {
            if (allTicketReservationList.get(i).getTicketReservationIDUser() == user.getUserId() && allTicketReservationList.get(i).getTicketReservationCheckIn() == checkIn) { // Get equals departure country and city
                uniqueTicketReservationList.add(allTicketReservationList.get(i));
            }
        }
        if(uniqueTicketReservationList.size() == 0){
            Intent intent = new Intent(ListTicketReservationActivity.this, HomeActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        }
    }

    public void onBackPressed(){
        Intent intent = new Intent(ListTicketReservationActivity.this, HomeActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }
}