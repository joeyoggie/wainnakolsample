package sa.aqwas.wainnakolsample.data.state;

import androidx.lifecycle.MutableLiveData;

public class StateLiveData<T> extends MutableLiveData<StateData<T>> {

    /**
     * Use this to put the Data on a LOADING Status
     */
    public void setLoading() {
        postValue(new StateData<T>().loading());
    }

    /**
     * Use this to put the Data on a SUCCESS DataStatus
     * @param data
     */
    public void setSuccess(T data) {
        postValue(new StateData<T>().success(data));
    }

    /**
     * Use this to put the Data on a ERROR DataStatus
     * @param throwable the error to be handled
     */
    public void setError(ErrorObject throwable) {
        postValue(new StateData<T>().error(throwable));
    }
}