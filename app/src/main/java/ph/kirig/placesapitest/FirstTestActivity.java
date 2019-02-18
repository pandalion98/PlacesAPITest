package ph.kirig.placesapitest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

public class FirstTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_test);

        Places.initialize(this, getString(R.string.places_api_key)); // Always initialize first
        PlacesClient placesClient = Places.createClient(this); // Object used for queries

        DelayAutoCompleteTextView mAddressBox = findViewById(R.id.edittext_address);
        mAddressBox.setAdapter(new PlacesAutoCompleteAdapter(this, placesClient));
        mAddressBox.setAutoCompleteDelay(80);
    }
}
