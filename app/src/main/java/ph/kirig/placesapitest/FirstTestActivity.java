package ph.kirig.placesapitest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

public class FirstTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_test);

        Places.initialize(this, getString(R.string.places_api_key)); // Always initialize first
        PlacesClient placesClient = Places.createClient(this);


        PlacesAutoCompleteAdapter adapter = new PlacesAutoCompleteAdapter(this, placesClient);

        // I suggest updating this using a LocationCallback
        adapter.setCurrentLatLng(new LatLng(14.4200761, 120.8592142));

        DelayAutoCompleteTextView mAddressBox = findViewById(R.id.edittext_address);
        mAddressBox.setAdapter(adapter);
        mAddressBox.setAutoCompleteDelay(80);

        ProgressBar loadIndicator = findViewById(R.id.progress_indicator_home);
        mAddressBox.setLoadingIndicator(loadIndicator);
    }
}
