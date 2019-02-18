package ph.kirig.placesapitest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class FirstTestActivity extends AppCompatActivity {

    private DelayAutoCompleteTextView mAddressBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_test);

        mAddressBox = findViewById(R.id.edittext_address);
    }
}
