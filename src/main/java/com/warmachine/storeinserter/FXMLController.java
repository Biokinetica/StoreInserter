package com.warmachine.storeinserter;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.warmachine.storeinserter.TextFieldLimited.TextFieldLimited;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class FXMLController implements Initializable {

    @FXML
    private TextField StoreLine;
    @FXML
    private TextField AddrLine;
    @FXML
    private TextField CityLine;
    @FXML
    private TextField ZipLine;
    @FXML
    private TextField PhoneLine;
    @FXML
    private Button InsertButton;
    @FXML
    private TextField Username;
    @FXML
    private PasswordField Password;
    @FXML
    private Button LoginButton;
        
    private MongoClient mongoClient;
    private ServerAddress address;
    private String user;
    @FXML
    private CheckBox monCheck;
    @FXML
    private CheckBox tueCheck;
    @FXML
    private CheckBox wedCheck;
    @FXML
    private CheckBox thursCheck;
    @FXML
    private CheckBox friCheck;
    @FXML
    private CheckBox satCheck;
    @FXML
    private CheckBox sunCheck;
    @FXML
    private TextFieldLimited openTime;
    @FXML
    private TextFieldLimited closeTime;
    @FXML
    private Button update;
    
    private BasicDBObject storeInfo;
    
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        storeInfo = new BasicDBObject();
        openTime.setMaxlength(5);
        closeTime.setMaxlength(5);
    }


    @FXML
    private void handleButtonAction(MouseEvent event) {
       
        
        DBCollection colls = mongoClient.getDB("project").getCollection("Stores");
        
        storeInfo.put("Store", StoreLine.getText());
        storeInfo.append("Address", AddrLine.getText())
                .append("City", CityLine.getText())
                .append("Zip", ZipLine.getText())
                .append("Phone", PhoneLine.getText())
                .append("Contributor", user);
        
        BasicDBObject location = new BasicDBObject("type","Point");
               
        try {
            double coordinates[] = new double[2];
            
            final Geocoder geocoder = new Geocoder();
GeocoderRequest geocoderRequest = new GeocoderRequestBuilder().setAddress(AddrLine.getText() + " " + CityLine.getText() + ", MI " + ZipLine.getText() ).setLanguage("en").getGeocoderRequest();
GeocodeResponse geocoderResponse = geocoder.geocode(geocoderRequest);

            coordinates[0] = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLng().doubleValue();
            coordinates[1] = geocoderResponse.getResults().get(0).getGeometry().getLocation().getLat().doubleValue();
           
            location.append("coordinates", coordinates);
            
            storeInfo.append("loc", location);
                        
            colls.insert(storeInfo);
            

            for (GeocoderResult e : geocoderResponse.getResults()){
                
               System.out.println(e.getFormattedAddress());
               System.out.println(e.getGeometry().getLocation().getLat());
               System.out.println(e.getGeometry().getLocation().getLng());
            }
            
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
        
    }

    @FXML
    private void handleLogin(MouseEvent event) {
        
        try {
                address = new ServerAddress("ec2-54-82-163-131.compute-1.amazonaws.com",27017);
            } catch (UnknownHostException ex) {
                System.out.println(ex.getLocalizedMessage());
            }
            MongoCredential creds = MongoCredential.createMongoCRCredential(Username.getText(), "project", Password.getText().toCharArray());
            mongoClient = new MongoClient(address, Arrays.asList(creds));
            
            user = Username.getText();
            Username.clear();
            Password.clear();
    }

    @FXML
    private void enterLogin(KeyEvent event) {
        MouseEvent m = null;
        if(event.getCode() == KeyCode.ENTER)
            handleLogin(m);
    }

    @FXML
    private void updateTime(MouseEvent event) throws ParseException {
        
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date open = sdf.parse(openTime.getText());
            Date close = sdf.parse(closeTime.getText());
            Calendar OpeningTime = GregorianCalendar.getInstance(TimeZone.getTimeZone("EST"));
            Calendar ClosingTime = GregorianCalendar.getInstance(TimeZone.getTimeZone("EST"));
            OpeningTime.setTime(open);
            ClosingTime.setTime(close);
            
            
            String timeArray[] = new String[2];
            
            
            
            if(OpeningTime.get(Calendar.MINUTE) == 0)
            timeArray[0] = OpeningTime.get(Calendar.HOUR_OF_DAY) + ":00";
            else
                timeArray[0] = OpeningTime.get(Calendar.HOUR_OF_DAY) + ":" + OpeningTime.get(Calendar.MINUTE);
            
            if(ClosingTime.get(Calendar.MINUTE) == 0)
            timeArray[1] = ClosingTime.get(Calendar.HOUR_OF_DAY) +":00";
            else
                timeArray[1] = ClosingTime.get(Calendar.HOUR_OF_DAY) + ":" + ClosingTime.get(Calendar.MINUTE);
            
            List<CheckBox> checkBoxes = new ArrayList<>();
            checkBoxes.add(monCheck);
            checkBoxes.add(tueCheck);
            checkBoxes.add(wedCheck);
            checkBoxes.add(thursCheck);
            checkBoxes.add(friCheck);
            checkBoxes.add(satCheck);
            checkBoxes.add(sunCheck);
        
        for(CheckBox c : checkBoxes)
            if(c.isSelected()){
        storeInfo.remove(c.getText());
        storeInfo.put(c.getText(), timeArray);
            }
        
    }
}