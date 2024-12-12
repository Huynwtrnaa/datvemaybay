package com.example.singiair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.profile.ProfileActivity;
import com.example.tickets.ListTicketReservationActivity;
import com.example.tickets.SearchFlightFragment;
import com.example.tickets.TicketReservation;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private User user;
    private DrawerLayout drawer;
    TextView nav_header_user_name_surname, nav_header_user_balance;
    ImageView nav_header_user_image;
    NavigationView navigationView;

    private List<TicketReservation> allTicketReservationList = new ArrayList<>();
    private List<TicketReservation> uniqueTicketReservationList = new ArrayList<>();
    private List<TicketReservation> uniqueCheckInTicket = new ArrayList<>();

    //Instance database
    DBHelper DB = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0); // Selected header items (image, name, surname and email)
        Menu menu_nav_view = navigationView.getMenu(); // Selected menu items

        nav_header_user_name_surname = (TextView) header.findViewById(R.id.nav_header_name_surname);
        nav_header_user_balance = (TextView) header.findViewById(R.id.nav_header_balance);

        nav_header_user_name_surname.setText(user.getUserName() + " " + user.getUserSurname());
        nav_header_user_balance.setText("Số dư tài khoản: $" + String.format("%.2f", user.getUserMoney()));

        nav_header_user_image = (ImageView) header.findViewById(R.id.nav_header_image);

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment(this, user.getUserId())).commit();
            navigationView.setCheckedItem(R.id.home_fragment);
        }

    }

    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.home_fragment:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment(this, user.getUserId())).commit();
                navigationView.setCheckedItem(R.id.home_fragment);
                break;

            case R.id.flights_search:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SearchFlightFragment(this, user.getUserId())).commit();
                navigationView.setCheckedItem(R.id.flights_search);
                break;

            case R.id.my_profile:
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                user = DB.getUserData(user.getUserId());
                intent.putExtra("user", user);
                startActivity(intent);
                break;

            case R.id.payment:
                CharSequence[] items = {"Thẻ tín dụng", "Phiếu thanh toán"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
                dialog.setTitle("Thanh toán");
                dialog.setIcon(R.drawable.list_icon);
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            showDialogCreditCard(user.getUserId());
                        }
                        if (which == 1){
                            showDialogPaymentSlip(user.getUserId());
                        }
                    }
                });
                dialog.show();
                break;

            case R.id.logout:
                DB.updateUserIsLogged(user.getUserId(), false);
                SharedPreferences pref = getSharedPreferences("MyUserPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();

                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                break;


            case R.id.my_reservation_ticket:
                updateAllTicketReservationList();
                if(uniqueTicketReservationList.size() == 0){
                    Toast.makeText(getApplicationContext(), "Bạn chưa đặt chuyến bay.", Toast.LENGTH_SHORT).show();
                    navigationView.setCheckedItem(R.id.home_fragment);
                } else {
                    intent = new Intent(HomeActivity.this, ListTicketReservationActivity.class);
                    user = DB.getUserData(user.getUserId());
                    intent.putExtra("user", user);
                    startActivity(intent);
                    finish();
                }
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showDialogPaymentSlip(int id_user) {
        final EditText taskEditText = new EditText(HomeActivity.this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this, id_user);
        dialog.setTitle("Thanh toán - Phiếu thanh toán");
        dialog.setIcon(R.drawable.money_dollar);
        dialog.setMessage("Nhập số tiền bạn muốn thanh toán:");
        taskEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialog.setView(taskEditText);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String money = String.valueOf(taskEditText.getText());
                if(money.equals("") || money.equals("0")){
                    Toast.makeText(HomeActivity.this, "Lỗi!\nBạn chưa nhập số!", Toast.LENGTH_SHORT).show();
                } else {
                    // Use the Builder class for convenient dialog construction
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setMessage("Payer:" + "\n" +
                                        user.getUserName() + " " + user.getUserSurname() + "\n" +"Địa chỉ của bạn" + "\n\n" +
                                        "Mục đích thanh toán:" + "\n" +
                                        "Thanh toán tiền vào tài khoản" + "\n\n" +
                                        "Nguời nhận:" + "\n" + "SingiAir d.o.o Belgrade" + "\n\n" +
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

        dialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void refreshMoneyInHeader(double user_current_money){
        nav_header_user_balance.setText("$" + String.format("%.2f", (user_current_money)));
    }

    private void showDialogCreditCard(int id_user) {
        final EditText taskEditText = new EditText(HomeActivity.this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this, id_user);
        dialog.setTitle("Thanh toán - Thẻ tín dụng");
        dialog.setIcon(R.drawable.money_dollar);
        dialog.setMessage("Nhập số tiền bạn muốn thanh toán\n:");
        taskEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        dialog.setView(taskEditText);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String money = String.valueOf(taskEditText.getText());
                if(money.equals("") || money.equals("0")){
                    Toast.makeText(HomeActivity.this, "Lỗi!\nBạn chưa nhập số \n!", Toast.LENGTH_SHORT).show();
                } else {
                    DB.updateUserMoney(id_user, (user.getUserMoney() + Double.valueOf(money)));
                    Toast.makeText(HomeActivity.this, "Bạn đã thanh toán thành công $" + money + " vào tài khoản.", Toast.LENGTH_LONG).show();
                    nav_header_user_balance.setText("$" + String.format("%.2f", (user.getUserMoney() + Double.valueOf(money))));
                }
            }
        });

        dialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void updateAllTicketReservationList() {
        DBHelper MyDB = new DBHelper(getApplicationContext());
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