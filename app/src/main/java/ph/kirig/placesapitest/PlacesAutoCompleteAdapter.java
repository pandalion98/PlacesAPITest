package ph.kirig.placesapitest;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Gene on 18/02/2019.
 * Kirig Technologies
 * gene(at)kirig.ph
 */
public class PlacesAutoCompleteAdapter
        extends ArrayAdapter<AutocompletePrediction> implements Filterable {
    private static int QUERY_TIMEOUT_SEC = 30;

    // Locations within this will be preferred.
    private static int BIAS_RADIUS_METERS = 5000;
    private static String COUNTRY_CODE = "PH";

    private List<AutocompletePrediction> mResultList = new ArrayList<>();
    private PlacesClient placesClient;
    private LatLng currentLatLng;

    public PlacesAutoCompleteAdapter(@NonNull Context context, PlacesClient placesClient) {
        // TODO: Change layout if needed
        super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
        this.placesClient = placesClient;
    }

    // Fairly self-explanatory.
    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Override
    public AutocompletePrediction getItem(int position) {
        return mResultList.get(position);
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = super.getView(position, convertView, parent);
        AutocompletePrediction item = getItem(position);

        TextView textView1 = row.findViewById(android.R.id.text1);
        TextView textView2 = row.findViewById(android.R.id.text2);

        if (item != null) {
            // TODO: Set character style as needed
            textView1.setText(item.getPrimaryText(null));
            textView2.setText(item.getSecondaryText(null));
        }

        return row;
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence searchQuery) {
                FilterResults results = new FilterResults();

                if (searchQuery != null) {
                    // Query the Places Autocomplete API for the search string
                    List<AutocompletePrediction> filterData = getAutocomplete(searchQuery);
                    results.values = filterData;

                    if (filterData != null) {
                        results.count = filterData.size();
                    } else {
                        results.count = 0;
                    }
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // Check if we have results, then act appropriately
                if (results != null && results.values != null) {
                    mResultList = (List<AutocompletePrediction>) results.values;
                    notifyDataSetChanged();
                } else {
                    mResultList.clear();
                    notifyDataSetInvalidated();
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                // Override this method to display a readable result in the AutocompleteTextView
                // when clicked.
                if (resultValue instanceof AutocompletePrediction) {
                    return ((AutocompletePrediction) resultValue).getFullText(null);
                } else {
                    return super.convertResultToString(resultValue);
                }
            }
        };
    }


    /**
     * Submits an autocomplete query to the Places Autocomplete API.
     */
    private List<AutocompletePrediction> getAutocomplete(CharSequence searchQuery) {
        FindAutocompletePredictionsRequest.Builder f = FindAutocompletePredictionsRequest.builder();
        f.setQuery(searchQuery.toString());

        if (COUNTRY_CODE != null) {
            f.setCountry(COUNTRY_CODE);
        }

        if (currentLatLng != null) {
            f.setLocationBias(RectangularBounds.newInstance(
                    toBounds(currentLatLng, BIAS_RADIUS_METERS)));
        }

        FindAutocompletePredictionsResponse results = null;
        try {
            // Block on a task and get the result synchronously. This is generally done
            // when executing a task inside a separately managed background thread. Doing this
            // on the main (UI) thread can cause your application to become unresponsive.
            results =
                    Tasks.await(placesClient
                            .findAutocompletePredictions(f.build()), QUERY_TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException ignored) {
            // Something in the task threw an exception or it got interrupted, or it timed out
        }

        if (results != null) {
            return results.getAutocompletePredictions();
        }

        return null;
    }


    /**
     * Used for bias.
     * Locations near this would be preferred, used together with BIAS_RADIUS_METERS.
     * <p>
     * Pass null to remove bias.
     */
    public void setCurrentLatLng(LatLng latLng) {
        currentLatLng = latLng;
    }

    /**
     * https://stackoverflow.com/q/15319431/3979290
     */
    private LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }
}
