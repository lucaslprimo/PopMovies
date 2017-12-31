package br.com.lucaslprimo.popmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Lucas Primo on 30-Dec-17.
 */

public class MoviesIntentService extends IntentService {
    public MoviesIntentService() {
        super("MoviesIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(intent!=null)
        {
            String action = intent.getAction();

            MovieTask.executeTask(action, intent, getApplicationContext());
        }


    }
}
