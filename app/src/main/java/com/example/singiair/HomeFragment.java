package com.example.singiair;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.profile.ProfileActivity;
import com.example.reservation.TicketA;
import com.example.tickets.ListTicketReservationActivity;
import com.example.tickets.SearchFlightFragment;
import com.example.tickets.TicketReservation;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Context context;
    private int User_id;
    User user;

    TextView home_activity_name_and_surname, home_activity_money;
    ImageView home_activity_profile_image, home_activity_profile_button, home_activity_reserved_tickets_button,  home_activity_payment_button, home_activity_search_flight_button,home_activity_ticketa;

    private List<TicketReservation> allTicketReservationList = new ArrayList<>();
    private List<TicketReservation> uniqueTicketReservationList = new ArrayList<>();
    private List<TicketReservation> uniqueCheckInTicket = new ArrayList<>();
    private SharedPreferences sharedpreferences;
    public HomeFragment(Context context, int user_id){
        this.context=context;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        //Instance database
        sharedpreferences = requireActivity().getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
        DBHelper DB = new DBHelper(context);
        user = DB.getUserData(sharedpreferences.getInt("user_id", 0));

        home_activity_name_and_surname = (TextView) rootView.findViewById(R.id.home_activity_name_and_surname);
        home_activity_name_and_surname.setText(user.getUserName() + " " + user.getUserSurname());

        home_activity_money = (TextView) rootView.findViewById(R.id.home_activity_money);
        home_activity_money.setText("$" + String.format("%.2f", user.getUserMoney()));

        home_activity_profile_image = (ImageView) rootView.findViewById(R.id.home_activity_profile_image);


        home_activity_profile_button = (ImageView) rootView.findViewById(R.id.home_activity_profile_button);
        home_activity_reserved_tickets_button = (ImageView) rootView.findViewById(R.id.home_activity_reserved_tickets_button);
        home_activity_payment_button = (ImageView) rootView.findViewById(R.id.home_activity_payment_button);
        home_activity_search_flight_button = (ImageView) rootView.findViewById(R.id.home_activity_search_flight_button);
        home_activity_ticketa = (ImageView) rootView.findViewById(R.id.home_activity_ticketa);
        updateAllTicketReservationList();

        home_activity_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                user = DB.getUserData(user.getUserId());
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        home_activity_reserved_tickets_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAllTicketReservationList();
                if(uniqueTicketReservationList.size() == 0){
                    Toast.makeText(context, "Bạn chưa đặt chuyến bay.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(context, ListTicketReservationActivity.class);
                    user = DB.getUserData(user.getUserId());
                    intent.putExtra("user", user);
                    intent.putExtra("checkIn", false);
                    startActivity(intent);
                }
            }
        });

        home_activity_ticketa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAllTicketReservationList();
                if(uniqueCheckInTicket.size() == 0){
                    Toast.makeText(context, "Bạn chưa check-in chuyến bay.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(context, ListTicketReservationActivity.class);
                    user = DB.getUserData(user.getUserId());
                    intent.putExtra("user", user);
                    intent.putExtra("checkIn", true);
                    startActivity(intent);
                }
            }
        });

        home_activity_payment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = new HomeActivity();
                CharSequence[] items = {"Thẻ tín dụng"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("Thanh toán");
                dialog.setIcon(R.drawable.list_icon);
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            final EditText taskEditText = new EditText(context);
                            AlertDialog.Builder dialogPayment = new AlertDialog.Builder(context, user.getUserId());
                            dialogPayment.setTitle("Thanh toán - Thẻ tín dụng");
                            dialogPayment.setIcon(R.drawable.money_dollar);
                            dialogPayment.setMessage("Nhập số tiền bạn muốn thanh toán:");
                            taskEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            dialogPayment.setView(taskEditText);
                            dialogPayment.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String money = String.valueOf(taskEditText.getText());
                                    if(money.equals("") || money.equals("0")){
                                        Toast.makeText(context, "Lỗi!\nBạn chưa nhập số tiền thanh toán!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        DB.updateUserMoney(user.getUserId(), (user.getUserMoney() + Double.valueOf(money)));
                                        Toast.makeText(context, "Bạn đã thanh toán thành công $" + money + " vào tài khoản  .", Toast.LENGTH_LONG).show();
                                        home_activity_money.setText("$" + String.format("%.2f", (user.getUserMoney() + Double.valueOf(money))));
                                        ((HomeActivity)getActivity()).refreshMoneyInHeader(user.getUserMoney() + Double.valueOf(money));

                                    }
                                }
                            });

                            dialogPayment.setNeutralButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            dialogPayment.show();
                        }
                        if (which == 1){
                            final EditText taskEditText = new EditText(context);
                            AlertDialog.Builder dialogPayment = new AlertDialog.Builder(context, user.getUserId());
                            dialogPayment.setTitle("Thanh toán - Phiếu thanh toán");
                            dialogPayment.setIcon(R.drawable.money_dollar);
                            dialogPayment.setMessage("Nhập số tiền bạn muốn thanh toán:");
                            taskEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            dialogPayment.setView(taskEditText);
                            dialogPayment.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String money = String.valueOf(taskEditText.getText());
                                    if(money.equals("") || money.equals("0")){
                                        Toast.makeText(context, "Lỗi!\\nBạn chưa nhập số tiền thanh toán!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Use the Builder class for convenient dialog construction
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("Người thanh toán:" + "\n" +
                                                user.getUserName() + " " + user.getUserSurname() + "\n" +"Địa chỉ của bạn" + "\n\n" +
                                                "Mục đích thanh toán:" + "\n" +
                                                "Thanh toán tiền vào tài khoản" + "\n\n" +
                                                "Người nhận:" + "\n" + "SingiAir d.o.o Belgrade" + "\n\n" +
                                                "Số tiền:" + "\n" + money + "\n\n" +
                                                "Tài khoản người nhận:" + "\n" + "192-3241123311-22" + "\n\n" +
                                                "Số tham chiếu (BẮT BUỘC): " + user.getUserId())
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        builder.show();
                                    }
                                }
                            });

                            dialogPayment.setNeutralButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            dialogPayment.show();
                        }
                    }
                });
                dialog.show();
            }
        });

        home_activity_search_flight_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container,
                        new SearchFlightFragment(context, user.getUserId())).commit();
            }
        });
        return rootView;
    }

    private void updateAllTicketReservationList() {
        DBHelper MyDB = new DBHelper(context);
        Cursor cursor = MyDB.getData("SELECT * FROM ticketsReserved");
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
        getSelectedCheckInTicket();
    }

    private void getSelectedReservation() {
        uniqueTicketReservationList.clear();
        for (int i = 0; i < allTicketReservationList.size(); i++) {
            if (allTicketReservationList.get(i).getTicketReservationIDUser() == user.getUserId() && allTicketReservationList.get(i).getTicketReservationCheckIn() == false) { // Get equals departure country and city
                uniqueTicketReservationList.add(allTicketReservationList.get(i));
            }
        }
    }

    private void getSelectedCheckInTicket() {
        uniqueCheckInTicket.clear();
        for (int i = 0; i < allTicketReservationList.size(); i++) {
            if ((allTicketReservationList.get(i).getTicketReservationIDUser() == user.getUserId()) && (allTicketReservationList.get(i).getTicketReservationCheckIn() == true)) { // Get equals departure country and city
                uniqueCheckInTicket.add(allTicketReservationList.get(i));
            }
        }
    }
}