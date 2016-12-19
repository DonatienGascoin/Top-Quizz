package cpe.top.quizz.beans;

import android.util.Log;

import java.util.Objects;

/**
 *
 * @author Donatien
 * @since 10/11/2016
 * @version 0.1
 */

public class ReturnObject {
    private ReturnCode code;

    private Object object;

    public ReturnCode getCode() {
        return code;
    }

    public void setCode(ReturnCode code) {
        this.code = code;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
