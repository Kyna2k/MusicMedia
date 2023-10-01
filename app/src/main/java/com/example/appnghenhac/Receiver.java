package com.example.appnghenhac;

import static com.example.appnghenhac.services.MusicServices.ACTION_BACK;
import static com.example.appnghenhac.services.MusicServices.ACTION_NEXT;
import static com.example.appnghenhac.services.MusicServices.ACTION_PAUSE;
import static com.example.appnghenhac.services.MusicServices.ACTION_RESUME;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.appnghenhac.services.MusicServices;

@RequiresApi(api = Build.VERSION_CODES.S)
public class Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, MusicServices.class);
        int action = intent.getIntExtra("SVACTION",-1);
        switch (action)
        {
            case ACTION_PAUSE :
                intent1.putExtra("action", ACTION_PAUSE);
                context.startService(intent1);
                break;
            case ACTION_RESUME :
                intent1.putExtra("action", ACTION_RESUME);
                context.startService(intent1);
                break;
            case ACTION_NEXT:
                intent1.putExtra("action", ACTION_NEXT);
                context.startService(intent1);
                break;
            case ACTION_BACK:
                intent1.putExtra("action", ACTION_BACK);
                context.startService(intent1);
                break;
        }
    }
}
