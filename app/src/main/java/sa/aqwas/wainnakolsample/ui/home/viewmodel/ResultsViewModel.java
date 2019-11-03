package sa.aqwas.wainnakolsample.ui.home.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import sa.aqwas.wainnakolsample.data.db.entities.Restaurant;
import sa.aqwas.wainnakolsample.data.repository.RestaurantRepository;
import sa.aqwas.wainnakolsample.data.state.StateLiveData;

public class ResultsViewModel extends AndroidViewModel {
    private static final String TAG = ResultsViewModel.class.getSimpleName();

    private StateLiveData<Restaurant> restaurantObservable;

    public ResultsViewModel(Application application) {
        super(application);

        restaurantObservable = new StateLiveData<>();

        //listObservable.addSource(DoctorsRepository.getInstance().getDoctorAppointments(0, ""), listObservable::setValue);
    }

    /**
     * Expose the LiveData Projects query so the UI can observe it.
     */
    public StateLiveData<Restaurant> getRestaurantObservable() {
        return restaurantObservable;
    }

    /**
     * Expose the LiveData Projects query so the UI can observe it.
     */
    public void loadRestaurant(double latitude, double longitude) {
        RestaurantRepository.getInstance().getRestaurant(latitude, longitude, restaurantObservable);
    }
}
