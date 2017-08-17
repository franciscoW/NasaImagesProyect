package com.pachirrys.nasaproyect;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by pachirrys on 11/08/2017.
 */

public class SingletonVolley {

    private static SingletonVolley singleInstance;
    private RequestQueue mRequestQueue;
    private static Context appContext;

    private SingletonVolley(Context context){
        appContext      = context ;
        mRequestQueue   = getRequestQueue();
    }//end constructor

    public static synchronized  SingletonVolley getInstance( Context context){

        if ( singleInstance == null){
            singleInstance = new SingletonVolley( context);
        }
        return singleInstance;
    }//end getInstance method

    public RequestQueue getRequestQueue(){

        if ( mRequestQueue == null ){
            mRequestQueue = Volley.newRequestQueue(appContext.getApplicationContext());
        }
        return mRequestQueue;
    }//end getRequestQueue method

    public <T> void  addToRequestQueue(Request<T> req){
        getRequestQueue().add(req);
    }//end addToRequestQueue method

}//end SingletonVolley class
