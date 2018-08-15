package com.luciayanicelli.icsalud.Api_Json;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.luciayanicelli.icsalud.Activity_Configuracion.Configuraciones;
import com.luciayanicelli.icsalud.DataBase.LoginContract;
import com.luciayanicelli.icsalud.DataBase.Login_DBHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LuciaYanicelli on 16/11/2017.
 */

public class Patients {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String birthday;
    private String password;
    private String gender;
    private String occupation;
    private String educationLevel;
    private int coexistence;
    private String lastHospitalizedAt;
    private String diagnosedAt;
    private String healthInsurance;

    private String _ID = JSON_CONSTANTS.ID;
    private String _FIRST_NAME = JSON_CONSTANTS.FIRST_NAME;
    private String _LAST_NAME = JSON_CONSTANTS.LAST_NAME;
    private String _EMAIL = JSON_CONSTANTS.EMAIL;
    private String _BIRTHDAY = JSON_CONSTANTS.BIRTHDAY;
    private String _GENDER = JSON_CONSTANTS.GENDER;
    private String _OCCUPATION = JSON_CONSTANTS.OCCUPATION;
    private String _EDUCATION_LEVEL = JSON_CONSTANTS.EDUCATION_LEVEL;
    private String _COEXISTENCE = JSON_CONSTANTS.COEXISTENCE;
    private String _LAST_HOSPITALIZED_AT = JSON_CONSTANTS.LAST_HOSPITALIZED_AT;
    private String _HEALTH_INSURANCE = JSON_CONSTANTS.HEALTH_INSURANCE;
    private String _DIAGNOSED_AT = JSON_CONSTANTS.DIAGNOSED_AT;


    public Patients(){
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public void setCoexistence(int coexistence) {
        this.coexistence = coexistence;
    }

    public void setLastHospitalizedAt(String lastHospitalizedAt) {  this.lastHospitalizedAt = lastHospitalizedAt;  }

    public void setHealthInsurance(String healthInsurance) {  this.healthInsurance = healthInsurance;    }

    public String getDiagnosedAt() {
        return diagnosedAt;
    }

    public void setDiagnosedAt(String diagnosedAt) {
        this.diagnosedAt = diagnosedAt;
    }


    public String getGender() {  return gender;    }

    public String getOccupation() {  return occupation;    }

    public String getEducationLevel() {   return educationLevel;    }

    public int getCoexistence() {    return coexistence;    }

    public String getLastHospitalizedAt() {    return lastHospitalizedAt;    }

    public String getHealthInsurance() {    return healthInsurance;    }


    //Para crear un paciente a partir de un jsonString obtenido de una búsqueda y guardarlo en configuraciones
    public void cargarPatient(Context context, String jsonString) {

        try {

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArr = jsonObject.getJSONArray(JSON_CONSTANTS.RESPONSE_DATA);

            final int numRows = jsonArr.length();

            Patients patients = new Patients();

            for (int x = 0; x < numRows; x++) {
                final JSONObject row = jsonArr.getJSONObject(x);

                patients.setId(row.getString(_ID));
                patients.setFirstName(row.getString(_FIRST_NAME));
                patients.setLastName(row.getString(_LAST_NAME));
                patients.setBirthday(row.getString(_BIRTHDAY));
                patients.setEmail(row.getString(_EMAIL));
                patients.setGender(row.getString(_GENDER));
                patients.setOccupation(row.getString(_OCCUPATION));
                patients.setEducationLevel(row.getString(_EDUCATION_LEVEL));
                patients.setCoexistence(row.getInt(_COEXISTENCE));
                patients.setLastHospitalizedAt(row.getString(_LAST_HOSPITALIZED_AT));
                patients.setHealthInsurance(row.getString(_HEALTH_INSURANCE));
                patients.setDiagnosedAt(row.getString(_DIAGNOSED_AT));

            }

            //Guarda datos en configuraciones
            Configuraciones configuraciones = new Configuraciones(context);

            configuraciones.setID(patients.getId());
            configuraciones.setUserName(patients.getFirstName());
            configuraciones.setUserSurname(patients.getLastName());
            configuraciones.setUserBirthday(patients.getBirthday());
            configuraciones.setUserEmail(patients.getEmail());
            configuraciones.setGender(patients.getGender());
            configuraciones.setEducationLevel(patients.getEducationLevel());
            configuraciones.setOccupation(patients.getOccupation());
            configuraciones.setLastHospitalization(patients.getLastHospitalizedAt());
            configuraciones.setHealthInsurance(patients.getHealthInsurance());
            configuraciones.setCoexistence(patients.getCoexistence());
            configuraciones.setDiagnosedAt(patients.getDiagnosedAt());

            //Guardar datos en DataBase
            Login_DBHelper loginDbHelper = new Login_DBHelper(context);
            SQLiteDatabase db = loginDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(LoginContract.LoginEntry.EMAIL, configuraciones.getUserEmail());
            values.put(LoginContract.LoginEntry.FIRST_NAME, configuraciones.getUserName());
            values.put(LoginContract.LoginEntry.LAST_NAME, configuraciones.getUserSurname());
            values.put(LoginContract.LoginEntry.PASSWORD, configuraciones.getUserPassword());
            values.put(LoginContract.LoginEntry.ID_USER_WEB_SERVICE, configuraciones.getID());

            long mLong = db.insert(LoginContract.LoginEntry.TABLE_NAME_LOGIN, null, values);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



}
