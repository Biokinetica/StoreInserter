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
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
    
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }


    @FXML
    private void handleButtonAction(MouseEvent event) {
       

        BasicDBObject storeInfo = new BasicDBObject();
        DBCollection colls = mongoClient.getDB("project").getCollection("Stores");
        
        storeInfo.put("Store", StoreLine.getText());
        storeInfo.append("Address", AddrLine.getText())
                .append("City", CityLine.getText())
                .append("Zip", ZipLine.getText())
                .append("Phone", PhoneLine.getText())
                .append("Contributer", user);
        
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
            System.out.println(ex.getCause());
        }
        
    }

    @FXML
    private void handleLogin(MouseEvent event) {
        
        
        try {
                address = new ServerAddress("ec2-54-82-163-131.compute-1.amazonaws.com",27017);
            } catch (UnknownHostException ex) {
                System.out.println(ex.getCause());
            }
            MongoCredential creds = MongoCredential.createMongoCRCredential(Username.getText(), "project", Password.getText().toCharArray());
            mongoClient = new MongoClient(address, Arrays.asList(creds));
            
            user = Username.getText();
            Username.clear();
            Password.clear();
    }
}