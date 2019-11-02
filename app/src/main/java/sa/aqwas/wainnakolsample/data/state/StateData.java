package sa.aqwas.wainnakolsample.data.state;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StateData<T> {
    @NonNull
    private DataStatus status;

    @Nullable
    private T data;

    @Nullable
    private ErrorObject error;

    public StateData() {
        this.status = DataStatus.LOADING;
        this.data = null;
        this.error = null;
    }

    public StateData<T> loading() {
        this.status = DataStatus.LOADING;
        this.data = null;
        this.error = null;
        return this;
    }

    public StateData<T> success(@NonNull T data) {
        this.status = DataStatus.SUCCESS;
        this.data = data;
        this.error = null;
        return this;
    }

    public StateData<T> error(@NonNull ErrorObject error) {
        this.status = DataStatus.ERROR;
        this.data = null;
        this.error = error;
        return this;
    }

    @NonNull
    public DataStatus getStatus() {
        return status;
    }

    @Nullable
    public T getData() {
        return data;
    }

    @Nullable
    public ErrorObject getError() {
        return error;
    }

    public enum DataStatus {
        SUCCESS,
        ERROR,
        LOADING
    }
}
