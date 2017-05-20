package com.zar.weupdateyou.service;
import com.zar.weupdateyou.doc.ENUM;

/**
 * Created by KJS on 11/15/2016.
 */
public interface ServiceListener {
    public void OnSuccess(Object response, ENUM.SERVICE_TYPE type);

    public void OnError(String ErrorMsg, ENUM.SERVICE_TYPE type);

    public void OnFaild(ENUM.SERVICE_TYPE type);

    public void OnFinished(ENUM.SERVICE_TYPE type);
}
