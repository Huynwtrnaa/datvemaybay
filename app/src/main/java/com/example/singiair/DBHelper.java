package com.example.singiair;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.flights.Flights;
import com.example.tickets.TicketReservation;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DBNAME = "SingiAir.db";

    // Table names
    private static final String TABLE_USER = "users";
    private static final String TABLE_FLIGHT = "flights";
    private static final String TABLE_TICKETS = "ticketsReserved";

    // User table columns
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_SURNAME = "surname";
    private static final String COLUMN_USER_USERNAME = "username";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_POSITION = "position";
    private static final String COLUMN_USER_LOGGED = "is_logged";
    private static final String COLUMN_USER_MONEY = "money";

    public DBHelper(Context context) {
        super(context, DBNAME, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_NAME + " TEXT, " +
                COLUMN_USER_SURNAME + " TEXT, " +
                COLUMN_USER_USERNAME + " TEXT, " +
                COLUMN_USER_EMAIL + " TEXT, " +
                COLUMN_USER_PASSWORD + " TEXT, " +
                COLUMN_USER_POSITION + " TEXT, " +
                COLUMN_USER_LOGGED + " BOOLEAN, " +
                COLUMN_USER_MONEY + " DOUBLE)";

        String createFlightsTable = "CREATE TABLE " + TABLE_FLIGHT + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "departureCountry TEXT, " +
                "departureCity TEXT, " +
                "departureDateTime DATETIME, " +
                "flightForCountry TEXT, " +
                "flightForCity TEXT, " +
                "flightForDateTime DATETIME, " +
                "businessTicket DOUBLE, " +
                "firstClassTicket DOUBLE, " +
                "businessTicketNumber INTEGER, " +
                "firstClassTicketNumber INTEGER)";

        String createTicketsTable = "CREATE TABLE " + TABLE_TICKETS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_user INTEGER, " +
                "id_flight INTEGER, " +
                "ticket_class TEXT, " +
                "departureFrom TEXT, " +
                "flightFor TEXT, " +
                "dateAndTimeDeparture DATETIME, " +
                "check_in_user_name TEXT, " +
                "check_in_user_surname TEXT, " +
                "check_in_user_passport_number TEXT, " +
                "check_in_user_nationality TEXT, " +
                "checkIn BOOLEAN, " +
                "ticketPrice DOUBLE)";

        db.execSQL(createUsersTable);
        db.execSQL(createFlightsTable);
        db.execSQL(createTicketsTable);


        // Thêm dữ liệu mẫu vào bảng flights
        db.execSQL("INSERT INTO flights VALUES (1, 'Hanoi', 'VietNam', '01/01/2025 08:00 AM', 'HoChiMinh', 'SGN', '01/01/2025', 79.99, 169.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (2, 'Hanoi', 'VietNam', '02/01/2025 10:00 AM', 'DaNang', 'DAD', '02/01/2025', 69.99, 159.99, 45, 35)");
        db.execSQL("INSERT INTO flights VALUES (3, 'Hanoi', 'VietNam', '03/01/2025 07:30 AM', 'PhuQuoc', 'PQC', '03/01/2025', 99.99, 199.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (4, 'Hanoi', 'VietNam', '04/01/2025 09:00 AM', 'NhaTrang', 'CXR', '04/01/2025', 79.99, 169.99, 50, 40)");
        db.execSQL("INSERT INTO flights VALUES (5, 'Hanoi', 'VietNam', '05/01/2025 08:45 AM', 'CanTho', 'VCS', '05/01/2025', 69.99, 159.99, 55, 45)");
        db.execSQL("INSERT INTO flights VALUES (6, 'Hanoi', 'VietNam', '06/01/2025 10:15 AM', 'Hue', 'HUI', '06/01/2025', 59.99, 149.99, 40, 30)");
        db.execSQL("INSERT INTO flights VALUES (7, 'HoChiMinh', 'VietNam', '07/01/2025 08:00 AM', 'Hanoi', 'HAN', '07/01/2025', 79.99, 169.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (8, 'HoChiMinh', 'VietNam', '08/01/2025 09:30 AM', 'DaNang', 'DAD', '08/01/2025', 69.99, 159.99, 45, 35)");
        db.execSQL("INSERT INTO flights VALUES (9, 'HoChiMinh', 'VietNam', '09/01/2025 07:15 AM', 'PhuQuoc', 'PQC', '09/01/2025', 99.99, 199.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (10, 'HoChiMinh', 'VietNam', '10/01/2025 08:30 AM', 'NhaTrang', 'CXR', '10/01/2025', 79.99, 169.99, 50, 40)");
        db.execSQL("INSERT INTO flights VALUES (11, 'HoChiMinh', 'VietNam', '11/01/2025 09:00 AM', 'CanTho', 'VCS', '11/01/2025', 69.99, 159.99, 55, 45)");
        db.execSQL("INSERT INTO flights VALUES (12, 'DaNang', 'VietNam', '12/01/2025 08:30 AM', 'Hanoi', 'HAN', '12/01/2025', 69.99, 159.99, 45, 35)");
        db.execSQL("INSERT INTO flights VALUES (13, 'DaNang', 'VietNam', '13/01/2025 09:15 AM', 'HoChiMinh', 'SGN', '13/01/2025', 79.99, 169.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (14, 'DaNang', 'VietNam', '14/01/2025 07:00 AM', 'PhuQuoc', 'PQC', '14/01/2025', 99.99, 199.99, 55, 45)");
        db.execSQL("INSERT INTO flights VALUES (15, 'DaNang', 'VietNam', '15/01/2025 08:45 AM', 'NhaTrang', 'CXR', '15/01/2025', 79.99, 169.99, 50, 40)");
        db.execSQL("INSERT INTO flights VALUES (16, 'DaNang', 'VietNam', '16/01/2025 10:00 AM', 'CanTho', 'VCS', '16/01/2025', 69.99, 159.99, 40, 30)");
        db.execSQL("INSERT INTO flights VALUES (17, 'PhuQuoc', 'VietNam', '17/01/2025 07:30 AM', 'Hanoi', 'HAN', '17/01/2025', 99.99, 199.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (18, 'PhuQuoc', 'VietNam', '18/01/2025 08:15 AM', 'HoChiMinh', 'SGN', '18/01/2025', 99.99, 199.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (19, 'PhuQuoc', 'VietNam', '19/01/2025 09:30 AM', 'DaNang', 'DAD', '19/01/2025', 89.99, 179.99, 50, 40)");
        db.execSQL("INSERT INTO flights VALUES (20, 'PhuQuoc', 'VietNam', '20/01/2025 10:45 AM', 'NhaTrang', 'CXR', '20/01/2025', 89.99, 179.99, 55, 45)");
        db.execSQL("INSERT INTO flights VALUES (21, 'NhaTrang', 'VietNam', '21/01/2025 08:00 AM', 'Hanoi', 'HAN', '21/01/2025', 79.99, 169.99, 50, 40)");
        db.execSQL("INSERT INTO flights VALUES (22, 'NhaTrang', 'VietNam', '22/01/2025 07:30 AM', 'HoChiMinh', 'SGN', '22/01/2025', 79.99, 169.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (23, 'NhaTrang', 'VietNam', '23/01/2025 08:45 AM', 'DaNang', 'DAD', '23/01/2025', 69.99, 159.99, 45, 35)");
        db.execSQL("INSERT INTO flights VALUES (24, 'NhaTrang', 'VietNam', '24/01/2025 09:00 AM', 'PhuQuoc', 'PQC', '24/01/2025', 89.99, 179.99, 55, 45)");
        db.execSQL("INSERT INTO flights VALUES (25, 'NhaTrang', 'VietNam', '25/01/2025 10:30 AM', 'CanTho', 'VCS', '25/01/2025', 69.99, 159.99, 50, 40)");
        db.execSQL("INSERT INTO flights VALUES (26, 'CanTho', 'VietNam', '26/01/2025 07:15 AM', 'Hanoi', 'HAN', '26/01/2025', 69.99, 159.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (27, 'CanTho', 'VietNam', '27/01/2025 08:00 AM', 'HoChiMinh', 'SGN', '27/01/2025', 59.99, 149.99, 55, 45)");
        db.execSQL("INSERT INTO flights VALUES (28, 'CanTho', 'VietNam', '28/01/2025 09:00 AM', 'DaNang', 'DAD', '28/01/2025', 69.99, 159.99, 50, 40)");
        db.execSQL("INSERT INTO flights VALUES (29, 'CanTho', 'VietNam', '29/01/2025 10:15 AM', 'PhuQuoc', 'PQC', '29/01/2025', 79.99, 169.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (30, 'CanTho', 'VietNam', '30/01/2025 08:45 AM', 'NhaTrang', 'CXR', '30/01/2025', 69.99, 159.99, 45, 35)");
        db.execSQL("INSERT INTO flights VALUES (31, 'Hue', 'VietNam', '31/01/2025 07:30 AM', 'Hanoi', 'HAN', '31/01/2025', 59.99, 149.99, 40, 30)");
        db.execSQL("INSERT INTO flights VALUES (32, 'Hue', 'VietNam', '01/02/2025 09:00 AM', 'HoChiMinh', 'SGN', '01/02/2025', 59.99, 149.99, 50, 40)");
        db.execSQL("INSERT INTO flights VALUES (33, 'Hue', 'VietNam', '02/02/2025 08:30 AM', 'DaNang', 'DAD', '02/02/2025', 49.99, 139.99, 45, 35)");
        db.execSQL("INSERT INTO flights VALUES (34, 'Hue', 'VietNam', '03/02/2025 10:15 AM', 'PhuQuoc', 'PQC', '03/02/2025', 79.99, 169.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (35, 'Hue', 'VietNam', '04/02/2025 08:00 AM', 'NhaTrang', 'CXR', '04/02/2025', 69.99, 159.99, 50, 40)");
        db.execSQL("INSERT INTO flights VALUES (36, 'Hue', 'VietNam', '05/02/2025 09:30 AM', 'CanTho', 'VCS', '05/02/2025', 59.99, 149.99, 45, 35)");
        db.execSQL("INSERT INTO flights VALUES (37, 'Hanoi', 'VietNam', '06/02/2025 07:30 AM', 'PhuQuoc', 'PQC', '06/02/2025', 99.99, 199.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (38, 'Hanoi', 'VietNam', '07/02/2025 08:30 AM', 'HoChiMinh', 'SGN', '07/02/2025', 79.99, 169.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (39, 'Hanoi', 'VietNam', '08/02/2025 09:00 AM', 'NhaTrang', 'CXR', '08/02/2025', 79.99, 169.99, 50, 40)");
        db.execSQL("INSERT INTO flights VALUES (40, 'Hanoi', 'VietNam', '09/02/2025 10:30 AM', 'CanTho', 'VCS', '09/02/2025', 69.99, 159.99, 55, 45)");
        db.execSQL("INSERT INTO flights VALUES (41, 'HoChiMinh', 'VietNam', '10/02/2025 07:15 AM', 'Hanoi', 'HAN', '10/02/2025', 79.99, 169.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (42, 'HoChiMinh', 'VietNam', '11/02/2025 08:45 AM', 'PhuQuoc', 'PQC', '11/02/2025', 99.99, 199.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (43, 'HoChiMinh', 'VietNam', '12/02/2025 10:00 AM', 'DaNang', 'DAD', '12/02/2025', 69.99, 159.99, 45, 35)");
        db.execSQL("INSERT INTO flights VALUES (44, 'HoChiMinh', 'VietNam', '13/02/2025 09:30 AM', 'NhaTrang', 'CXR', '13/02/2025', 79.99, 169.99, 50, 40)");
        db.execSQL("INSERT INTO flights VALUES (45, 'HoChiMinh', 'VietNam', '14/02/2025 10:15 AM', 'CanTho', 'VCS', '14/02/2025', 69.99, 159.99, 55, 45)");
        db.execSQL("INSERT INTO flights VALUES (46, 'DaNang', 'VietNam', '15/02/2025 08:00 AM', 'HoChiMinh', 'SGN', '15/02/2025', 79.99, 169.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (47, 'DaNang', 'VietNam', '16/02/2025 09:00 AM', 'Hanoi', 'HAN', '16/02/2025', 69.99, 159.99, 45, 35)");
        db.execSQL("INSERT INTO flights VALUES (48, 'DaNang', 'VietNam', '17/02/2025 10:00 AM', 'PhuQuoc', 'PQC', '17/02/2025', 99.99, 199.99, 60, 50)");
        db.execSQL("INSERT INTO flights VALUES (49, 'DaNang', 'VietNam', '18/02/2025 07:15 AM', 'CanTho', 'VCS', '18/02/2025', 69.99, 159.99, 55, 45)");
        db.execSQL("INSERT INTO flights VALUES (50, 'DaNang', 'VietNam', '19/02/2025 08:30 AM', 'NhaTrang', 'CXR', '19/02/2025', 79.99, 169.99, 50, 40)");




        // Thêm dữ liệu mẫu vào bảng ticketsReserved
        db.execSQL("INSERT INTO ticketsReserved VALUES (1, 11, 1, 'firstClassTicket', 'TPHCM, TSN', 'DaNang, DaNang', '11/23/2024', 'Huyen', 'Tran', '123456', 'Vietnam', 1, 90.0)");
        db.execSQL("INSERT INTO ticketsReserved VALUES (2, 11, 1, 'firstClassTicket', 'TPHCM, TSN', 'DaNang, DaNang', '11/23/2024', 'Hanh', 'Tran', '454545', 'Vietnam', 1, 90.0)");
        db.execSQL("INSERT INTO ticketsReserved VALUES (3, 11, 13, 'firstClassTicket', 'QuangNinh, VanDon', 'TPHCM, LongThanh', '11/30/2024', NULL, NULL, NULL, NULL, 0, 129.99)");
        db.execSQL("INSERT INTO ticketsReserved VALUES (4, 11, 24, 'businessTicket', 'TPHCM, TSN', 'NgheAn, Vinh', '11/28/2024', NULL, NULL, NULL, NULL, 0, 49.99)");
        db.execSQL("INSERT INTO ticketsReserved VALUES (5, 12, 1, 'businessTicket', 'TPHCM, TSN', 'DaNang, DaNang', '11/23/2024', NULL, NULL, NULL, NULL, 0, 49.99)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLIGHT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKETS);
        onCreate(db);
    }


    // Insert flight
    public boolean insertDataFlight(String departureCountry, String departureCity, String departureDateTime, String flightForCountry, String flightForCity, String flightForDateTime, Double businessTicket, Double firstClassTicket, int businessTicketNumber, int firstClassTicketNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("departureCountry", departureCountry);
        values.put("departureCity", departureCity);
        values.put("departureDateTime", departureDateTime);
        values.put("flightForCountry", flightForCountry);
        values.put("flightForCity", flightForCity);
        values.put("flightForDateTime", flightForDateTime);
        values.put("businessTicket", businessTicket);
        values.put("firstClassTicket", firstClassTicket);
        values.put("businessTicketNumber", businessTicketNumber);
        values.put("firstClassTicketNumber", firstClassTicketNumber);
        long result = db.insert(TABLE_FLIGHT, null, values);
        db.close();
        return result != -1;
    }

    // Hàm lấy thông tin chi tiết vé đặt chỗ
    @SuppressLint("Range")
    public TicketReservation getTicketReservationData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        TicketReservation ticketReservation = null;

        try {
            cursor = db.rawQuery("SELECT * FROM ticketsReserved WHERE id = ?", new String[]{String.valueOf(id)});
            if (cursor.moveToFirst()) {
                ticketReservation = new TicketReservation();
                ticketReservation.setTicketReservationID(cursor.getInt(cursor.getColumnIndex("id")));
                ticketReservation.setTicketReservationIDUser(cursor.getInt(cursor.getColumnIndex("id_user")));
                ticketReservation.setTicketReservationIDFlight(cursor.getInt(cursor.getColumnIndex("id_flight")));
                ticketReservation.setTicketReservationTicketClass(cursor.getString(cursor.getColumnIndex("ticket_class")));
                ticketReservation.setTicketReservationDepartureFrom(cursor.getString(cursor.getColumnIndex("departureFrom")));
                ticketReservation.setTicketReservationFlightFor(cursor.getString(cursor.getColumnIndex("flightFor")));
                ticketReservation.setTicketReservationDepartureDateAndTime(cursor.getString(cursor.getColumnIndex("dateAndTimeDeparture")));
                ticketReservation.setTicketReservationCheckInUserName(cursor.getString(cursor.getColumnIndex("check_in_user_name")));
                ticketReservation.setTicketReservationCheckInUserSurname(cursor.getString(cursor.getColumnIndex("check_in_user_surname")));
                ticketReservation.setTicketReservationCheckInUserPassportNumber(cursor.getString(cursor.getColumnIndex("check_in_user_passport_number")));
                ticketReservation.setTicketReservationCheckInUserNationality(cursor.getString(cursor.getColumnIndex("check_in_user_nationality")));
                ticketReservation.setTicketReservationCheckIn(cursor.getInt(cursor.getColumnIndex("checkIn")) > 0);
                ticketReservation.setTicketPrice(cursor.getDouble(cursor.getColumnIndex("ticketPrice")));
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error in getTicketReservationData", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        }

        return ticketReservation;
    }

    // Hàm xóa vé đặt chỗ
    public void deleteReservationTicket(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete("ticketsReserved", "id = ?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e("DBHelper", "Lỗi xóa vé đặt chỗ\n", e);
        } finally {
            db.close();
        }
    }

    // Hàm cập nhật số tiền của người dùng
    public void updateUserMoney(int userId, double newMoney) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("money", newMoney);

        try {
            db.update("users", values, "id = ?", new String[]{String.valueOf(userId)});
        } catch (Exception e) {
            Log.e("DBHelper", "Lỗi cập nhật tiền của người dùng\n", e);
        } finally {
            db.close();
        }
    }
    // Insert ticket reservation
    public boolean insertDataTicketReservation(int id_user, int id_flight, String ticket_class, String departureFrom, String flightFor, String dateAndTimeDeparture, String check_in_user_name, String check_in_user_surname, String check_in_user_passport_number, String check_in_user_nationality, boolean checkIn, double ticketPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id_user", id_user);
        values.put("id_flight", id_flight);
        values.put("ticket_class", ticket_class);
        values.put("departureFrom", departureFrom);
        values.put("flightFor", flightFor);
        values.put("dateAndTimeDeparture", dateAndTimeDeparture);
        values.put("check_in_user_name", check_in_user_name);
        values.put("check_in_user_surname", check_in_user_surname);
        values.put("check_in_user_passport_number", check_in_user_passport_number);
        values.put("check_in_user_nationality", check_in_user_nationality);
        values.put("checkIn", checkIn);
        values.put("ticketPrice", ticketPrice);
        long result = db.insert(TABLE_TICKETS, null, values);
        db.close();
        return result != -1;
    }

    // Login user
    public User setLoginUserData(String username, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        User user = null;

        try {
            db = this.getReadableDatabase();
            String query = "SELECT * FROM " + TABLE_USER + " WHERE username = ? AND password = ?";
            cursor = db.rawQuery(query, new String[]{username, password});
            if (cursor.moveToFirst()) {
                user = mapCursorToUser(cursor);
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error in setLoginUserData", e);
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return user;
    }

    // Map cursor to user object
    @SuppressLint("Range")
    private User mapCursorToUser(Cursor cursor) {
        User user = new User();
        user.setUserId(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID)));
        user.setUserName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
        user.setUserSurname(cursor.getString(cursor.getColumnIndex(COLUMN_USER_SURNAME)));
        user.setUserUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USER_USERNAME)));
        user.setUserEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
        user.setUserPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
        user.setUserPosition(cursor.getString(cursor.getColumnIndex(COLUMN_USER_POSITION)));
        user.setIsLogged(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_LOGGED)) > 0);
        user.setUserMoney(cursor.getDouble(cursor.getColumnIndex(COLUMN_USER_MONEY)));
        return user;
    }

    // Check if username exists
    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE username = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Check username and password
    public boolean checkUsernameAndPassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE username = ? AND password = ?", new String[]{username, password});
        boolean valid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return valid;
    }

    // Delete user
    public void deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }



    public Boolean insertData(String name, String surname, String username, String email, String password, String position, boolean is_logged, double money) {
        SQLiteDatabase MYDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Log.d("NameAPI", name + " " + surname + " " + username + " " + email + " " + password + " " + position + " " + is_logged + " " + money);
        contentValues.put("name", name);
        contentValues.put("surname", surname);
        contentValues.put("username", username);
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("position", position);
        contentValues.put("is_logged", is_logged);
        contentValues.put("money", money);
        long result = MYDB.insert("users", null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }


    public void updateUserInfo(int id, String username, String name, String surname, String email, String password, String position, boolean is_logged, double money) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("surname", surname);
        contentValues.put("username", username);
        contentValues.put("email", email);
        contentValues.put("password", password);
        contentValues.put("position", position);
        contentValues.put("is_logged", is_logged);
        contentValues.put("money", money);
        MyDB.update("users", contentValues, "id = ?", new String[]{String.valueOf(id)});
        MyDB.close();
    }

    public void updateUserIsLogged(int id, boolean is_logged) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("is_logged", is_logged);
        MyDB.update("users", contentValues, "id = ?", new String[]{String.valueOf(id)});
        MyDB.close();
    }

    public void updateFlightInfo(int id, String departureCountry, String departureCity, String departureDateTime, String flightForCountry, String flightForCity, String flightForDateTime, Double businessTicket, Double firstClassTicket, int businessTicketNumber, int firstClassTicketNumber) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("departureCountry", departureCountry);
        contentValues.put("departureCity", departureCity);
        contentValues.put("departureDateTime", departureDateTime);
        contentValues.put("flightForCountry", flightForCountry);
        contentValues.put("flightForCity", flightForCity);
        contentValues.put("flightForDateTime", flightForDateTime);
        contentValues.put("businessTicket", businessTicket);
        contentValues.put("firstClassTicket", firstClassTicket);
        contentValues.put("businessTicketNumber", businessTicketNumber);
        contentValues.put("firstClassTicketNumber", firstClassTicketNumber);
        MyDB.update("flights", contentValues, "id = ?", new String[]{String.valueOf(id)});
        MyDB.close();
    }

    public void updateTicketReservationInfo(int id, String check_in_user_name, String check_in_user_surname, String check_in_user_passport_number, String check_in_user_nationality, boolean checkIn) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("check_in_user_name", check_in_user_name);
        contentValues.put("check_in_user_surname", check_in_user_surname);
        contentValues.put("check_in_user_passport_number", check_in_user_passport_number);
        contentValues.put("check_in_user_nationality", check_in_user_nationality);
        contentValues.put("checkIn", checkIn);
        MyDB.update("ticketsReserved", contentValues, "id = ?", new String[]{String.valueOf(id)});
        MyDB.close();
    }

    public void updateBusinessTicketNumber(int id, int numberTicket) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("businessTicketNumber", numberTicket);
        MyDB.update("flights", contentValues, "id = ?", new String[]{String.valueOf(id)});
        MyDB.close();
    }

    public void updateFirstClassTicketNumber(int id, int numberTicket) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("firstClassTicketNumber", numberTicket);
        MyDB.update("flights", contentValues, "id = ?", new String[]{String.valueOf(id)});
        MyDB.close();
    }



    public void activateAdmin(String username) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("position", "admin");
        MyDB.update("users", contentValues, "username = ?", new String[]{username});
        MyDB.close();
    }

    public void deleteFlight(int id){
        SQLiteDatabase MyDB = getWritableDatabase();
        String sql = "DELETE FROM flights WHERE id=?";
        SQLiteStatement statement = MyDB.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1, (double)id);
        statement.execute();
        MyDB.close();
    }


    public Cursor getData(String sql){
        SQLiteDatabase MyDB = this.getReadableDatabase();
        return MyDB.rawQuery(sql, null);
    }

    public void queryData(String sql){
        SQLiteDatabase MyDB = getWritableDatabase();
        MyDB.execSQL(sql);
    }

    public User getUserData(int id){
        User user = new User();
        try{
            SQLiteDatabase MyDB = this.getReadableDatabase();
            Cursor cursor = MyDB.rawQuery("Select * from users where id = ?", new String[]{String.valueOf(id)});
            if(cursor.moveToFirst()){
                user = new User();
                user.setUserId(cursor.getInt(0));
                user.setUserName(cursor.getString(1));
                user.setUserSurname(cursor.getString(2));
                user.setUserUsername(cursor.getString(3));
                user.setUserEmail(cursor.getString(4));
                user.setUserPassword(cursor.getString(5));
                user.setUserPosition(cursor.getString(6));
                user.setIsLogged(cursor.getInt(7) > 0);
                user.setUserMoney(cursor.getDouble(8));
            }
        } catch (Exception e){
            user = null;
        }
        return user;
    }

    public Flights getFlightsData(int id){
        Flights flights = new Flights();
        try{
            SQLiteDatabase MyDB = this.getReadableDatabase();
            Cursor cursor = MyDB.rawQuery("Select * from flights where id = ?", new String[]{String.valueOf(id)});
            if(cursor.moveToFirst()){
                flights = new Flights();
                flights.set_id(cursor.getInt(0));
                flights.setFlightDepartureCountry(cursor.getString(1));
                flights.setFlightDepartureCity(cursor.getString(2));
                flights.setFlightDepartureDateAndTime(cursor.getString(3));
                flights.setFlightForCountry(cursor.getString(4));
                flights.setFlightForCity(cursor.getString(5));
                flights.setFlightForDateAndTime(cursor.getString(6));
                flights.setFlightBusinessTicket(cursor.getDouble(7));
                flights.setFlightFirstClassTicket(cursor.getDouble(8));
                flights.setFlightBusinessTicketNumber(cursor.getInt(9));
                flights.setFlightFirstClassTicketNumber(cursor.getInt(10));
            }
        } catch (Exception e){
            flights = null;
        }
        return flights;
    }

}
